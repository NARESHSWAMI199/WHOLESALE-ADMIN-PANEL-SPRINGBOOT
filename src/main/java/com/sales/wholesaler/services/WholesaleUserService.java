package com.sales.wholesaler.services;


import com.sales.cachemanager.services.UserCacheService;
import com.sales.dto.*;
import com.sales.entities.ServicePlan;
import com.sales.entities.SupportEmail;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
public class WholesaleUserService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleUserService.class);

    @Autowired
    private WholesaleServicePlanService wholesaleServicePlanService;

    @Autowired
    private WholesalePaginationService wholesalePaginationService;

    @Autowired
    UserCacheService userCacheService;

    @Value("${profile.absolute}")
    String profilePath;

    @Autowired
    WholesaleStoreService wholesaleStoreService;

    @Value("${default.password}")
    String password;

    public User findByEmailAndPassword(Map<String,String> param) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting findByEmailAndPassword method with param: {}", param);
        // Validating required fields. If their we found any required field is null, this will throw an Exception
        Utils.checkRequiredFields(param,List.of("email","password"));

        String email = param.get("email");
        String password = param.get("password");
        User user = wholesaleUserRepository.findByEmailAndPassword(email,password);
        logger.info("Completed findByEmailAndPassword method");
        userCacheService.saveCacheUser(user);
        return user;
    }

    public User findUserByOtpAndSlug(UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting findUserByOtpAndSlug method with userDto: {}", userDto);
        // Validating required fields. If their we found any required field is null, this will throw an Exception
        Utils.checkRequiredFields(userDto,List.of("slug","password"));
        User user = wholesaleUserRepository.findUserByOtpAndSlug(userDto.getSlug(),userDto.getPassword());
        logger.info("Completed findUserByOtpAndSlug method");
        userCacheService.saveCacheUser(user);
        return user;
    }

    public User findUserByOtpAndEmail(UserDto userDto) {
        logger.info("Starting findUserByOtpAndEmail method with userDto: {}", userDto);
        User user = wholesaleUserRepository.findUserByOtpAndEmail(userDto.getEmail(),userDto.getPassword());
        logger.info("Completed findUserByOtpAndEmail method");
        userCacheService.saveCacheUser(user);
        return user;
    }

    public void resetOtp(String email){
        logger.info("Starting resetOtp method with email: {}", email);
        wholesaleUserHbRepository.updateOtp(email,"");
        logger.info("Completed resetOtp method");
    }

    public boolean sendOtp(UserDto userDto){
        logger.info("Starting sendOtp method with userDto: {}", userDto);
        boolean sent = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace it with your mail server
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        User user = null;
        if(userDto.getEmail() == null){
            user = wholesaleUserRepository.findUserBySlug(userDto.getSlug());
        }else{
            user = wholesaleUserRepository.findUserByEmail(userDto.getEmail());
        }
        if (user == null) return  false;

        String recipient = user.getEmail();

        SupportEmail supportEmail =  wholesaleSupportEmailsRepository.findSupportEmailBySupportType("SUPPORT");
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
            wholesaleUserHbRepository.updateOtp(user.getEmail(),otp);
            sent = true;
        }
        catch (MessagingException mex)
        {
            mex.printStackTrace();
        }
        logger.info("Completed sendOtp method");
        return  sent;
    }

    public User findUserBySlug(String slug){
        logger.info("Starting findUserBySlug method with slug: {}", slug);
        User user = wholesaleUserRepository.findUserBySlug(slug);
        logger.info("Completed findUserBySlug method");
        return user;
    }

    public StoreDto userDtoToStoreDto(UserDto userDto) {
        logger.info("Starting userDtoToStoreDto method with userDto: {}", userDto);
        StoreDto storeDto = new StoreDto();
        storeDto.setStoreName(userDto.getStoreName());
        storeDto.setStoreEmail(userDto.getStoreEmail());
        storeDto.setDescription(userDto.getDescription());
        storeDto.setCity(userDto.getCity());
        storeDto.setState(userDto.getState());
        storeDto.setStorePhone(userDto.getStorePhone());
        logger.info("Completed userDtoToStoreDto method");
        return storeDto;
    }

    public Map<String, Object> updateUserProfile(UserDto userDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting updateUserProfile method with userDto: {}, loggedUser: {}", userDto, loggedUser);
        // Validating required fields. If there we found any required field is null, this will throw an Exception
        Utils.checkRequiredFields(userDto,List.of("slug","username","email","contact"));

        Map<String, Object> responseObj = new HashMap<>();

        Utils.mobileAndEmailValidation(
                userDto.getEmail(),
                userDto.getContact(),
                "Not a valid user's _ recheck your and user's _."
        );

        String username = Utils.isValidName( userDto.getUsername(),"user");
        userDto.setUsername(username);
        int isUpdated = updateUser(userDto, loggedUser); // Update operation
        if (isUpdated > 0) {
            responseObj.put("message", "Successfully updated.");
            responseObj.put(ConstantResponseKeys.STATUS, 200);
        } else {
            responseObj.put("message", "No user found to update.");
            responseObj.put(ConstantResponseKeys.STATUS, 404);
        }
        logger.info("Completed updateUserProfile method");
        return responseObj;
    }

    @Transactional
    public int updateUser(UserDto userDto, User loggedUser) {
        logger.info("Starting updateUser method with userDto: {}, loggedUser: {}", userDto, loggedUser);
        int updateCount = wholesaleUserHbRepository.updateUser(userDto, loggedUser); // Update operation
        logger.info("Completed updateUser method");
        return updateCount;
    }

    public User getUserDetail(String slug) {
        logger.info("Starting getUserDetail method with slug: {}", slug);
        User user = wholesaleUserRepository.findUserBySlug(slug);
        logger.info("Completed getUserDetail method");
        return user;
    }

    @Transactional
    public User resetPasswordByUserSlug(PasswordDto passwordDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting resetPasswordByUserSlug method with passwordDto: {}, loggedUser: {}", passwordDto, loggedUser);
        // Validating required fields. If their we found any required field is null, this will throw an Exception
        Utils.checkRequiredFields(passwordDto,List.of("password"));
        if(passwordDto.getPassword().isEmpty()) throw new IllegalArgumentException("password can't by empty or blank");
        loggedUser.setPassword(passwordDto.getPassword());
        User updatedUser = wholesaleUserRepository.save(loggedUser); // Update operation
        logger.info("Completed resetPasswordByUserSlug method");
        return updatedUser;
    }

    public String updateProfileImage(MultipartFile profileImage,User loggedUser) throws IOException {
        logger.info("Starting updateProfileImage method with profileImage: {}, loggedUser: {}", profileImage, loggedUser);
        String slug = loggedUser.getSlug();
        String imageName = UUID.randomUUID().toString().substring(0,5)+"_"+ Objects.requireNonNull(profileImage.getOriginalFilename()).replaceAll(" ","_");
        if (!Utils.isValidImage(imageName)) throw new IllegalArgumentException("Not a valid Image.");
        String dirPath = profilePath+slug+ GlobalConstant.PATH_SEPARATOR;
        File dir = new File(dirPath);
        if(!dir.exists()) dir.mkdirs();
        profileImage.transferTo(new File(dirPath+imageName));
        int isUpdated =  wholesaleUserHbRepository.updateProfileImage(slug,imageName); // Update operation
        if(isUpdated > 0) {
            logger.info("Completed updateProfileImage method");
            return imageName;
        }
        logger.info("Completed updateProfileImage method");
        return null;
    }

    public User addNewUser(UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting addNewUser method with userDto: {}", userDto);
        // Validating required fields. If their we found any required field is null, this will throw an Exception
        Utils.checkRequiredFields(userDto,List.of("username","email","password","contact"));

        // '_' replaced by actual error message in mobileAndEmailValidation
        Utils.mobileAndEmailValidation(userDto.getEmail(), userDto.getContact(),"Not a valid _");
        String username = Utils.isValidName(userDto.getUsername(),"user");
        User user = User.builder()
            .username(username)
            .email(userDto.getEmail())
            .password(userDto.getPassword())
            .contact(userDto.getContact())
            .slug(UUID.randomUUID().toString())
            .status("A")
            .isDeleted("N")
            .userType("W")
            .createdAt(Utils.getCurrentMillis())
            .updatedAt(Utils.getCurrentMillis())
            .build();
        User insertedUser =  wholesaleUserRepository.save(user); // Create operation
        // assigning a free plan to user
        ServicePlan defaultServicePlan = wholesaleServicePlanRepository.getDefaultServicePlan();
        if(defaultServicePlan != null) {
            wholesaleServicePlanService.assignUserPlan(insertedUser.getId(), defaultServicePlan.getId());
        }
        // Sending mail to user for email validation.
        if (!sendOtp(userDto)){
            throw new MyException("User was created successfully. but we facing issue some issue during sending otp. Make sure your email address was correct.");
        }
        logger.info("Completed addNewUser method");

        // updating default pagination settings also for both kind of user "W" and "R"
        wholesalePaginationService.setUserDefaultPaginationForSettings(insertedUser);

        // Save user in redis
        userCacheService.saveCacheUser(insertedUser);
        return insertedUser;
    }

    public int updateLastSeen(User loggedUser) {
        logger.info("Starting updateLastSeen method with loggedUser: {}", loggedUser);
        int updateCount = wholesaleUserHbRepository.updatedUserLastSeen(loggedUser.getSlug()); // Update operation
        logger.info("Completed updateLastSeen method");
        return updateCount;
    }

    public boolean updateSeenMessages(MessageDto message){
        logger.info("Starting updateSeenMessages method with message: {}", message);
        boolean isUpdated = wholesaleUserHbRepository.updateSeenMessage(message); // Update operation
        logger.info("Completed updateSeenMessages method");
        return isUpdated;
    }


    /** Getting all retailers and wholesalers for chat purpose */
    public Page<User> getAllUsers(UserSearchFilters filters, User loggedUser) {
        logger.info("Starting getAllUsers method with filters: {}, loggedUser: {}", filters, loggedUser);
        Specification<User> specification = Specification.allOf(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
                    .and(isStatus("A"))
                    .and(hasUserType("W").or(hasUserType("R")))
                    .and(notHasSlug(loggedUser.getSlug()))
        );

        Pageable pageable = getPageable(filters);
        Page<User> users = wholesaleUserRepository.findAll(specification,pageable);
        logger.info("Completed getAllUsers method");
        return users;
    }

}
