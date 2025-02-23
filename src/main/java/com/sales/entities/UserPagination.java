package com.sales.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user_paginations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPagination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name="pagination_id")
    Integer paginationId;

    @Column(name="rows_number")
    Integer rowsNumber;


}
