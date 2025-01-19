package com.sales.dto;


import com.sales.entities.User;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ContactDto {


    Integer id;
    Integer userId;
    String contactSlug;


}