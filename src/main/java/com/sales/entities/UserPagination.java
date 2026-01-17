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
    private int id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name="pagination_id")
    private Integer paginationId;

    @Column(name="rows_number")
    private Integer rowsNumber;


}
