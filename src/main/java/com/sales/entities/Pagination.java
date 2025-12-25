package com.sales.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Entity
@Table(name = "paginations" )
@Getter
@Setter
public class Pagination implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "field_for")
    String fieldFor;
    @Column(name = "can_see")
    String canSee;

}
