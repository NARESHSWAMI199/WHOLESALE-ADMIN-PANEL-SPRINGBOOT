package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "wholesaler_wallet")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "amount")
    Float amount;

    @Column(name = "updated_at")
    Long updated_at;

}
