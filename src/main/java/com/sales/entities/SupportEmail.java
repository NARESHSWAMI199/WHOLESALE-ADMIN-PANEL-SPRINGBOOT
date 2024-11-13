package com.sales.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
