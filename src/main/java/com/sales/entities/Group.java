package com.sales.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

import static com.sales.utils.Utils.getCurrentMillis;

@Entity
@Table(name = "`groups`")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {
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


    public Group (User loggedUser) {
        this.slug = UUID.randomUUID().toString();
        this.createdAt = getCurrentMillis();
        this.createdBy = loggedUser.getId();
        this.updatedAt = getCurrentMillis();
        this.updatedBy = loggedUser.getId();
    }


}
