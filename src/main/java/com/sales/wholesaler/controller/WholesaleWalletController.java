package com.sales.wholesaler.controller;


import com.sales.entities.User;
import com.sales.entities.Wallet;
import com.sales.global.ConstantResponseKeys;
import com.sales.utils.Utils;
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
@RequestMapping("wholesale/wallet")
public class WholesaleWalletController extends WholesaleServiceContainer {

    @GetMapping("/")
    public ResponseEntity<Wallet> getWalletDetail(HttpServletRequest request){
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Wallet walletDetail = wholesaleWalletService.getWalletDetail(loggedUser.getId());
        return new ResponseEntity<>(walletDetail, HttpStatus.OK);
    }


    @GetMapping("pay/{servicePlanSlug}")
    public ResponseEntity<Map<String,Object>> payUsingWallet(@PathVariable String servicePlanSlug,HttpServletRequest request) {
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Map<String,Object> result = new HashMap<>();
        boolean payment = wholesaleWalletService.paymentViaWallet(servicePlanSlug, loggedUser);
        if(payment){
            result.put(ConstantResponseKeys.MESSAGE,"Plan purchased successfully.");
            result.put(ConstantResponseKeys.STATUS,200);
        }else{
            result.put(ConstantResponseKeys.MESSAGE,"Inefficient amount in wallet.");
            result.put("status",400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }

}
