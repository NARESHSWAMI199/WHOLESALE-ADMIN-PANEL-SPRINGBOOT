package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportCategory {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @Column(name = "category_title")
    String categoryTitle;

    @Column(name = "description")
    String description;

}
