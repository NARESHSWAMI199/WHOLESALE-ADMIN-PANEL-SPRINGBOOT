package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemReport {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @Column(name = "item_id")
    Long itemId;

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
