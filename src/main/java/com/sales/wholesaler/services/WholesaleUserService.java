package com.sales.wholesaler.services;


import com.sales.dto.PasswordDto;
import com.sales.dto.StoreDto;
import com.sales.dto.UserDto;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class WholesaleUserService extends WholesaleRepoContainer {

    @Autowired
    WholesaleStoreService wholesaleStoreService;

    @Value("${default.password}")
    String password;

    public User findByEmailAndPassword(Map<String,String> param) {
        String email = param.get("email");
        String password = param.get("password");
        return wholesaleUserRepository.findByEmailAndPassword(email,password);
    }


    public StoreDto userDtoToStoreDto(UserDto userDto) {
        StoreDto storeDto = new StoreDto();
        storeDto.setStoreName(userDto.getStoreName());
        storeDto.setStoreEmail(userDto.getStoreEmail());
        storeDto.setDescription(userDto.getDescription());
        storeDto.setCity(userDto.getCity());
        storeDto.setState(userDto.getState());
        storeDto.setStorePhone(userDto.getStorePhone());
        return storeDto;
    }


    @Transactional
    public Map<String, Object> updateUserProfile(UserDto userDto, User loggedUser){
        Map<String, Object> responseObj = new HashMap<>();
        int isUpdated = updateUser(userDto, loggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "successfully updated.");
            responseObj.put("status", 201);
        } else {
            responseObj.put("message", "nothing to updated. may be something went wrong");
            responseObj.put("status", 400);
        }
        return responseObj;
    }

    @Transactional
    public int updateUser(UserDto userDto, User loggedUser) {
        return wholesaleUserHbRepository.updateUser(userDto, loggedUser);
    }

    public User getUserDetail(String slug) {
        return wholesaleUserRepository.findUserBySlug(slug);
    }

    @Transactional
    public int resetPasswordByUserSlug(PasswordDto passwordDto, User loggedUser){
        password = !Utils.isEmpty(password) ?  passwordDto.getPassword() : password;
        loggedUser.setPassword(password);
        return loggedUser.getId();
    }

}
