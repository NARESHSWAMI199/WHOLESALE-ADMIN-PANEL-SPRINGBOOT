package com.sales.admin.services;


import com.sales.dto.WalletTransactionDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.StoreNotifications;
import com.sales.entities.User;
import com.sales.entities.Wallet;
import com.sales.exceptions.NotFoundException;
import com.sales.wholesaler.services.WalletTransactionService;
import com.sales.wholesaler.services.WholesaleServicePlanService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService extends RepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    private WholesaleServicePlanService servicePlanService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    public Wallet getWalletDetail(String userSlug){
        Integer userId = userRepository.getUserIdBySlug(userSlug);
        if (userId == null) throw new NotFoundException("Wallet user details not found.");
        return walletRepository.findByUserId(userId);
    }


    @Transactional
    public void sendNotification(String title,String messageBody,int storeId,User loggedUser){
        logger.info("Entering sendNotification with title: {}, messageBody: {}, storeId: {}, loggedUser: {}", title, messageBody, storeId, loggedUser);
        StoreNotifications storeNotifications = new StoreNotifications();
        storeNotifications.setTitle(title);
        storeNotifications.setMessageBody(messageBody);
        storeNotifications.setWholesaleId(storeId);
        storeNotifications.setCreatedBy(loggedUser);
        storeHbRepository.insertStoreNotifications(storeNotifications);
        logger.info("Exiting sendNotification");
    }

    public boolean paymentViaWallet(String servicePlanSlug, String slug) {
        boolean payment = false;
        User user = userRepository.findUserBySlug(slug);
        if(user == null) throw new NotFoundException("User not found.");
        ServicePlan servicePlan = servicePlanService.findBySlug(servicePlanSlug);
        if(servicePlan == null) throw new NotFoundException("Service plan not found.");
        Long planPrice = servicePlan.getPrice();
        Wallet wallet = walletRepository.findByUserId(user.getId());
        float walletAmount = wallet != null ? wallet.getAmount() : 0;

        // Preparing wallet transaction.
        WalletTransactionDto walletTransactionDto = WalletTransactionDto.builder()
                .amount(planPrice.floatValue())
                .transactionType("DR")
                .status("F") // Default assuming it failed.
                .build();

        String title = null;
        String messageBody = null;
        if(wallet != null && walletAmount >= planPrice){
            wallet.setAmount(walletAmount-planPrice); // Updating wallet amount.
            walletRepository.save(wallet);
            servicePlanService.assignOrAddFuturePlans(user.getId(),servicePlan.getId());
            title = "Payment of "+planPrice;
            messageBody = "Your plan activated successfully. Plan Name : "+servicePlan.getName() + " for "+servicePlan.getMonths() + "Months.";
            walletTransactionDto.setStatus("S"); // Payment success.
            payment = true;
        }else{
            title = "Payment of "+planPrice;
            messageBody = "Wallet amount is inefficient for this payment.";
        }

        // Updating wallet transactions.
        walletTransactionService.addWalletTransaction(walletTransactionDto,user.getId());
        // Send notification also
        Integer storeId = storeRepository.getStoreIdByUserId(user.getId());
        sendNotification(title,messageBody,storeId,user);
        return payment;
    }



}
