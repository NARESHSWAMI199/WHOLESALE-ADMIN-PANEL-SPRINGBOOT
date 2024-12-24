package com.sales.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GroupPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    Group group;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    Permission permissions;



}
