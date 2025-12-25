package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "chats")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@SQLRestriction("is_sender_deleted !='Y' or is_receiver_deleted !='Y' ")
public class Chat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "parent_id")
    Long parentId;
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
    @Column(name = "is_sender_deleted")
    String isSenderDeleted;
    @Column(name = "is_receiver_deleted")
    String isReceiverDeleted;
    @Column(name = "seen")
    Boolean seen;
    @Column(name = "images")
    String images;
    @Column(name = "is_sent")
    String isSent;

    @Transient
    List<String> imagesUrls;
}
