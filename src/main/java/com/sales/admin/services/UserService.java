package com.sales.admin.services;


import com.sales.dto.*;
import com.sales.entities.Store;
import com.sales.entities.StorePermissions;
import com.sales.entities.SupportEmail;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    public User findUserByOtpAndEmail(UserDto userDto) {
        /** here password key has otp */
        return  userRepository.findUserByOtpAndEmail(userDto.getEmail(),userDto.getPassword());
    }


    public Integer getUserIdBySlug(String slug){
        return userRepository.getUserIdBySlug(slug);
    }

    public void resetOtp(String email){
        userHbRepository.updateOtp(email,"");
    }


    public boolean sendOtp(UserDto userDto){
        boolean sent = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your mail server
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        User user = userRepository.findUserByEmail(userDto.getEmail());
        if (user == null) return  false;

        String recipient = user.getEmail();

        SupportEmail supportEmail =  supportEmailsRepository.findSupportEmailBySupportType("SUPPORT");
        String sender = supportEmail.getEmail();
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
            return new PasswordAuthentication(sender, supportEmail.getPasswordKey());
            }
        });
        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            String otp = String.valueOf(Utils.generateOTP(6));
            String subject = "Subject: Otp-in to Receive Updates from Swami Sales";
            message.setSubject(subject);
            String body = "Dear "+user.getUsername()+",<br/>" +
                    "<br/>" +
                    "You recently requested a login otp for your Swami Sales account. <br/>" +
                    "<br/>" +
                    "Your one-time password (OTP) is: <b>"+otp+"</b><br/>" +
                    "<br/>" +
                    "Please use this OTP to verify your identity and complete the password reset process. <br/>" +
                    "<br/>" +
                    "<b>Important:</b><br/>" +
                    "<br/>" +
                    "* Do not share this OTP with anyone.<br/>" +
                    "* If you did not request this OTP, please ignore this email.<br/>" +
                    "<br/>" +
                    "If you have any issues or require further assistance, please contact our customer support team at support@swamisales.com.\n" +
                    "<br/>" +
                    "Thank you,<br/>" +
                    "The Swami Sales Team<br/>";
            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);
            userHbRepository.updateOtp(user.getEmail(),otp);
            sent = true;
        }
        catch (MessagingException mex)
        {
            mex.printStackTrace();
        }
        return  sent;
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
        storeDto.setSubCategoryId(userDto.getSubCategoryId());
        storeDto.setCategoryId(userDto.getCategoryId());
        return storeDto;
    }

    @Transactional(rollbackOn = {MyException.class, RuntimeException.class})
    public Map<String, Object> createOrUpdateUser(UserDto userDto, User loggedUser) throws MyException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        // Before operation validate fields
        List<String> requiredFields = new ArrayList<>(List.of("username", "contact", "email", "userType"));;
                switch (userDto.getUserType()) {
                    case "R":
                        break;
                    case "S", "SA":
                        requiredFields.add("groupList");
                        break;
                    case "W" :
                        requiredFields.addAll(List.of("city","state"));
                        break;
                    default :
                        throw new IllegalStateException("Unexpected value: " + userDto.getUserType());
        };


        Map<String, Object> nullFields = Utils.verifyFieldsBeforeCreateUser(userDto,requiredFields);
        if(nullFields != null) return nullFields;

        Map<String, Object> responseObj = new HashMap<>();
        StoreDto storeDto = null;

        /** condition for create or update super user */
        if((loggedUser.getId() !=GlobalConstant.suId && userDto.getUserType().equals("SA") && !loggedUser.getSlug().equals(userDto.getSlug()))) throw new MyException("You don't have permissions to create a admin contact to administrator.");
        Utils.mobileAndEmailValidation(userDto.getEmail(),userDto.getContact(),"Not a valid user's _ recheck your and user's _.");
        /** condition who can update a staff */
        Utils.canUpdateAStaff(userDto.getSlug(),userDto.getUserType(),loggedUser);

        String username = Utils.isValidName( userDto.getUsername(),"user");
        userDto.setUsername(username);

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
            int isAssigned = permissionHbRepository.assignGroupsToUser(userDto.getUserId(), userDto.getGroupList(),loggedUser);
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



    public String updateProfileImage(MultipartFile profileImage,String slug,User loggedUser) throws IOException {
        User user = userRepository.findUserBySlug(slug);
        Utils.canUpdateAStaff(slug,user.getUserType(),loggedUser);
        String imageName = UUID.randomUUID().toString().substring(0,5)+"_"+ Objects.requireNonNull(profileImage.getOriginalFilename()).replaceAll(" ","_");
        if (!Utils.isValidImage(imageName)) return null;
        String dirPath = profilePath+slug+"/";
        File dir = new File(dirPath);
        if(!dir.exists()) dir.mkdirs();
        profileImage.transferTo(new File(dirPath+imageName));
        int isUpdated =  userHbRepository.updateProfileImage(slug,imageName);
        if(isUpdated > 0) return imageName;
        return null;
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
