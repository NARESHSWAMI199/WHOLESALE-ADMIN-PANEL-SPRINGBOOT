package com.sales.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "item")
@Where(clause = "is_deleted != 'y'")
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;

    @Column(name = "label")
    String label;

    @Column(name = "price")
    float price;
    @Column(name = "discount")
    float discount;
    @Column(name = "description")
    String description;
    @Column(name = "avatar")
    String avtar;
    @Column(name = "rating")
    float rating;
    @Column(name = "status")
    String status="A";
    @Column(name = "is_deleted")
    String isDeleted="N";
    @Column(name = "created_at")
    Long createdAt;
    @Column(name = "created_by")
    Integer createdBy;
    @Column(name = "updated_at")
    Long updatedAt;
    @Column(name = "updated_by")
    Integer updatedBy;
    @Column(name = "slug")
    String slug;
    @Column(name = "in_stock")
    String inStock;

    @Column(name = "wholesale_id")
    Integer wholesaleId;

//    @ManyToOne
//    @JoinColumn(name = "wholesale_id")
//    Store wholesale;


}



