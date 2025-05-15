package com.sales.wholesaler.services;

import com.sales.entities.Wallet;
import org.springframework.stereotype.Service;

@Service
public class WalletService extends WholesaleRepoContainer {


    public Wallet getWalletDetail(Integer userId){
        return walletRepository.findByUserId(userId);
    }



}
