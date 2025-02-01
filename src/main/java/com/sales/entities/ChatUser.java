package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_users_list")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "user_id")
    Integer userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_user_id")
    User chatUser;

    @Column(name = "status")
    String status;


}
