package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "chats")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "user_id")
    Integer userId;
    @Column(name = "receiver_key")
    String receiverKey;
    @Column(name = "sender_key")
    String senderKey;
    @Column(name = "message", nullable = false)
    String message;
    @Column(name = "created_at")
    Long createdAt;
    @Column(name = "updated_at")
    Long updatedAt;
    @Column(name = "is_deleted")
    String isDeleted;
}
