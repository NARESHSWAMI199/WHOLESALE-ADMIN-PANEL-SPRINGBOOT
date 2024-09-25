package com.sales.admin.services;


import com.sales.dto.StatusDto;
import com.sales.dto.StoreDto;
import com.sales.dto.UserDto;
import com.sales.dto.UserSearchFilters;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sales.specifications.UserSpecifications.*;

@Service
public class UserService extends RepoContainer {

    @Autowired
    StoreService storeService;

    @Value("${profile.absolute}")
    String profilePath;

    @Value("${profile.relative}")
    String profileRelativePath;



    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    public User findByEmailAndPassword(UserDto userDto) {
        return userRepository.findByEmailAndPassword(userDto.getEmail(), userDto.getPassword());
    }

    public Map<String, Integer> getUserCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.totalUserCount());
        responseObj.put("active",userRepository.optionUserCount("A"));
        responseObj.put("deactive",userRepository.optionUserCount("D"));
        return responseObj;
    }

    public Map<String, Integer> getRetailersCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("R"));
        responseObj.put("active",userRepository.getUserWithUserType("A","R"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","R"));
        return responseObj;
    }

    public Map<String, Integer> getWholesalersCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("W"));
        responseObj.put("active",userRepository.getUserWithUserType("A","W"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","W"));
        return responseObj;
    }

    public Map<String, Integer> getStaffsCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("S"));
        responseObj.put("active",userRepository.getUserWithUserType("A","S"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","S"));
        return responseObj;
    }




    public Page<User> getAllUser(UserSearchFilters filters) {
        Specification<User> specification = Specification.where(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
            .and(greaterThanOrEqualFromDate(filters.getFromDate()))
            .and(lessThanOrEqualToToDate(filters.getToDate()))
            .and(isStatus(filters.getStatus()))
            .and(hasUserType(filters.getUserType())).and(hasSlug(filters.getSlug()))
        );
        Pageable pageable = getPageable(filters);
        return userRepository.findAll(specification,pageable);
    }




    public StoreDto userDtoToStoreDto(UserDto userDto){
        StoreDto storeDto = new StoreDto();
        storeDto.setStoreName(userDto.getStoreName());
        storeDto.setStoreEmail(userDto.getStoreEmail());
        storeDto.setDescription(userDto.getDescription());
        storeDto.setCity(userDto.getCity());
        storeDto.setState(userDto.getState());
        storeDto.setStorePhone(userDto.getStorePhone());
        storeDto.setStoreSlug(userDto.getStoreSlug());
        return storeDto;
    }

    @Transactional
    public Map<String, Object> createOrUpdateUser(UserDto userDto, User loggedUser) throws Exception {
        Map<String, Object> responseObj = new HashMap<>();
        StoreDto storeDto = null;
        if (!Utils.isEmpty(userDto.getSlug())) {
            int isUpdated = updateUser(userDto, loggedUser);
            if (userDto.getUserType().equalsIgnoreCase("W")){
                storeDto =  userDtoToStoreDto(userDto);
                storeDto.setUserSlug(userDto.getSlug());
                storeService.createOrUpdateStore(storeDto,loggedUser);
            }
            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 201);
            } else {
                responseObj.put("message", "nothing to updated. may be something went wrong");
                responseObj.put("status", 400);
            }
            return responseObj;
        } else {
            User updatedUser = createUser(userDto, loggedUser);
            System.out.println(userDto.getUserType() + " : "+userDto.getUserSlug());
            if (userDto.getUserType().equalsIgnoreCase("W")){
                storeDto =  userDtoToStoreDto(userDto);
                storeDto.setUserSlug(updatedUser.getSlug());
                storeService.createOrUpdateStore(storeDto,loggedUser);
            }
            if (updatedUser.getId() > 0) {
                responseObj.put("res", updatedUser);
                responseObj.put("message", "successfully inserted.");
                responseObj.put("status", 200);
            } else {
                responseObj.put("message", "nothing to insert. may be something went wrong");
                responseObj.put("status", 400);
            }
        }
        return responseObj;
    }

    @Transactional
    public User createUser(UserDto userDto, User loggedUser) {
        User user = new User(loggedUser);
        user.setUsername(userDto.getUsername());
        user.setSlug(UUID.randomUUID().toString());
        user.setPassword(userDto.getPassword());
        user.setContact(userDto.getContact());
        user.setEmail(userDto.getEmail());
        user.setUserType(userDto.getUserType());
        return userRepository.save(user);
    }

    @Transactional
    public int updateUser(UserDto userDto, User loggedUser){
        return userHbRepository.updateUser(userDto,loggedUser);
    }

    public User getUserDetail(String slug){
        return userRepository.findUserBySlug(slug);
    }


    @Transactional
    public int deleteUserBySlug(String slug){
        User user = getUserDetail(slug);
        storeService.deleteStoreByUserId(user.getId());
        return userHbRepository.deleteUserBySlug(slug);
    }

    public int updateStatusBySlug(StatusDto statusDto){
        return userHbRepository.updateStatus(statusDto.getSlug(),statusDto.getStatus());
    }



    public int updateProfileImage(MultipartFile profileImage,String slug,User loggerdUser) throws IOException {
        if (!isValidImage(profileImage.getOriginalFilename())) return 0;
        profileImage.transferTo(new File(profilePath+slug+profileImage.getOriginalFilename()));
        return  userHbRepository.updateProfileImage(slug,profileRelativePath+slug+profileImage.getOriginalFilename());
    }


    public boolean isValidImage(String image){
        Pattern pattern =  Pattern.compile(IMAGE_PATTERN);
        Matcher matcher = pattern.matcher(image);
        return matcher.matches();
    }


}
