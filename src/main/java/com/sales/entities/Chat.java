package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;


@Entity
@Table(name = "chats")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("is_deleted !='Y'")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "user_id")
    Integer userId;
    @Column(name = "receiver_key")
    String receiver;
    @Column(name = "sender_key")
    String sender;
    @Column(name = "message", nullable = false)
    String message;
    @Column(name = "created_at")
    Long createdAt;
    @Column(name = "updated_at")
    Long updatedAt;
    @Column(name = "is_deleted")
    String isDeleted;
    @Column(name = "seen")
    Boolean seen;
    @Column(name = "images")
    String images;
    @Transient
    List<String> imagesUrls;
}
