package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "block_list")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Blocklist {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "user_id")
    Integer userId;
    @Column(name = "chat_user_id")
    Integer chatUserId;


}
