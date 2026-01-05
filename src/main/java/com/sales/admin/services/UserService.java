package com.sales.admin.services;


import com.sales.admin.repositories.*;
import com.sales.claims.AuthUser;
import com.sales.dto.*;
import com.sales.entities.Store;
import com.sales.entities.StorePermissions;
import com.sales.entities.SupportEmail;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.exceptions.NotFoundException;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import static com.sales.helpers.PaginationHelper.getPageable;
import static com.sales.specifications.UserSpecifications.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserHbRepository userHbRepository;
    private final PermissionHbRepository permissionHbRepository;
    private final StorePermissionsRepository storePermissionsRepository;
    private final SupportEmailsRepository supportEmailsRepository;
    private final StoreRepository storeRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final StoreService storeService;
    private final PaginationService paginationService;

    @Value("${profile.absolute}")
    private String profilePath;

    @Value("${default.password}")
    private String password;


    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow( () -> new UsernameNotFoundException("User not found."));
    }

    public User findByEmailAndPassword(String email,String password) {
        return userRepository.findByEmailAndPassword(email,password).orElseThrow(() -> new UsernameNotFoundException("User not fond."));
    }

    public User findUserByOtpAndEmail(UserDto userDto) {
        logger.debug("Finding user by OTP and email: {}", userDto.getEmail());
        // Here password key has otp
        return  userRepository.findUserByOtpAndEmail(userDto.getEmail(),userDto.getPassword());
    }


    public Integer getUserIdBySlug(String slug){
        logger.debug("Getting user ID by slug: {}", slug);
        return userRepository.getUserIdBySlug(slug);
    }

    public void resetOtp(String email){
        logger.debug("Resetting OTP for email: {}", email);
        userHbRepository.updateOtp(email,"");
    }


    public boolean sendOtp(UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Sending OTP to email: {}", userDto.getEmail());
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(userDto,List.of("email"));

        boolean sent = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your mail server
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        User user = userRepository.findUserByEmail(userDto.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user == null) throw new IllegalArgumentException("We are unable to send mail on this mail id "+userDto.getEmail());
        
        String recipient = user.getEmail();
        SupportEmail supportEmail =  supportEmailsRepository.findSupportEmailBySupportType("SUPPORT");
        if(Objects.isNull(supportEmail)) {
            throw new InternalError("Support email is not found. please contact administrator.");
        }
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
            logger.error("Fetching Exception : {} ",mex.getMessage());
        }
        return  sent;
    }



    public Map<String, Integer> getUserCounts () {
        logger.debug("Getting user counts");
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.totalUserCount());
        responseObj.put("active",userRepository.optionUserCount("A"));
        responseObj.put("deactive",userRepository.optionUserCount("D"));
        return responseObj;
    }

    public Map<String, Integer> getRetailersCounts () {
        logger.debug("Getting retailers counts");
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("R"));
        responseObj.put("active",userRepository.getUserWithUserType("A","R"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","R"));
        return responseObj;
    }

    public Map<String, Integer> getWholesalersCounts () {
        logger.debug("Getting wholesalers counts");
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("W"));
        responseObj.put("active",userRepository.getUserWithUserType("A","W"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","W"));
        return responseObj;
    }

    public Map<String, Integer> getStaffsCounts () {
        logger.debug("Getting staffs counts");
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("S"));
        responseObj.put("active",userRepository.getUserWithUserType("A","S"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","S"));
        return responseObj;
    }

    public Map<String, Integer> getAdminsCounts () {
        logger.debug("Getting admins counts");
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",userRepository.getUserWithUserType("SA"));
        responseObj.put("active",userRepository.getUserWithUserType("A","SA"));
        responseObj.put("deactive",userRepository.getUserWithUserType("D","SA"));
        return responseObj;
    }




    public Page<User> getAllUser(UserSearchFilters filters, AuthUser loggedUser) {
        logger.debug("Getting all users with filters: {}", filters);
       String notUserType = null;
        if(filters.getUserType().equals("SA") && loggedUser.getId() !=GlobalConstant.suId){
            filters.setUserType(null);
        }else if(filters.getUserType().equals("A")) {
            notUserType = "SA";
            filters.setUserType(null);
        }
        Specification<User> specification = Specification.allOf(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
            .and(greaterThanOrEqualFromDate(filters.getFromDate()))
            .and(lessThanOrEqualToToDate(filters.getToDate()))
            .and(isStatus(filters.getStatus()))
                        .and(hasSlug(filters.getSlug()))
                        .and(notSuperAdmin())
                        .and(hasUserType(filters.getUserType()))
                        .and(hasNotUserType(notUserType))
        );

        Pageable pageable = getPageable(logger,filters);
        return userRepository.findAll(specification, pageable);
    }




    public StoreDto userDtoToStoreDto(UserDto userDto){
        logger.debug("Converting UserDto to StoreDto: {}", userDto);
        StoreDto storeDto = new StoreDto();
        storeDto.setStreet(userDto.getStreet());
        storeDto.setZipCode(userDto.getZipCode());
        storeDto.setStoreName(userDto.getStoreName());
        storeDto.setStoreEmail(userDto.getStoreEmail());
        storeDto.setDescription(userDto.getDescription());
        storeDto.setCity(userDto.getCity());
        storeDto.setState(userDto.getState());
        storeDto.setStorePhone(userDto.getStorePhone());
/*        storeDto.setStoreSlug(userDto.getStoreSlug());*/
        storeDto.setSubCategoryId(userDto.getSubCategoryId());
        storeDto.setCategoryId(userDto.getCategoryId());
        return storeDto;
    }


    public void validateRequiredFieldsBeforeCreateUser(UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Validating required fields before creating user: {}", userDto);
        List<String> requiredFields = new ArrayList<>(List.of("username", "contact", "email", "userType"));
        switch (userDto.getUserType()) {
            case "R":
                break;
            case "S", "SA":
                requiredFields.add("groupList");
                break;
            case "W" : /* We checked these fields during create store but
                 also checking for wholesaler also because we don't unnecessary query hit on db */
                requiredFields.addAll(List.of(
                        "city",
                        "state",
                        "zipCode",
                        "street",
                        "storeName",
                        "storeEmail",
                        "description",
                        "categoryId",
                        "subCategoryId",
                        "storePhone"
                ));
                break;
            default :
                throw new IllegalStateException("Unexpected value: " + userDto.getUserType());
        };

        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(userDto,requiredFields);
    }

    public void validateRequiredFieldsBeforeUpdateUser(UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Validating required fields before updating user: {}", userDto);
        List<String> requiredFields = new ArrayList<>(List.of("username", "contact", "email","slug"));
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(userDto,requiredFields);
    }


    /**
        @Important : There are two types of user @loggedUser and @requestUser both are different
     */
    @Transactional(rollbackOn = {MyException.class, RuntimeException.class})
    public Map<String, Object> createOrUpdateUser(UserDto userDto, AuthUser loggedUser,String path) throws MyException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Creating or updating user: {}", userDto);
        Map<String, Object> responseObj = new HashMap<>();
        StoreDto storeDto;
        // condition for create or update superuser
        if((loggedUser.getId() !=GlobalConstant.suId &&
                userDto.getUserType().equals("SA") &&
                !loggedUser.getSlug().equals(userDto.getSlug())
        )) throw new PermissionDeniedDataAccessException("You don't have permissions to create a admin contact to administrator.",null);

        Utils.mobileAndEmailValidation(
                userDto.getEmail(),
                userDto.getContact(),
                "Not a valid user's _ recheck your and user's _."
        );
        // condition who can update a staff
        Utils.canUpdateAStaff(userDto.getSlug(),
                userDto.getUserType(),
                loggedUser
        );

        String username = Utils.isValidName(userDto.getUsername(),"user");
        userDto.setUsername(username);

        // Updating existing user
        if (!Utils.isEmpty(userDto.getSlug()) || path.contains("update")) {
            logger.debug("We are going to update the user.");
            // Verify required fields before create user
            validateRequiredFieldsBeforeUpdateUser(userDto);
            int isUpdated = updateUser(userDto, loggedUser);
             Integer userId = userRepository.getUserIdBySlug(userDto.getSlug());
             userDto.setUserId(userId);

             // if request user is a Wholesaler
            if (userDto.getUserType().equals("W")){
                storeDto =  userDtoToStoreDto(userDto);
                storeDto.setUserSlug(userDto.getSlug());
                storeService.createOrUpdateStore(storeDto, loggedUser,path);
            }
            if (isUpdated > 0) {
                responseObj.put(ConstantResponseKeys.MESSAGE, "Successfully updated.");
                responseObj.put(ConstantResponseKeys.STATUS, 200);
            } else {
                responseObj.put(ConstantResponseKeys.MESSAGE, "Nothing to updated. may be something went wrong");
                responseObj.put(ConstantResponseKeys.STATUS, 404);
                // return responseObj;
            }
        } else {    // Creating new user
            logger.debug("We are going to create the user.");
            // Verify required fields before create user
            validateRequiredFieldsBeforeCreateUser(userDto);

            User updatedUser = createUser(userDto, loggedUser);
            userDto.setUserId(updatedUser.getId());
            logger.debug("{} : {}", userDto.getUserType(), userDto.getUserSlug());

            // if logged user not same to request user and make sure request user must be Wholesaler
            if((userDto.getUserId() != loggedUser.getId()) &&  userDto.getUserType().equals("W"))
            {
                storeDto =  userDtoToStoreDto(userDto);
                storeDto.setUserSlug(updatedUser.getSlug());
                storeService.createOrUpdateStore(storeDto,loggedUser,path);

                // Providing default permissions to wholesaler
                List<Integer> defaultPermissions = storePermissionsRepository.getAllDefaultPermissionsIds();
                int isAssigned = permissionHbRepository.assignPermissionsToWholesaler(userDto.getUserId(),defaultPermissions);
                if (isAssigned < 1)
                    throw new MyException("Something went wrong during update wholesaler's permissions. please contact to administrator.");
            }

            if(!Utils.isEmpty(userDto.getUserType()) &&  (userDto.getUserType().equals("S") || userDto.getUserType().equals("W"))) {
                // updating default pagination settings also for both kind of user "W" and "S"
                paginationService.setUserDefaultPaginationForSettings(updatedUser);
            }

            if (updatedUser.getId() > 0) {
                responseObj.put(ConstantResponseKeys.RES, updatedUser);
                responseObj.put(ConstantResponseKeys.MESSAGE, "Successfully inserted.");
                responseObj.put(ConstantResponseKeys.STATUS, 201);
            } else {
                responseObj.put(ConstantResponseKeys.MESSAGE, "Nothing to save. may be something went wrong please contact to administrator.");
                responseObj.put(ConstantResponseKeys.STATUS, 500);
            }
        }

        /** going to update user's groups ------------> only for staffs and super admin has group permissions */
        if (
            (userDto.getUserId() != loggedUser.getId()) && // Logged user can't change self groups
            (userDto.getUserType().equals("SA") || userDto.getUserType().equals("S"))  // Make sure user must be Super Admin or Staff
        ) {
            int isAssigned = permissionHbRepository.assignGroupsToUser(userDto.getUserId(), userDto.getGroupList(),loggedUser);
            if (isAssigned < 1)
                throw new MyException("Something went wrong during update user's groups. please contact to administrator.");
        }
        return responseObj;
    }


    @Transactional
    public User createUser(UserDto userDto, AuthUser loggedUser) {
        logger.debug("Creating user: {}", userDto);
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
    public int updateUser(UserDto userDto, AuthUser loggedUser) {
        logger.debug("Updating user: {}", userDto);
        return userHbRepository.updateUser(userDto,loggedUser);
    }

    public User getUserDetail(String slug ,AuthUser loggedUser){
        logger.debug("Getting user detail for slug: {}", slug);
       User user = userRepository.findUserBySlug(slug);
        if(user !=null && (user.getId() !=GlobalConstant.suId || loggedUser.getId() == GlobalConstant.suId )){
            return user;
        }
        return null;
    }

    public User getUserDetail(String slug){
        logger.debug("Getting user detail for slug: {}", slug);
        User user = userRepository.findUserBySlug(slug);
        if(user !=null && (user.getId() !=GlobalConstant.suId )){
            return user;
        }
        return null;
    }

    @Transactional(rollbackOn = {PermissionDeniedDataAccessException.class,IllegalArgumentException.class,RuntimeException.class,Exception.class})
    public int deleteUserBySlug(DeleteDto deleteDto,AuthUser loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Deleting user by slug: {}", deleteDto.getSlug());
        // if there is any required field null, then this will throw IllegalArgumentException
        Utils.checkRequiredFields(deleteDto, List.of("slug"));

        String slug = deleteDto.getSlug();
        User user = getUserDetail(slug);
        if(user == null) throw new NotFoundException("User not found to delete.");
        // if logged user doesn't have permission, then can't delete it this will throw an Exception
        Utils.canUpdateAStaff(slug,user.getUserType(),loggedUser);
        if(user.getUserType().equals("W")) storeService.deleteStoreByUserId(user.getId());
        return userHbRepository.deleteUserBySlug(slug);
    }


    @Transactional
    public int resetPasswordByUserSlug(PasswordDto passwordDto,AuthUser loggedUser){
        logger.debug("Resetting password for user with slug: {}", passwordDto.getSlug());
        password = !Utils.isEmpty(password) ?  passwordDto.getPassword() : password;
        User user = userRepository.findUserBySlug(passwordDto.getSlug());
        Utils.canUpdateAStaff(passwordDto.getSlug(),user.getUserType(),loggedUser);
        user.setPassword(password);
        return user.getId();
    }

    @Transactional
    public int updateStatusBySlug(StatusDto statusDto,AuthUser loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Updating status for user with slug: {}", statusDto.getSlug());
        try {
            // if there is any required field null, then this will throw IllegalArgumentException
            Utils.checkRequiredFields(statusDto, List.of("status", "slug"));

            switch (statusDto.getStatus()){
                case "A" , "D":
                    String status = statusDto.getStatus();
                    /* Getting user and updating status */
                    User user = userRepository.findUserBySlug(statusDto.getSlug());
                    if (user == null) throw new NotFoundException("No user not found to update.");
                    Utils.canUpdateAStaffStatus(statusDto.getSlug(),user.getUserType(),loggedUser);
                    user.setStatus(status);
                    // if userType is wholesaler no need to go further
                    if(!user.getUserType().equals("W")){
                        user = userRepository.save(user);
                        return user.getId();
                    }
                    /* Getting store and updating the status */
                    Store store = storeRepository.findStoreByUserId(user.getId());
                    store.setStatus(status);
                    store = storeRepository.save(store);
                    if (store.getId() > 0)
                        user = userRepository.save(user);
                    return user.getId();
                default:
                    throw new IllegalArgumentException("Status must be A or D.");
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }



    public String updateProfileImage(MultipartFile profileImage, String slug, AuthUser loggedUser) throws IOException {
        logger.debug("Updating profile image for user with slug: {}", slug);
        User user = userRepository.findUserBySlug(slug);
        Utils.canUpdateAStaff(slug,user.getUserType(),loggedUser);
        String imageName = UUID.randomUUID().toString().substring(0,5)+"_"+ Objects.requireNonNull(profileImage.getOriginalFilename()).replaceAll(" ","_");
        if (!Utils.isValidImage(imageName)) return null;
        String dirPath = profilePath+slug+GlobalConstant.PATH_SEPARATOR;
        File dir = new File(dirPath);
        if(!dir.exists()) dir.mkdirs();
        profileImage.transferTo(new File(dirPath+imageName));
        int isUpdated =  userHbRepository.updateProfileImage(slug,imageName);
        if(isUpdated > 0) return imageName;
        return null;
    }



    public List<Integer> getUserGroupsIdBySlug(String slug) {
        logger.debug("Getting user groups ID by slug: {}", slug);
        return userRepository.getUserGroupsIdBySlug(slug);
    }


    public List<Integer> getWholesalerAllAssignedPermissions(String slug) {
        logger.debug("Getting wholesaler all assigned permissions for slug: {}", slug);
        User user = getUserDetail(slug);
        if(user==null) return null;
        return storePermissionsRepository.getAllAssignedPermissionsIdByUserId(user.getId());
    }

    public Map<String,Object> getWholesalerAllPermissions() {
        logger.debug("Getting wholesaler all permissions");
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
    public Map<String,Object> updateWholesalerPermissions(UserDto userDto) throws MyException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Updating wholesaler permissions for user with slug: {}", userDto.getSlug());
        // Validating required field is there is any null field this will throw Exception
        Utils.checkRequiredFields(userDto,List.of("slug","userType","storePermissions"));

        Map<String,Object> responseObject = new HashMap<>();
        if (Utils.isEmpty(userDto.getSlug()) || !userDto.getUserType().equals("W")) throw  new MyException("There is nothing to update.");
        User user = getUserDetail(userDto.getSlug());
        if (user == null) throw new NotFoundException("User not found.");
        int isUpdated = permissionHbRepository.assignPermissionsToWholesaler(user.getId(), userDto.getStorePermissions());
        if (isUpdated > 0) {
            responseObject.put(ConstantResponseKeys.MESSAGE, "All permissions have been updated successfully.");
            responseObject.put(ConstantResponseKeys.STATUS, 200);
        } else {
            responseObject.put(ConstantResponseKeys.MESSAGE, "We don't found any user to update");
            responseObject.put(ConstantResponseKeys.STATUS, 404);
        }
        return responseObject;
    }
}
