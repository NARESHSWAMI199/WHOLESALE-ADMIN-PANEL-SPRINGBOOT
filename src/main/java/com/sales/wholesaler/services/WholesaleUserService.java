package com.sales.wholesaler.services;


import com.sales.dto.*;
import com.sales.entities.ServicePlan;
import com.sales.entities.SupportEmail;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
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
import java.util.*;

import static com.sales.specifications.UserSpecifications.*;


@Service
public class WholesaleUserService extends WholesaleRepoContainer {

    @Autowired
    WholesaleServicePlanService wholesaleServicePlanService;

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

    public User findUserByOtpAndSlug(UserDto userDto) {
        return wholesaleUserRepository.findUserByOtpAndSlug(userDto.getSlug(),userDto.getPassword());
    }

    public User findUserByOtpAndEmail(UserDto userDto) {
        return  wholesaleUserRepository.findUserByOtpAndEmail(userDto.getEmail(),userDto.getPassword());
    }

    public void resetOtp(String email){
        wholesaleUserHbRepository.updateOtp(email,"");
    }


    public boolean sendOtp(UserDto userDto){
        boolean sent = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your mail server
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
        return  sent;
    }


    public User findUserBySlug(String slug){
        return   wholesaleUserRepository.findUserBySlug(slug);
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

        String username = Utils.isValidName( userDto.getUsername(),"user");
        userDto.setUsername(username);
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


    public String updateProfileImage(MultipartFile profileImage,String slug,User loggedUser) throws IOException {
        User user = wholesaleUserRepository.findUserBySlug(slug);
        Utils.canUpdateAStaff(slug,user.getUserType(),loggedUser);
        String imageName = UUID.randomUUID().toString().substring(0,5)+"_"+ Objects.requireNonNull(profileImage.getOriginalFilename()).replaceAll(" ","_");
        if (!Utils.isValidImage(imageName)) return null;
        String dirPath = profilePath+slug+"/";
        File dir = new File(dirPath);
        if(!dir.exists()) dir.mkdirs();
        profileImage.transferTo(new File(dirPath+imageName));
        int isUpdated =  wholesaleUserHbRepository.updateProfileImage(slug,imageName);
        if(isUpdated > 0) return imageName;
        return null;
    }



    public User addNewUser(UserDto userDto) {
        /* '_' replaced by actual error message in mobileAndEmailValidation */
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
        User insertedUser =  wholesaleUserRepository.save(user);
        /* assigning a free plan to user */
        ServicePlan defaultServicePlan = wholesaleServicePlanRepository.getDefaultServicePlan();
        if(defaultServicePlan != null) {
            wholesaleServicePlanService.assignUserPlan(insertedUser.getId(), defaultServicePlan.getId());
        }
        /** Sending a mail to user for email validation. */
        if (!sendOtp(userDto)){
            throw new MyException("User was created successfully. but we facing issue some issue during sending otp. Make sure your email address was correct.");
        }
        return user;
    }



    public int updateLastSeen(User loggedUser) {
       return wholesaleUserHbRepository.updatedUserLastSeen(loggedUser.getSlug());
    }

    public boolean updateSeenMessages(MessageDto message){
        return wholesaleUserHbRepository.updateSeenMessage(message);
    }

    public int deleteMessage(User loggedUser,MessageDto messageDto){
        switch (messageDto.getIsDeleted()){
            case "S": // delete sender's message
                if(!messageDto.getSender().equals(loggedUser.getSlug())) return  0;
                messageDto.setIsSenderDeleted("H");
                messageDto.setIsReceiverDeleted(null);
                break;
            case "SY" : // Force delete sender's message
                if(!messageDto.getSender().equals(loggedUser.getSlug())) return  0;
                messageDto.setIsSenderDeleted("Y");
                messageDto.setIsReceiverDeleted(null);
                break;
            case "R": // delete receiver's message
                if(!messageDto.getReceiver().equals(loggedUser.getSlug())) return 0;
                messageDto.setIsSenderDeleted(null);
                messageDto.setIsReceiverDeleted("H");
                break;
            case "RY": // Force delete receiver's message
                if(!messageDto.getReceiver().equals(loggedUser.getSlug())) return 0;
                messageDto.setIsSenderDeleted(null);
                messageDto.setIsReceiverDeleted("Y");
                break;
            case "B": // Delete from both side
                if(!messageDto.getReceiver().equals(loggedUser.getSlug()) && !messageDto.getSender().equals(loggedUser.getSlug())) return 0;
                messageDto.setIsSenderDeleted("H");
                messageDto.setIsReceiverDeleted("H");
                break;
            default:
                return 0;
        }
      return wholesaleUserHbRepository.deleteChat(messageDto);
    }


    /** Getting all retailers and wholesalers for chat purpose */

    public Page<User> getAllUsers(UserSearchFilters filters, User loggedUser) {
        Specification<User> specification = Specification.where(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
                    .and(isStatus("A"))
                    .and(hasNotUserType("W"))
                    .and(hasNotUserType("R"))
                    .and(notHasSlug(loggedUser.getSlug()))
        );

        Pageable pageable = getPageable(filters);
        return wholesaleUserRepository.findAll(specification,pageable);
    }



}
