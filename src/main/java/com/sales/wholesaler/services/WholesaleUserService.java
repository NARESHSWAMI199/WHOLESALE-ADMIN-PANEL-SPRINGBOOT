package com.sales.wholesaler.services;


import com.sales.dto.PasswordDto;
import com.sales.dto.StoreDto;
import com.sales.dto.UserDto;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WholesaleUserService extends WholesaleRepoContainer {


    @Value("${profile.absolute}")
    String profilePath;

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
        String password = passwordDto.getPassword();
        loggedUser.setPassword(password);
        wholesaleUserRepository.save(loggedUser);
        return loggedUser.getId();
    }




    public int updateProfileImage(MultipartFile profileImage, String slug, User loggedUser) throws IOException {
        User user = wholesaleUserRepository.findUserBySlug(slug);
        Utils.isValidPerson(slug,user.getUserType(),loggedUser);
        if (!Utils.isValidImage(profileImage.getOriginalFilename())) return 0;
        String dirPath = profilePath+slug+"/";
        File dir = new File(dirPath);
        if(!dir.exists()) dir.mkdirs();
        profileImage.transferTo(new File(dirPath+profileImage.getOriginalFilename()));
        return  wholesaleUserHbRepository.updateProfileImage(slug,profileImage.getOriginalFilename());
    }



}
