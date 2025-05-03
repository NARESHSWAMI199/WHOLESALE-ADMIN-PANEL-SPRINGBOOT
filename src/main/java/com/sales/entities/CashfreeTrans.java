package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cashfree_trans")
public class CashfreeTrans {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;

    @Column(name = "slug")
    String slug;

    @Column(name = "user_id")
    String userId;

    @Column(name = "order_id")
    String orderId;

    @Column(name = "cf_payment_id")
    String cfPaymentId;

    @Column(name = "status")
    String status;

    @Column(name = "amount")
    String amount;

    @Column(name = "currency")
    String currency;

    @Column(name = "message")
    String message;

    @Column(name="payment_time")
    String paymentTime;

    @Column(name = "payment_group") // payment type like: upi or credit_card.
    String paymentType;

    @Column(name = "payment_method")
    String paymentMethod; // detailed description about payment

    @Column(name = "actual_response")
    String actualResponse;

    @Column(name = "created_at")
    Long createdAt;

}