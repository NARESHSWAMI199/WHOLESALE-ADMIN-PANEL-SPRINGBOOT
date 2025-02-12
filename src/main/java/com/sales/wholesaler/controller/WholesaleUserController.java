package com.sales.wholesaler.controller;


import com.sales.dto.PasswordDto;
import com.sales.dto.UserDto;
import com.sales.dto.UserSearchFilters;
import com.sales.entities.Store;
import com.sales.entities.User;
import com.sales.global.GlobalConstant;
import com.sales.jwtUtils.JwtToken;
import com.sales.utils.Utils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wholesale/auth")
public class WholesaleUserController extends WholesaleServiceContainer {

    @Autowired
    JwtToken jwtToken;




    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(
            example = """
                    {
                        "email" : "string",
                        "password" : "string"
                    }
                    """
    )))
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> findByEmailAndPassword(@RequestBody Map<String,String> param) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Object> responseObj = new HashMap<>();
            logger.info("=============LOGIN PROCESSES STARTED =====================");
        User user = wholesaleUserService.findByEmailAndPassword(param);
        if (user == null) {
            responseObj.put("message", "invalid credentials.");
            responseObj.put("status", 401);
        }else if(!Utils.isEmpty(user.getOtp())) {
            responseObj.put("message", "User exist but not verified. You can login via otp.");
            responseObj.put("status", 401);
        }else if (user.getStatus().equalsIgnoreCase("A")) {
            responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
            Store store = wholesaleStoreService.getStoreByUserId(user.getId());
            responseObj.put("message", "success");
            responseObj.put("user", user);
            responseObj.put("store", store);
            responseObj.put("status", 200);
        }else {
            responseObj.put("message", "You are blocked by admin");
            responseObj.put("status", 401);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @PostMapping("/login/otp")
    public ResponseEntity<Map<String, Object>> loginUserViaOtp (@RequestBody UserDto userDetails) {
        logger.info("====================== ADMIN LOGIN OTP PROCESS STARTED ======================");
        Map<String, Object> responseObj = new HashMap<>();
        User user = wholesaleUserService.findUserByOtpAndEmail(userDetails);
        if (user == null) {
            responseObj.put("message", "Wrong otp password.");
            responseObj.put("status", 401);
        } else if (user.getStatus().equalsIgnoreCase("A")) {
            responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
            Store store = wholesaleStoreService.getStoreByUserId(user.getId());
            responseObj.put("message", "success");
            responseObj.put("user", user);
            responseObj.put("store", store);
            responseObj.put("status", 200);
            wholesaleUserService.resetOtp(user.getEmail());
        } else {
            responseObj.put("message", "You are blocked by admin");
            responseObj.put("status", 401);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(
            example = """
                    {
                        "slug" : "string",
                        "password" : "otp-password"
                    }
                    """
    )))

    @PostMapping("validate-otp")
    public ResponseEntity<Map<String, Object>> validateUserOtp(@RequestBody UserDto userDetails) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("====================== OTP VALIDATION STARTED ======================");
        Map<String, Object> responseObj = new HashMap<>();
        User user = wholesaleUserService.findUserByOtpAndSlug(userDetails);
        if (user == null) {
            responseObj.put("message", "Wrong otp password.");
            responseObj.put("status", 401);
        } else if (user.getStatus().equalsIgnoreCase("A")) {
            responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
            responseObj.put("message", "success");
            responseObj.put("user", user);
            Store store = wholesaleStoreService.getStoreByUserId(user.getId());
            responseObj.put("store", store);
            responseObj.put("status", 200);
            wholesaleUserService.resetOtp(user.getEmail());
        } else {
            responseObj.put("message", "You are blocked by admin");
            responseObj.put("status", 401);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("sendOtp")
    public ResponseEntity<Map<String,Object>> sendOtp(HttpServletRequest request, @RequestBody UserDto userDto){
        Map<String,Object> responseObj = new HashMap<>();
        boolean sendOtp = wholesaleUserService.sendOtp(userDto);
        if(sendOtp)  {
            responseObj.put("status",200);
            responseObj.put("message", "Otp sent successfully");
        }else {
            responseObj.put("status",400);
            responseObj.put("message", "We facing some issue to send otp to this mail ->"+userDto.getEmail());
        }
        return  new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    // For add and update user
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema( description = "If you going to update must add slug",
                    example = """
                {
                    "slug" : "string",
                    "email" : "string",
                    "username" : "string",
                    "contact" : "string"
                }
            """)
            ))
    @PostMapping(value = {"/update"})
    public ResponseEntity<Map<String, Object>> updateAuth(HttpServletRequest request, @RequestBody UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> responseObj = wholesaleUserService.updateUserProfile(userDto, loggedUser);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }

    @GetMapping(value = {"/detail","/detail/{slug}"})
    public ResponseEntity<Map<String, Object>> getDetailUser(@PathVariable(required = false) String slug, HttpServletRequest request) {
        Map<String,Object> responseObj = new HashMap<>();
        User user = null;
        if(slug == null){
            user = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        }else {
            user = wholesaleUserService.findUserBySlug(slug);
        }
        if(slug == null){
            Store store = wholesaleStoreService.getStoreByUserSlug(user.getId());
            responseObj.put("store", store);
        }
        responseObj.put("user", user);
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @Transactional
    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> resetUserPasswordBySlug(HttpServletRequest request ,@RequestBody PasswordDto passwordDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        User updatedUser = wholesaleUserService.resetPasswordByUserSlug(passwordDto,loggedUser);
        responseObj.put("res",updatedUser);
        responseObj.put("message", "User password has been successfully updated.");
        responseObj.put("status", 201);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @PostMapping("/update_profile")
    public ResponseEntity<Map<String, Object>> updateProfileImage(HttpServletRequest request, @RequestPart MultipartFile profileImage) throws IOException {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        String  imageName = wholesaleUserService.updateProfileImage(profileImage,loggedUser);
        if(imageName!=null) {
            responseObj.put("imageName",imageName);
            responseObj.put("message" , "Profile image successfully updated");
            responseObj.put("status" , 201);
        }else {
            responseObj.put("status" , 406);
            responseObj.put("message" , "Not a valid profile image");
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }

    @Value("${profile.get}")
    String filePath;

    @GetMapping("/profile/{slug}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable(required = true) String filename , @PathVariable String slug) throws Exception {
        Path path = Paths.get(filePath +"/"+slug+"/"+ filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }


    // For add and update user
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema( description = "If you going to update must add slug",
                example = """
                {
                    "email" : "string",
                    "username" : "string",
                    "password' : "string",
                    "contact" : "string"
                }
            """)
    ))
    @PostMapping(value = {"add","register"})
    public ResponseEntity<Map<String,Object>> addNewUser(@RequestBody UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> result = new HashMap<>();
        User insertedUser = wholesaleUserService.addNewUser(userDto);
        result.put("user",insertedUser);
        result.put("message", "User created successfully");
        result.put("status", 200);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


    @GetMapping("last-seen")
    public ResponseEntity<Map<String,Object>> updateUserLastSeen(HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User)request.getAttribute("user");
        int isUpdated = wholesaleUserService.updateLastSeen(loggedUser);
        loggedUser.setOnline(false);
        wholesaleUserService.updateLastSeen(loggedUser);
        GlobalConstant.onlineUsers.put(loggedUser.getSlug(), loggedUser);
        if(isUpdated > 0){
            result.put("message", "User's last seen successfully updated.");
            result.put("status", 201);
        }else{
            result.put("message","Something went wrong during updating last seen of user");
            result.put("status",500);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));

    }



    /** Returning a list of users where users are retailer and wholesaler only for chat purpose.*/

    @PostMapping("chat/users")
    public ResponseEntity<Page<User>> getAllChatUser(HttpServletRequest request, @RequestBody UserSearchFilters userSearchFilters){
        User loggedUser = (User) request.getAttribute("user");
        Page<User> allUsers = wholesaleUserService.getAllUsers(userSearchFilters, loggedUser);
        return new ResponseEntity<>(allUsers,HttpStatus.OK);
    }



}




