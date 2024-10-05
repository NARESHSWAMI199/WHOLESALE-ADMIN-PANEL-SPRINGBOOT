package com.sales.admin.controllers;


import com.sales.dto.PasswordDto;
import com.sales.dto.StatusDto;
import com.sales.dto.UserDto;
import com.sales.dto.UserSearchFilters;
import com.sales.entities.User;
import com.sales.global.GlobalConstant;
import com.sales.jwtUtils.JwtToken;
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

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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
    public ResponseEntity<Page<User>> getAllUsers(HttpServletRequest request,@RequestBody UserSearchFilters searchFilters, @PathVariable(required = true) String userType, HttpServletRequest httpServletRequest) {
        searchFilters.setUserType(userType);
        User logggedUser = (User) request.getAttribute("user");
        Page<User> userPage = userService.getAllUser(searchFilters,logggedUser);
        return new ResponseEntity<>(userPage, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> findByEmailAndPassword(@RequestBody UserDto userDetails) {
        logger.info("====================== ADMIN LOGIN PROCESS STARTED ======================");
        Map<String, Object> responseObj = new HashMap<>();
        User user = userService.findByEmailAndPassword(userDetails);
        if (user == null) {
            responseObj.put("message", "invalid credentials.");
            responseObj.put("status", 401);
            return new ResponseEntity<>(responseObj, HttpStatus.UNAUTHORIZED);
        } else if (user.getStatus().equalsIgnoreCase("A")) {
            responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
            responseObj.put("message", "success");
            responseObj.put("status", 200);
            responseObj.put("user", user);
            return new ResponseEntity<>(responseObj, HttpStatus.OK);
        } else {
            responseObj.put("message", "You are blocked by admin");
            responseObj.put("status", 401);
            return new ResponseEntity<>(responseObj, HttpStatus.OK);
        }
    }


    @Transactional
    @PostMapping(value = {"/add", "/update"})
    public ResponseEntity<Map<String, Object>> register(HttpServletRequest request, @RequestBody UserDto userDto) throws Exception {
        Map<String,Object> responseObj = new HashMap<>();
        User logggedUser = (User) request.getAttribute("user");
        responseObj = userService.createOrUpdateUser(userDto, logggedUser);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getDetailUser(HttpServletRequest request,@PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        User logggedUser = (User) request.getAttribute("user");
        User user = userService.getUserDetail(slug,logggedUser);
        if (user != null) {
            responseObj.put("res", user);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "Please check you parameters not a valid request.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @Transactional
    @GetMapping("/delete/{slug}")
    public ResponseEntity<Map<String, Object>> deleteUserBySlug(HttpServletRequest request,@PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        User logggedUser = (User) request.getAttribute("user");
        int isUpdated = userService.deleteUserBySlug(slug,logggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "User has been successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @Transactional
    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> resetUserPasswordBySlug(HttpServletRequest request ,@RequestBody PasswordDto passwordDto) {
        Map<String,Object> responseObj = new HashMap<>();
        User logggedUser = (User) request.getAttribute("user");
        int isUpdated = userService.resetPasswordByUserSlug(passwordDto,logggedUser);
        if (isUpdated > 0 || logggedUser.getId() == GlobalConstant.suId) {
            responseObj.put("message", "User password has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to update.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> stockSlug(HttpServletRequest request,@RequestBody StatusDto statusDto) {
        Map<String,Object> responseObj = new HashMap<>();
        User logggedUser = (User) request.getAttribute("user");
        int isUpdated = userService.updateStatusBySlug(statusDto,logggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's status has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }





    @Transactional
    @PostMapping("/update_profile/{slug}")
    public ResponseEntity<Map<String, Object>> updateProfileImage(HttpServletRequest request, @RequestPart MultipartFile profileImage, @PathVariable String slug ) {
        Map responseObj = new HashMap();
        try {
            User logggedUser = (User) request.getAttribute("user");
            int  isUpdated = userService.updateProfileImage(profileImage,slug,logggedUser);
            if(isUpdated > 0) {
                responseObj.put("status" , 200);
                responseObj.put("message" , "Profile image successfully updated");
            }else {
                responseObj.put("status" , 400);
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
        Map responseObj = new HashMap();
        List<Integer> groupsIds = userService.getUserGroupsIdBySlug(slug);
        User logggedUser = (User) request.getAttribute("user");
        if (groupsIds.size() > 0 ) {
            responseObj.put("content", groupsIds);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is no groups.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<Map<String,Object>>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

}




