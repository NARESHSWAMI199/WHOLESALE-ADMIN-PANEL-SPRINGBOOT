package com.sales.admin.controllers;


import com.sales.dto.SearchFilters;
import com.sales.entities.WalletTransaction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/store/wallet/transactions/")
public class StoreWalletTransactionController extends ServiceContainer {


    @PostMapping("all/{userSlug}")
    public ResponseEntity<Page<WalletTransaction>> getAllWalletTransactionsByUserId(@PathVariable String userSlug, HttpServletRequest request, @RequestBody SearchFilters searchFilters){
        Page<WalletTransaction> transactions = storeWalletTransactionService.getAllWalletTransactionByUserId(searchFilters, userSlug);
        return new ResponseEntity<>(transactions,HttpStatus.valueOf(200));
    }


}
