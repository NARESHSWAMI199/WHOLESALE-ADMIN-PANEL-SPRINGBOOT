package com.sales.admin.controllers;


import com.sales.admin.services.StoreWalletTransactionService;
import com.sales.dto.SearchFilters;
import com.sales.entities.WalletTransaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/store/wallet/transactions/")
@RequiredArgsConstructor
public class StoreWalletTransactionController  {

    private final StoreWalletTransactionService storeWalletTransactionService;

    @PostMapping("all/{userSlug}")
    public ResponseEntity<Page<WalletTransaction>> getAllWalletTransactionsByUserId(@PathVariable String userSlug, HttpServletRequest request, @RequestBody SearchFilters searchFilters){
        Page<WalletTransaction> transactions = storeWalletTransactionService.getAllWalletTransactionByUserId(searchFilters, userSlug);
        return new ResponseEntity<>(transactions,HttpStatus.valueOf(200));
    }


}
