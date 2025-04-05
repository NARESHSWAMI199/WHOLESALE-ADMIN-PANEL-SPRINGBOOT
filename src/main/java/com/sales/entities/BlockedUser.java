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
public class BlockedUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "user_id")
    Integer userId;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_user_id")
    User blockedUser;
    @Column(name = "created_at")
    Long createdAt;


}
