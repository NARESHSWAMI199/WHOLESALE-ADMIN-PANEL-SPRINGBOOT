package com.sales.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    String addressSlug;
    String street;
    String zipCode;
    Integer city;
    Integer state;
    Float latitude;
    Float altitude;

}
