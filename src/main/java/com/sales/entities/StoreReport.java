package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "store_report")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreReport {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;

    @Column(name = "store_id")
    Integer storeId;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "message")
    String message;

    @Column(name = "created_at")
    Long createdAt;

    @Column(name = "updated_at")
    Long updatedAt;

}
