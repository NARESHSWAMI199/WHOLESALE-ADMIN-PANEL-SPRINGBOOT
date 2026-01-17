package com.sales.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Table(name = "group_permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class GroupPermission implements Serializable {

    @Column(name = "group_id")
    Long groupId;

    @Column(name = "permission_id")
    Permission permissionId;

}
