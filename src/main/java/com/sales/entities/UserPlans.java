package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPlans{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "slug")
    String slug;
    @Column(name = "user_id")
    Integer userId;
    @Column(name = "plan_id")
    Integer planId;
    @Column(name = "created_at")
    Long createdAt;
    @Column(name = "expiry_date")
    Long expiryDate;
    @Column(name = "created_by")
    Integer createdBy;
}
