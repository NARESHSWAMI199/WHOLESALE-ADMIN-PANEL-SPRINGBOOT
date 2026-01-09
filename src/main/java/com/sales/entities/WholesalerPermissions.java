package com.sales.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "wholesaler_permissions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
public class WholesalerPermissions {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "permission_id")
    private Integer permissionId;

}
