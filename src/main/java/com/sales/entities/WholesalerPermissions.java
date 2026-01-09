package com.sales.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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
    Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "permission_id")
    private Integer permissionId;

}
