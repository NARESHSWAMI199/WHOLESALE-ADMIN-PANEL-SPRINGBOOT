package com.sales.entities;


import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @Column(name = "capacity")
    Float capacity;
    @Column(name = "price")
    float price;
    @Column(name = "discount")
    float discount;
    @Column(name = "description")
    String description;
    @Column(name = "avatar")
    String avtars;
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


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category", referencedColumnName = "id")
    ItemCategory itemCategory;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subcategory", referencedColumnName = "id")
    ItemSubCategory itemSubCategory;


//    @ManyToOne
//    @JoinColumn(name = "wholesale_id")
//    Store wholesale;


    public String toString() {
        String label = this.getLabel().equals("O") ? "Old" : "New";
        return "{  Key :  " + this.getSlug() + " , " +
                "name :  " + this.getName() + ", " +
                "price :  " + this.getPrice() + ", " +
                "discount :  " + this.getDiscount() + "," +
                "label :  " + label + "," +
                "description :  " + this.getDescription() + "}";
    }

}



