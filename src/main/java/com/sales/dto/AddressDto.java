package com.sales.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {

    String addressSlug;
    String street;
    String zipCode;
    Integer city;
    Integer state;
    Float latitude;
    Float altitude;

}
