package com.sales.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Entity
@Table(name = "user_paginations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPagination implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "user_id")
    Integer userId;

    @OneToOne
    @JoinColumn(name="pagination_id")
    Pagination pagination;

    @Column(name="rows_number")
    Integer rowsNumber;


}
