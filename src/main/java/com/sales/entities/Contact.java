package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "contact_list")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Contact implements Serializable {

    @Column(name = "user_id")
    Integer userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id")
    User contactUser;


}
