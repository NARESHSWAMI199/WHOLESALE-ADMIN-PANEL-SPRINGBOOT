package com.sales.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.UUID;

import static com.sales.utils.Utils.getCurrentMillis;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "store")
@SQLRestriction("is_deleted != 'Y'") /* Same as where clause */
@Builder
public class Store implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "slug")
    String slug;
    @Column(name = "name")
    String storeName;
    @Column(name = "avtar")
    String avtar;
    @Column(name = "email")
    String email;
    @Column(name = "phone")
    String phone;
    @Column(name = "discription")
    String description;
    @Column(name = "rating")
    Float rating;
    @Column(name = "status")
    String status;
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
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToOne
    @JoinColumn(name = "address")
    Address address;

    @Transient
    Integer totalStoreItems;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category", referencedColumnName = "id")
    StoreCategory storeCategory;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subcategory", referencedColumnName = "id")
    StoreSubCategory storeSubCategory;



    public Store (User loggedUser) {
        this.createdAt = getCurrentMillis();
        this.createdBy = loggedUser.getId();
        this.updatedAt = getCurrentMillis();
        this.updatedBy = loggedUser.getId();
        this.status = "A";
        this.isDeleted = "N";
        this.slug = UUID.randomUUID().toString();
    }

}
