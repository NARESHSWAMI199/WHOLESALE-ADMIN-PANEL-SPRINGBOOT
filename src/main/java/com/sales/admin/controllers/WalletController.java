package com.sales.admin.controllers;


import com.sales.entities.Wallet;
import com.sales.global.ConstantResponseKeys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("admin/store/wallet")
public class WalletController extends ServiceContainer {

    @GetMapping("/{userSlug}")
    public ResponseEntity<Wallet> getWalletDetail(@PathVariable String userSlug, HttpServletRequest request){
        Wallet walletDetail = walletService.getWalletDetail(userSlug);
        return new ResponseEntity<>(walletDetail, HttpStatus.OK);
    }


    @GetMapping("pay/{userSlug}/{servicePlanSlug}")
    public ResponseEntity<Map<String,Object>> payUsingWallet(@PathVariable String userSlug , @PathVariable String servicePlanSlug,HttpServletRequest request) {
        Map<String,Object> result = new HashMap<>();
        boolean payment = walletService.paymentViaWallet(servicePlanSlug, userSlug);
        if(payment){
            result.put("message","Plan purchased successfully.");
            result.put(ConstantResponseKeys.STATUS,200);
        }else{
            result.put("message","Inefficient amount in wallet.");
            result.put(ConstantResponseKeys.STATUS,400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }

}
