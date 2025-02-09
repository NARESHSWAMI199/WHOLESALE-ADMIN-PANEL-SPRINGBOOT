package com.sales.admin.controllers;


import com.sales.dto.*;
import com.sales.entities.User;
import com.sales.global.GlobalConstant;
import com.sales.jwtUtils.JwtToken;
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
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin/auth")
public class UserController extends ServiceContainer {

    @Autowired
    JwtToken jwtToken;

    @PostMapping("/{userType}/all")
    public ResponseEntity<Page<User>> getAllUsers(HttpServletRequest request,@RequestBody UserSearchFilters searchFilters, @PathVariable(required = true) String userType) {
        searchFilters.setUserType(userType);
        User loggedUser = (User) request.getAttribute("user");
        Page<User> userPage = userService.getAllUser(searchFilters,loggedUser);
        return new ResponseEntity<>(userPage, HttpStatus.OK);
    }


    // Required params for login in swagger ui
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(schema = @Schema(example = """
                    {
                       "email" : "string",
                       "password" : "string"
                    }
                    """)
    ))
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> findByEmailAndPassword(@RequestBody UserDto userDetails) {
        logger.info("====================== ADMIN LOGIN PROCESS STARTED ======================");
        Map<String, Object> responseObj = new HashMap<>();
        User user = userService.findByEmailAndPassword(userDetails);
        if (user == null) {
            responseObj.put("message", "Invalid credentials.");
            responseObj.put("status", 401);
        } else if (user.getStatus().equalsIgnoreCase("A")) {
            responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
            responseObj.put("message", "success");
            responseObj.put("user", user);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "You are blocked by admin");
            responseObj.put("status", 401);
        }
        logger.info("====================== ADMIN LOGIN PROCESS END ======================");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    // Required params for otp login in swagger ui
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(schema = @Schema(example = """
                {
                   "email" : "string",
                   "password" : "(otp) string"
                }
                """)
    ))
    @PostMapping("/login/otp")
    public ResponseEntity<Map<String, Object>> findUserByOtp(@RequestBody UserDto userDetails) {
        logger.info("====================== ADMIN LOGIN OTP PROCESS STARTED ======================");
        Map<String, Object> responseObj = new HashMap<>();
        User user = userService.findUserByOtpAndEmail(userDetails);
        if (user == null) {
            responseObj.put("message", "Wrong otp password.");
            responseObj.put("status", 401);
        } else if (user.getStatus().equalsIgnoreCase("A")) {
            responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
            responseObj.put("message", "Successfully logged in.");
            responseObj.put("user", user);
            responseObj.put("status", 200);
            userService.resetOtp(user.getEmail());
        } else {
            responseObj.put("message", "You are blocked by admin.");
            responseObj.put("status", 401);
        }
        logger.info("====================== ADMIN LOGIN OTP PROCESS END ======================");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(schema = @Schema(example = """
            {
               "email" : "string",
            }
            """)
    ))
    @PostMapping("sendOtp")
    public ResponseEntity<Map<String,Object>> sendOtp(@RequestBody UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("====================== START SENDING OTP STARTED ======================");
        Map<String,Object> responseObj = new HashMap<>();
        boolean sendOtp = userService.sendOtp(userDto);
        if(sendOtp)  {
            responseObj.put("status",200);
            responseObj.put("message", "Otp sent successfully");
        }else {
            responseObj.put("status",400);
            responseObj.put("message", "We facing some issue to send otp to this mail ->"+userDto.getEmail());
        }
        logger.info("====================== END SENDING OTP STARTED ======================");
        return  new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }




    // For add and update user
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema( description = "If you going to update must add slug",
            example = """
            {
                    "slug" : "(only during update) string",
                    "email" : "string",
                    "username" : "string",
                    "userType"  : "W|R|S|SA",
                    "contact" : "string",
                    "city" : "cityId",
                    "state" : "stateId",
                    "street" : "string",
                    "storeName" : "string",
                    "storeEmail" : "string",
                    "description" : "string",
                    "categoryId" : 0,
                    "subCategoryId"  : 0,
                    "zipCode" : "string",
                    "storePhone" : "string"
                }
            """)
    ))
    @Transactional
    @PostMapping(value = {"/add", "/update"})
    public ResponseEntity<Map<String, Object>> register(HttpServletRequest request, @RequestBody UserDto userDto) throws Exception {
        logger.info("====================== START ADD OR UPDATE USER STARTED ======================");
        User loggedUser = (User) request.getAttribute("user");
        String path = request.getRequestURI();
        Map<String,Object> responseObj = userService.createOrUpdateUser(userDto, loggedUser,path);
        logger.info("====================== END ADD OR UPDATE USER STARTED ======================");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getDetailUser(HttpServletRequest request,@PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        User user = userService.getUserDetail(slug,loggedUser);
        if (user != null) {
            responseObj.put("res", user);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "User not found.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteUserBySlug(HttpServletRequest request, @RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isUpdated = userService.deleteUserBySlug(deleteDto,loggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "User has been successfully deleted.");
            responseObj.put("status", 201);
        } else {
            responseObj.put("message", "No user found to delete");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @Transactional
    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> resetUserPasswordBySlug(HttpServletRequest request ,@RequestBody PasswordDto passwordDto) {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isUpdated = userService.resetPasswordByUserSlug(passwordDto,loggedUser);
        if (isUpdated > 0 || loggedUser.getId() == GlobalConstant.suId) {
            responseObj.put("message", "User password has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to update.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> stockSlug(HttpServletRequest request,@RequestBody StatusDto statusDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isUpdated = userService.updateStatusBySlug(statusDto,loggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "User's status updated successfully.");
            responseObj.put("status", 201);
        } else {
            responseObj.put("message", "No user found to update.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }





    @Transactional
    @PostMapping("/update_profile/{slug}")
    public ResponseEntity<Map<String, Object>> updateProfileImage(HttpServletRequest request, @RequestPart MultipartFile profileImage, @PathVariable String slug ) {
        Map<String,Object> responseObj = new HashMap<>();
        try {
            User loggedUser = (User) request.getAttribute("user");
            String  imageName = userService.updateProfileImage(profileImage,slug,loggedUser);
            if(imageName!=null) {
                responseObj.put("status" , 200);
                responseObj.put("imageName",imageName);
                responseObj.put("message" , "Profile image successfully updated");
            }else {
                responseObj.put("status" , 406);
                responseObj.put("message" , "Not a valid profile image");
            }
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }

    @Value("${profile.get}")
    String filePath;

    @GetMapping("/profile/{slug}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable(required = true) String filename ,@PathVariable String slug) throws Exception {
        Path path = Paths.get(filePath +"/"+slug+"/"+ filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }

    @GetMapping("/groups/{slug}")
    public ResponseEntity<Map<String,Object>> getUserGroupsIdsBySlug(HttpServletRequest request,@PathVariable String slug){
        Map<String,Object> responseObj = new HashMap<>();
        List<Integer> groupsIds = userService.getUserGroupsIdBySlug(slug);
        if (!groupsIds.isEmpty()) {
            responseObj.put("content", groupsIds);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is no groups.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<Map<String,Object>>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }




    @GetMapping("wholesale/permissions/{slug}")
    public ResponseEntity<Map<String,Object>> getAllAssignedPermissionsForWholesaler(HttpServletRequest request,@PathVariable String slug){
        Map<String, Object> wholesalerAllPermissions = userService.getWholesalerAllPermissions();
        List<Integer> permissions =  userService.getWholesalerAllAssignedPermissions(slug);
        Map<String,Object> responseObj = new HashMap<>();
        if (permissions != null ) {
            responseObj.put("assigned", permissions);
            responseObj.put("allPermissions", wholesalerAllPermissions);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is no permission for this user.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj,  HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(example = """
            {
               "slug" : "string",
               "userType" : "string",
               "storePermissions" : "[permissionIds list]"
            }
            """)
    ))
    @Transactional
    @PostMapping("wholesaler/permissions/update")
    public ResponseEntity<Map<String,Object>> updateWholesalerPermissions(HttpServletRequest request, @RequestBody UserDto userDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        User loggedUser =  (User)request.getAttribute("user");
        Map<String,Object> response= userService.updateWholesalerPermissions(userDto,loggedUser);
        return new ResponseEntity<>(response, HttpStatus.valueOf((Integer) response.get("status")));
    }


}




