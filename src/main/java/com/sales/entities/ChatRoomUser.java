package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="chat_room_users")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUser {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;

    @Column(name = "room_id", nullable = false)
    long roomId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "created_at")
    Long createdAt;


}
