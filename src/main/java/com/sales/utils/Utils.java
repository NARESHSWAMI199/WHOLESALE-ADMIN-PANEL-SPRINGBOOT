package com.sales.utils;

import com.sales.admin.repositories.UserRepository;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.exceptions.UserException;
import com.sales.global.GlobalConstant;
import com.sales.jwtUtils.JwtToken;
import com.sales.wholesaler.repository.WholesaleUserRepository;
import com.sales.wholesaler.services.WholesaleUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Utils {


    public static Long getCurrentMillis(){
        long millis = new java.util.Date().getTime();
        return millis;
    }

    public static String mobileRegex = "^[789]\\d{9}$";

    public static String getMillisToDate(Long millis){
        DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        Date date = new Date(millis);
        return format.format(date);
    }


    public static boolean isEmpty(String string){
        return (string ==null || string.trim().equals(""));
    }


    public static String aesEncrypt(String Data, String secretKey) {
        try {
            Key key = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES"); // Default uses ECB PKCS5Padding
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = cipher.doFinal(Data.getBytes());
            String encryptedValue = java.util.Base64.getEncoder().encodeToString(encVal);
            return encryptedValue;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while encrypting: " + e.getMessage());
        }
        return null;
    }

    public static String aesDecrypt(String strToDecrypt, String secretKey) {
        try {
            Key key = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while encrypting: " + e.getMessage());
        }
        return null;
    }

    public static boolean isValidImage(String image){
        String IMAGE_PATTERN =
                "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg|jfif|webp))$)";
        Pattern pattern =  Pattern.compile(IMAGE_PATTERN);
        Matcher matcher = pattern.matcher(image);
        return matcher.matches();
    }


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isValidEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    public static void mobileAndEmailValidation(String email ,String contact,String errorMessage) throws MyException {
        if (Utils.isEmpty(contact) || !contact.matches(Utils.mobileRegex)) throw new MyException(errorMessage.replaceAll("_","mobile number") +  " ["+contact+"]") ;
        if (Utils.isEmpty(email) || !isValidEmail(email)) throw new MyException(errorMessage.replaceAll("_","email address") + " ["+email+"]") ;
    }

    public static void canUpdateAStaff(String slug ,String userType, User loggedUser){
        if((!loggedUser.getUserType().equals("SA") &&
            loggedUser.getId() != GlobalConstant.suId) &&
            userType.equals("S") &&
            !loggedUser.getSlug().equals(slug)) {
            throw new MyException("You don't have permissions to create or update a staff contact to administrator.");
        }
    }






    public static String isValidName(final String name,String flag) throws MyException {
        String NAME_PATTERN =
                "^[a-zA-Z](?=.{1,100}$)[A-Za-z_& ]*(?:\\h+[A-Z][A-Za-z]*)*$";
        if(flag.equalsIgnoreCase("user")){
            /* item can hold more then 0 char and less then 28 and don't  support &*/
            //NAME_PATTERN = "^[A-Z](?=.{1,28}$)[A-Za-z_ ]*(?:\\h+[A-Z][A-Za-z]*)*$";
        } else if (flag.equalsIgnoreCase("item")) {
            NAME_PATTERN = "^[a-zA-Z][a-zA-Z0-9\\s,().-]*$";
        }
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        System.out.println(matcher.matches());
        if(!matcher.matches()){
            String message ="";
            String neededSyntax = "Special symbols like : ^*$+?[]()| are not allowed.";
            if(flag.equals("user")){
                message = "Not a valid username";
                System.out.println(message);
                throw  new MyException(message + " "+neededSyntax  );
            }
            message = "Not a valid "+flag+" name.";
            System.out.println(message);
            throw  new MyException(message + " "+neededSyntax);
        }
        return name;
    }


    public static int generateOTP(int length) {
        Random random = new Random();
        int otp = 0;
        for (int i = 0; i < length; i++) {
            otp = otp * 10 + random.nextInt(10);
        }
        return otp;
    }


    public static User getUserFromRequest(HttpServletRequest request, JwtToken jwtToken, WholesaleUserService userService){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("request url : "+request.getRequestURI());
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String slug = jwtToken.getSlugFromToken(token);
                /* get user by slug. */
                User user = userService.findUserBySlug(slug);
                if (user.getIsDeleted().equals("Y")) {
                    throw new UserException("User is not found.");
                } else if (user.getStatus().equals("D")) {
                    throw new UserException("User is not active.");
                }
                return user;
            }
            throw new UserException("Invalid authorization.");
        }catch (Exception e){
            throw new UserException(e.getMessage());
        }
    }

}
