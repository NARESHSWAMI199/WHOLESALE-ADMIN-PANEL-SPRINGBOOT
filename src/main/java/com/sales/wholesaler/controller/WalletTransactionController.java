package com.sales.wholesaler.controller;


import com.sales.dto.SearchFilters;
import com.sales.entities.User;
import com.sales.entities.WalletTransaction;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("wholesale/wallet/transactions/")
public class WalletTransactionController extends WholesaleServiceContainer {


    @PostMapping("all")
    public ResponseEntity<Page<WalletTransaction>> getAllWalletTransactionsByUserId(HttpServletRequest request, @RequestBody SearchFilters searchFilters){
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Page<WalletTransaction> transactions = walletTransactionService.getAllWalletTransactionByUserId(searchFilters, loggedUser.getId());
        return new ResponseEntity<>(transactions,HttpStatus.valueOf(200));
    }


}
