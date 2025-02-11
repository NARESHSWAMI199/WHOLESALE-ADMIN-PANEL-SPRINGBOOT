package com.sales.utils;

import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.exceptions.NotFoundException;
import com.sales.exceptions.UserException;
import com.sales.global.GlobalConstant;
import com.sales.jwtUtils.JwtToken;
import com.sales.wholesaler.services.WholesaleUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Utils {


    public static Long getCurrentMillis(){
        long millis = new java.util.Date().getTime();
        return millis;
    }

    public static String mobileRegex = "^[6789]\\d{9}$";

    public static String getMillisToDate(Long millis){
        DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        Date date = new Date(millis);
        return format.format(date);
    }

    public static String getStringDateOnly(Long millis){
        if(millis == null) return  "null";
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
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

    public static boolean isValidPhoneNumber(String mobileNumber){
        return mobileRegex.matches(mobileNumber);
    }

    public static void mobileAndEmailValidation(String email ,String contact,String errorMessage) {
        if (Utils.isEmpty(contact) || !contact.matches(Utils.mobileRegex)) throw new IllegalArgumentException(errorMessage.replaceAll("_","mobile number") +  " ["+contact+"]") ;
        if (Utils.isEmpty(email) || !isValidEmail(email)) throw new IllegalArgumentException(errorMessage.replaceAll("_","email address") + " ["+email+"]") ;
    }

    public static void canUpdateAStaff(String slug ,String userType, User loggedUser) throws PermissionDeniedDataAccessException{
        if((!loggedUser.getUserType().equals("SA") && // if user is not a super admin
            loggedUser.getId() != GlobalConstant.suId) &&  // if user not owner
            userType.equals("S") && // but user is a staff
            !loggedUser.getSlug().equals(slug)) { // request slug equals self slug
            throw new PermissionDeniedDataAccessException("You don't have permissions to create or update a staff contact to administrator.",null);
        }
    }

    public static void canUpdateAStaffStatus(String slug ,String userType, User loggedUser) throws PermissionDeniedDataAccessException{
        if((!loggedUser.getUserType().equals("SA") && // if user is not a super admin
                loggedUser.getId() != GlobalConstant.suId) &&  // if user not owner
                userType.equals("S") && // but user is a staff
                loggedUser.getSlug().equals(slug)) { // request slug equals logged user's slug
            throw new PermissionDeniedDataAccessException("You don't have permissions to create or update a staff contact to administrator.",null);
        }
    }






    public static String isValidName(final String name,String flag) throws MyException {
        if (name == null) throw new IllegalArgumentException(flag+"'s name can't be name");
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
            throw new IllegalArgumentException(message + " "+neededSyntax);
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
        // Token from swagger because swagger not sends Authorization header in request.
        token = token == null ? request.getHeader("authToken") : token;
        System.out.println("request url : "+request.getRequestURI());
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String slug = jwtToken.getSlugFromToken(token);
                /* get user by slug. */
                User user = userService.findUserBySlug(slug);
                if (user.getIsDeleted().equals("Y")) {
                    throw new NotFoundException("User is not found.");
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


    public static String getHostUrl(HttpServletRequest request) {
        String scheme = request.getScheme(); // http or https
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        String hostUrl = scheme + "://" + serverName;

        // Append port if it's not the default port for the scheme
        if (("http".equals(scheme) && serverPort != 80)
                || ("https".equals(scheme) && serverPort != 443)) {
            hostUrl += ":" + serverPort;
        }

        return hostUrl;
    }


    public static <T> void checkRequiredFields(T  dto, List<String> requiredFields) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for (String field : requiredFields) {
            if (PropertyUtils.getProperty(dto, field) == null) {
                throw new IllegalArgumentException(field + " cannot be null");
            }
        }
    }

}
