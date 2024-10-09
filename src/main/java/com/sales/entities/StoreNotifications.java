package com.sales.entities;

import com.sales.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "store_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "title")
    String title;

    @Column(name = "message_body")
    String messageBody;

    @Column(name = "created_at")
    Long createAt;

    @Column(name = "created_by")
    Integer createdBy;

    @Column(name ="is_deleted" )
    String isDeleted ="N";




    public StoreNotifications(User loggedUser){
        this.createdBy = loggedUser.getId();
        this.createAt = Utils.getCurrentMillis();
    }



}
