package com.sales.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "group_permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupPermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id" , referencedColumnName = "id")
    Permission permissions;



}
