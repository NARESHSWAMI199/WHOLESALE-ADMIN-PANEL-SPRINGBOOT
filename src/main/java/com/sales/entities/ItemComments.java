package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item_comments")
@SQLRestriction("is_deleted != 'Y'")
public class ItemComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "item_id")
    Integer itemId;

    @Column(name = "slug")
    String slug;

    @Column(name = "store_id")
    Integer storeId;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "likes")
    Long likes= 0L;

    @Column(name = "parent_id")
    Integer parentId;

    @Column(name = "message")
    String message;

    @Column(name = "created_at")
    String createdAt;

    @Column(name="is_deleted")
    String isDeleted;

    @Column(name = "updated_at")
    String updatedAt;

    @Transient
    Integer repliesCount;

}
