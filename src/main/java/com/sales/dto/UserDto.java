package com.sales.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserDto extends StoreDto {
    private String email;
    private String password;
    private String username;
    private String token;
    private String userType="R";
    private String status;
    private String contact="";
    private String slug;
    private MultipartFile profileImage;
}
