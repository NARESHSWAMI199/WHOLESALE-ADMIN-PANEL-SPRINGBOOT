package com.sales.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "store_permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class StorePermissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "permission")
    String permission;


    @Column(name = "access_url")
    String accessUrl;


    @Column(name = "permission_for")
    String permissionFor;

    @Column(name="default_permission")
    String defaultPermission;


}