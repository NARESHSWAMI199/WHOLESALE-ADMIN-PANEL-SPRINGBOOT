package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact_list")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contact {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "user_id")
    Integer userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id")
    User contact;


}
