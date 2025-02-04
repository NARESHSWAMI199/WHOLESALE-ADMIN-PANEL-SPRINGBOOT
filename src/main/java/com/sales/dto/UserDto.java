package com.sales.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserDto extends StoreDto {
    private String email;
    private String password;
    private String username;
    private String token;
    private String userType="R";  // Default the user is 'Retailer' but if user will pass user type then it's will update
    private String status;
    private String contact=""; // Optional field
    private String slug;
    private MultipartFile profileImage;
    Integer userId;
    List<Integer> groupList;
    List<Integer> storePermissions;
}
