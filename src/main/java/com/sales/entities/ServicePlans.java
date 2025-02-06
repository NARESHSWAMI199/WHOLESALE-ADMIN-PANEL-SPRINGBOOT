package com.sales.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "service_plans")
@SQLRestriction("is_deleted != 'Y' ")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicePlans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "price")
    Float price;
    @Column(name = "discount")
    Float discount;
    @Column(name = "status")
    String status;
    @Column(name = "icon")
    String icon;
    @Column(name = "months")
    Integer months;
    @Column(name = "description")
    String description;
    @Column(name = "is_deleted")
    String isDeleted;
    @Column(name = "created_at")
    Long createdAt;
    @Column(name = "created_by")
    Integer createdBy;
    @Column(name = "updated_at")
    Long updatedAt;
    @Column(name = "updated_by")
    Integer updatedBy;


}
