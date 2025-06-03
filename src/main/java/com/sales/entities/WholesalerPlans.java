package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wholesaler_plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WholesalerPlans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "slug")
    String slug;
    @Column(name = "user_id")
    Integer userId;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    ServicePlan servicePlan;
    @Column(name = "created_at")
    Long createdAt;
    @Column(name = "expiry_date")
    Long expiryDate;
    @Column(name = "created_by")
    Integer createdBy;

    @Transient
    boolean isExpired;

}
