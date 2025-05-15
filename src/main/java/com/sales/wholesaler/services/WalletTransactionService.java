package com.sales.wholesaler.services;

import com.sales.dto.SearchFilters;
import com.sales.dto.WalletTransactionDto;
import com.sales.entities.Wallet;
import com.sales.entities.WalletTransaction;
import com.sales.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.sales.specifications.WalletTransactionSpecification.*;

@Service
public class WalletTransactionService extends WholesaleRepoContainer{

    private static final Logger logger = LoggerFactory.getLogger(WalletTransactionService.class);




    public Page<WalletTransaction> getAllWalletTransactionByUserId(SearchFilters searchFilters,Integer userId){
        Specification<WalletTransaction> specification = Specification.where(
            hasSlug(searchFilters.getSlug())
            .and(greaterThanOrEqualFromDate(searchFilters.getFromDate()))
            .and(lessThanOrEqualToToDate(searchFilters.getToDate())
            .and(hasUserId(userId))
        ));
        Pageable pageable = getPageable(searchFilters);
        return walletTransactionRepository.findAll(specification,pageable);
    }



    public WalletTransaction addWalletTransaction(WalletTransactionDto walletTransactionDto,Integer userId) {
        logger.info("The addWalletTransaction method started with wallTransactionDto : {}",walletTransactionDto);
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .slug(UUID.randomUUID().toString())
                .userId(userId)
                .amount(walletTransactionDto.getAmount())
                .transactionType(walletTransactionDto.getTransactionType())
                .createdAt(Utils.getCurrentMillis())
                .status(walletTransactionDto.getStatus())
                .build();
        logger.info("The addWalletTransaction method ended with wallTransactionDto : {}",walletTransaction);

        if(!Utils.isEmpty(walletTransactionDto.getTransactionType()) && walletTransactionDto.getTransactionType().equalsIgnoreCase("CR")){
            Integer added = walletRepository.addMoneyInWallet(walletTransaction.getAmount(), userId, Utils.getCurrentMillis());
            logger.info("Added money in wallet rows was updated : {} ",added);
            if(added < 1){ // if user not found in wallet.
                Wallet wallet = Wallet.builder()
                        .userId(userId)
                        .amount(walletTransaction.getAmount())
                        .updatedAt(Utils.getCurrentMillis())
                        .build();
                walletRepository.save(wallet);
            }
        }else{
            Integer deducted = walletRepository.deductMoneyFromWallet(walletTransaction.getAmount(), userId, Utils.getCurrentMillis());
            logger.info("Detected money in wallet rows was updated : {} ",deducted);
        }

        return walletTransactionRepository.save(walletTransaction);
    }

}
