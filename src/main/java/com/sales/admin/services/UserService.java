package com.sales.admin.services;


import com.sales.dto.*;
import com.sales.entities.Store;
import com.sales.entities.StorePermissions;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
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
import java.util.*;

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

    public Map<String, Integer> getAdminsCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("SA"));
        responseObj.put("active",userRepository.getUserWithUserType("A","SA"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","SA"));
        return responseObj;
    }




    public Page<User> getAllUser(UserSearchFilters filters, User loggedUser) {
       String notUserType = null;
        if(filters.getUserType().equals("SA") && loggedUser.getId() !=GlobalConstant.suId){
            filters.setUserType(null);
        }else if(filters.getUserType().equals("A")) {
            notUserType = "SA";
            filters.setUserType(null);
        }
        Specification<User> specification = Specification.where(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
            .and(greaterThanOrEqualFromDate(filters.getFromDate()))
            .and(lessThanOrEqualToToDate(filters.getToDate()))
            .and(isStatus(filters.getStatus()))
                        .and(hasSlug(filters.getSlug()))
                        .and(notSuperAdmin())
                        .and(hasUserType(filters.getUserType()))
                        .and(hasNotUserType(notUserType))
        );

        Pageable pageable = getPageable(filters);
        return userRepository.findAll(specification,pageable);
    }




    public StoreDto userDtoToStoreDto(UserDto userDto){
        StoreDto storeDto = new StoreDto();
        storeDto.setStreet(userDto.getStreet());
        storeDto.setZipCode(userDto.getZipCode());
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

        /** condition for create or update super user */
        if((loggedUser.getId() !=GlobalConstant.suId && userDto.getUserType().equals("SA") && !loggedUser.getSlug().equals(userDto.getSlug()))) throw new MyException("You don't have permissions to create a admin contact to administrator.");
        Utils.mobileAndEmailValidation(userDto.getEmail(),userDto.getContact(),"Not a valid user's _ recheck your and user's _.");
        /** condition who can update a staff */
        Utils.canUpdateAStaff(userDto.getSlug(),userDto.getUserType(),loggedUser);

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

        /** going to update user's groups ------------> only for staffs and super admin has group permissions */
        if ((userDto.getUserId() != loggedUser.getId()) && (userDto.getUserType().equals("SA") || userDto.getUserType().equals("S")) ) {
            int isAssigned = permissionHbRepository.assignGroupsToUser(userDto.getUserId(), userDto.getGroupList());
            if (isAssigned < 1)
                throw new MyException("Something went wrong during update user's groups. please contact to administrator.");
        }else if((userDto.getUserId() != loggedUser.getId()) && userDto.getUserType().equals("W")){
            List<Integer> defaultPermissions = storePermissionsRepository.getAllDefaultPermissionsIds();
            int isAssigned = permissionHbRepository.assignPermissionsToWholesaler(userDto.getUserId(),defaultPermissions);
            if (isAssigned < 1)
                throw new MyException("Something went wrong during update wholesaler's permissions. please contact to administrator.");
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
        if(user !=null && (user.getId() !=GlobalConstant.suId || loggedUser.getId() == GlobalConstant.suId )){
            return user;
        }
        return null;
    }


    public User getUserDetail(String slug){
        User user = userRepository.findUserBySlug(slug);
        if(user !=null && (user.getId() !=GlobalConstant.suId )){
            return user;
        }
        return null;
    }

    @Transactional
    public int deleteUserBySlug(String slug,User loggedUser){
        User user = getUserDetail(slug);
        Utils.canUpdateAStaff(slug,user.getUserType(),loggedUser);
        storeService.deleteStoreByUserId(user.getId());
        return userHbRepository.deleteUserBySlug(slug);
    }


    @Transactional
    public int resetPasswordByUserSlug(PasswordDto passwordDto,User loggedUser){
        password = !Utils.isEmpty(password) ?  passwordDto.getPassword() : password;
        User user = userRepository.findUserBySlug(passwordDto.getSlug());
        Utils.canUpdateAStaff(passwordDto.getSlug(),user.getUserType(),loggedUser);
        user.setPassword(password);
        return user.getId();
    }

    @Transactional
    public int updateStatusBySlug(StatusDto statusDto,User loggedUser){
        try {
            String status = statusDto.getStatus();
            User user = userRepository.findUserBySlug(statusDto.getSlug());
            Utils.canUpdateAStaff(statusDto.getSlug(),user.getUserType(),loggedUser);
            user.setStatus(status);
            if(!user.getUserType().equals("W")){
                user = userRepository.save(user);
                return user.getId();
            }
            Store store = storeRepository.findStoreByUserId(user.getId());
            store.setStatus(status);
            store = storeRepository.save(store);
            if (store.getId() > 0)
                user = userRepository.save(user);
            return user.getId();
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }



    public int updateProfileImage(MultipartFile profileImage,String slug,User loggedUser) throws IOException {
        User user = userRepository.findUserBySlug(slug);
        Utils.canUpdateAStaff(slug,user.getUserType(),loggedUser);
        if (!Utils.isValidImage(profileImage.getOriginalFilename())) return 0;
        String dirPath = profilePath+slug+"/";
        File dir = new File(dirPath);
        if(!dir.exists()) dir.mkdirs();
        profileImage.transferTo(new File(dirPath+profileImage.getOriginalFilename()));
        return  userHbRepository.updateProfileImage(slug,profileImage.getOriginalFilename());
    }



    public List<Integer> getUserGroupsIdBySlug(String slug) {
        return userRepository.getUserGroupsIdBySlug(slug);
    }


    public List<Integer> getWholesalerAllAssignedPermissions(String slug) {
        User user = getUserDetail(slug);
        if(user==null) return null;
        return storePermissionsRepository.getAllAssignedPermissionsIdByUserId(user.getId());
    }

    public Map<String,Object> getWholesalerAllPermissions() {
        List<StorePermissions> storePermissionsList = storePermissionsRepository.findAll();
        Map<String,Object> result = new HashMap<>();
        for(StorePermissions storePermissions : storePermissionsList){
            String key= storePermissions.getPermissionFor();
            if(result.containsKey(key)){
                Map<String,Object> newPermission = new HashMap<>();
                newPermission.put("permission",storePermissions.getPermission());
                newPermission.put("id",storePermissions.getId());
                List<Object> oldList = (List<Object>) result.get(key);
                oldList.add(newPermission);
                result.put(key,oldList);
            }else{
                Map<String,Object> newPermission = new HashMap<>();
                newPermission.put("permission",storePermissions.getPermission());
                newPermission.put("id",storePermissions.getId());
                List<Object> newList = new ArrayList<>();
                newList.add(newPermission);
                result.put(key,newList);
            }
        }
        return result;
    }





    @Transactional(rollbackOn = {MyException.class,RuntimeException.class})
    public Map<String,Object> updateWholesalerPermissions(UserDto userDto, User loggededUser) throws MyException {
        Map<String,Object> responseObject = new HashMap<>();
        if (Utils.isEmpty(userDto.getSlug()) || !userDto.getUserType().equals("W")) throw  new MyException("There is nothing to update.");
        User user = getUserDetail(userDto.getSlug());
        if (user == null) throw new MyException("Not a valid user.");
        int isUpdated = permissionHbRepository.assignPermissionsToWholesaler(user.getId(), userDto.getStorePermissions());
        if (isUpdated > 0) {
            responseObject.put("message", "All permissions have been updated successfully.");
            responseObject.put("status", 201);
        } else {
            responseObject.put("message", "Something went wrong during update permissions");
            responseObject.put("status", 400);
        }
        return responseObject;
    }


}