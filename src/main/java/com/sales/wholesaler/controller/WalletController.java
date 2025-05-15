package com.sales.wholesaler.controller;


import com.sales.entities.User;
import com.sales.entities.Wallet;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wholesale/wallet")
public class WalletController extends WholesaleServiceContainer {


    @RequestMapping("/")
    public ResponseEntity<Wallet> getWalletDetail(HttpServletRequest request){
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Wallet walletDetail = walletService.getWalletDetail(loggedUser.getId());
        return new ResponseEntity<>(walletDetail, HttpStatus.OK);
    }

}
