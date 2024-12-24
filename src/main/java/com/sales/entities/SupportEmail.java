package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "support_emails")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupportEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "email")
    String email;

    @Column(name = "password_key")
    String passwordKey;

    @Column(name="support_type")
    String supportType;

}
