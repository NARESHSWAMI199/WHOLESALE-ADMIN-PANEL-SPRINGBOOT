package com.sales.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "store_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted != 'Y' ")
public class StoreNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "wholesale_id")
    Integer wholesaleId;

    @Column(name = "title")
    String title;

    @Column(name = "message_body")
    String messageBody;

    @Column(name = "created_at")
    Long createAt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by",referencedColumnName = "user_id")
    User createdBy;

    @Column(name ="is_deleted" )
    String isDeleted ="N";

    @Column(name ="seen" )
    String seen="N";




//    public StoreNotifications(User loggedUser){
//        this.createdBy = loggedUser;
//        this.createAt = Utils.getCurrentMillis();
//    }



}
