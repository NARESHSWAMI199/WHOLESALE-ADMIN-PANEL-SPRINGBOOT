package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "report_categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportCategory implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @Column(name = "category_title")
    String categoryTitle;

    @Column(name = "description")
    String description;

}
