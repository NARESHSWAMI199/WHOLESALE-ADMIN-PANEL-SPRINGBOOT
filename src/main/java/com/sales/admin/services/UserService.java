package com.sales.admin.services;


import com.sales.dto.*;
import com.sales.entities.Store;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sales.specifications.UserSpecifications.*;

@Service
public class UserService extends RepoContainer {

    @Autowired
    StoreService storeService;

    @Value("${profile.absolute}")
    String profilePath;

    @Value("${profile.relative}")
    String profileRelativePath;

    @Value("${default.password}")
    String password;



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
            .and(hasUserType(filters.getUserType()))
                        .and(hasSlug(filters.getSlug()))
                        .and(notSuperAdmin())
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

    @Transactional(rollbackOn = {MyException.class, RuntimeException.class})
    public Map<String, Object> createOrUpdateUser(UserDto userDto, User loggedUser) throws MyException, IOException {
        Map<String, Object> responseObj = new HashMap<>();
        StoreDto storeDto = null;
        /** make sure email and phone numbers are valid */
        Utils.mobileAndEmailValidation(userDto.getEmail(),userDto.getContact(),"Not a valid user's _ recheck your and user's _.");
        if (!Utils.isEmpty(userDto.getSlug())) {
            int isUpdated = updateUser(userDto, loggedUser);
             Integer userId = userRepository.getUserIdBySlug(userDto.getSlug());
             userDto.setUserId(userId);
            if (userDto.getUserType().equalsIgnoreCase("W")){
                storeDto =  userDtoToStoreDto(userDto);
                storeDto.setUserSlug(userDto.getSlug());
                storeService.createOrUpdateStore(storeDto, loggedUser);
            }
            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 201);
            } else {
                responseObj.put("message", "nothing to updated. may be something went wrong");
                responseObj.put("status", 400);
            }
           // return responseObj;
        } else {
            User updatedUser = createUser(userDto, loggedUser);
            userDto.setUserId(updatedUser.getId());
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
                responseObj.put("message", "nothing to save. may be something went wrong please contact to administrator.");
                responseObj.put("status", 400);
            }
        }

        /** going to update user's groups */
        if (userDto.getUserId() != loggedUser.getId()) {
            int isAssigned = permissionHbRepository.assignGroupsToUser(userDto.getUserId(), userDto.getGroupList());
            if (isAssigned < 1)
                throw new MyException("Something went wrong during update user's groups. please contact to administrator.");
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
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Transactional
    public int updateUser(UserDto userDto, User loggedUser) {
        return userHbRepository.updateUser(userDto,loggedUser);
    }

    public User getUserDetail(String slug ,User loggedUser){
       User user = userRepository.findUserBySlug(slug);
        if(user !=null && (user.getId() !=0 || loggedUser.getId() == 0)){
            return user;
        }
        return null;
    }


    public User getUserDetail(String slug){
        User user = userRepository.findUserBySlug(slug);
        if(user !=null && (user.getId() !=0)){
            return user;
        }
        return null;
    }

    @Transactional
    public int deleteUserBySlug(String slug){
        User user = getUserDetail(slug);
        storeService.deleteStoreByUserId(user.getId());
        return userHbRepository.deleteUserBySlug(slug);
    }


    @Transactional
    public int resetPasswordByUserSlug(PasswordDto passwordDto){
        password = !Utils.isEmpty(password) ?  passwordDto.getPassword() : password;
        User user = getUserDetail(passwordDto.getSlug());
        user.setPassword(password);
        return user.getId();
    }

    @Transactional
    public int updateStatusBySlug(StatusDto statusDto){
        try {
            String status = statusDto.getStatus();
            User user = userRepository.findUserBySlug(statusDto.getSlug());
            user.setStatus(status);
            if(!user.getUserType().equals("W")){
                user = userRepository.save(user);
                return user.getId();
            }
            Store store = storeRepository.findStoreByUserId(user.getId());
            store.setStatus(status);
            store = storeRepository.save(store);
            if (store != null && store.getId() > 0)
                user = userRepository.save(user);
            return user.getId();
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }



    public int updateProfileImage(MultipartFile profileImage,String slug,User loggerdUser) throws IOException {
        if (!Utils.isValidImage(profileImage.getOriginalFilename())) return 0;
        profileImage.transferTo(new File(profilePath+slug+profileImage.getOriginalFilename()));
        return  userHbRepository.updateProfileImage(slug,profileRelativePath+slug+profileImage.getOriginalFilename());
    }



    public List<Integer> getUserGroupsIdBySlug(String slug) {
        return userRepository.getUserGroupsIdBySlug(slug);
    }

}
