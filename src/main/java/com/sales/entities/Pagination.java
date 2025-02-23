package com.sales.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name = "paginations" )
@Getter
@Setter
public class Pagination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "field_for")
    String fieldFor;

}
