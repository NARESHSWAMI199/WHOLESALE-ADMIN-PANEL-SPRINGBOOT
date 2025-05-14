package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "wholesaler_future_plans")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WholesalerFuturePlan {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;

    @Column(name = "user_id",nullable = false)
    Integer userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id",nullable = false)
    ServicePlan servicePlan;

    @Column(name = "created_at")
    Long createdAt;

    @Column(name = "updated_at")
    Long updatedAt;

    @Column(name="status") // status like used or not.
    String status;

}
