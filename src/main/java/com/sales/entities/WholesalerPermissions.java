package com.sales.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wholesaler_permissions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WholesalerPermissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "permission_id")
    private Integer permissionId;

}
