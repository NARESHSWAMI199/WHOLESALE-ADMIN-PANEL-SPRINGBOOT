package com.sales.wholesaler.services;


import com.sales.dto.WalletTransactionDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.StoreNotifications;
import com.sales.entities.User;
import com.sales.entities.Wallet;
import com.sales.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WholesaleWalletService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleWalletService.class);

    @Autowired
    private WholesaleServicePlanService servicePlanService;

    @Autowired
    private  WalletTransactionService walletTransactionService;

    public Wallet getWalletDetail(Integer userId){
        return wholesaleWalletRepository.findByUserId(userId);
    }


    @Transactional
    public void sendNotification(String title,String messageBody,int storeId,User loggedUser){
        logger.info("Entering sendNotification with title: {}, messageBody: {}, storeId: {}, loggedUser: {}", title, messageBody, storeId, loggedUser);
        StoreNotifications storeNotifications = new StoreNotifications();
        storeNotifications.setTitle(title);
        storeNotifications.setMessageBody(messageBody);
        storeNotifications.setWholesaleId(storeId);
        storeNotifications.setCreatedBy(loggedUser);
        wholesaleNotificationHbRepository.insertStoreNotifications(storeNotifications);
        logger.info("Exiting sendNotification");
    }

    public boolean paymentViaWallet(String servicePlanSlug, User loggedUser) {
        boolean payment = false;
        int userId = loggedUser.getId();
        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(userId);
        ServicePlan servicePlan = wholesaleServicePlanRepository.findBySlug(servicePlanSlug);
        if(servicePlan == null) throw new NotFoundException("Service plan not found.");
        Long planPrice = servicePlan.getPrice();
        Wallet wallet = wholesaleWalletRepository.findByUserId(userId);
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
            wholesaleWalletRepository.save(wallet);
            servicePlanService.assignOrAddFuturePlans(userId,servicePlan.getId());
            title = "Payment of "+planPrice;
            messageBody = "Your plan activated successfully. Plan Name : "+servicePlan.getName() + " for "+servicePlan.getMonths() + "Months.";
            walletTransactionDto.setStatus("S"); // Payment success.
            payment = true;
        }else{
            title = "Payment of "+planPrice;
            messageBody = "Wallet amount is inefficient for this payment.";
        }

        // Updating wallet transactions.
        walletTransactionService.addWalletTransaction(walletTransactionDto,userId);
        // Send notification also
        sendNotification(title,messageBody,storeId,loggedUser);
        return payment;
    }



}
