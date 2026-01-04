package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

import static com.sales.utils.Utils.getCurrentMillis;

@Entity
@Table(name = "`GROUPS`")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int id;

    @Column(name = "name")
    String name;

    @Column(name = "slug")
    String slug;

    @Column(name = "created_at")
    Long createdAt;

    @Column(name = "created_by")
    Integer createdBy;

    @Column(name = "updated_at")
    Long updatedAt;

    @Column(name = "updated_by")
    Integer updatedBy;


    public Group (AuthUser loggedUser) {
        this.slug = UUID.randomUUID().toString();
        this.createdAt = getCurrentMillis();
        this.createdBy = loggedUser.getId();
        this.updatedAt = getCurrentMillis();
        this.updatedBy = loggedUser.getId();
    }


}
