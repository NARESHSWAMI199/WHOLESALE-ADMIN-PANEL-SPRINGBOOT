package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phonepe_trans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhonePeTrans {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;
    @Column(name = "merchant_transaction_id",nullable = false)
    String merchantTransactionId;
    @Column(name = "x_verify")
    String xVerify;
    @Column(name = "amount",nullable = false)
    Long amount;
    @Column(name = "transaction_id")
    String transactionId;
    @Column(name = "bank_id")
    String bankId;
    @Column(name = "response_code")
    String responseCode;
    @Column(name = "payment_type" )
    String paymentType;
    @Column(name = "state")
    String state;
    @Column(name = "code")
    String code;
    @Column(name = "message")
    String message;
    @Column(name = "actual_response")
    String actualResponse;
    @Column(name = "status")
    String status;
    @Column(name = "created_at")
    Long createdAt;
}
