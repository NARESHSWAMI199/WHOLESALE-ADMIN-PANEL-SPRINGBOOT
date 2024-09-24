package com.sales.admin.services;


import com.sales.dto.GroupDto;
import com.sales.entities.Group;
import com.sales.entities.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PermissionService extends RepoContainer {



    public Map<String,Object> createOrUpdateGroup(GroupDto groupDto, User loggededUser) {
        Map<String,Object> responseObject = new HashMap<>();
        if (groupDto.getSlug() != null && !groupDto.getSlug().trim().equals("")) {
            int isUpdated =  permissionHbRepository.updateGroup(groupDto);
            if (isUpdated > 0){
                responseObject.put("message" , "The group has been updated successfully.");
                responseObject.put("status" , 201);
            }else{
                responseObject.put("message" , "Something went wrong during update "+groupDto.getName()+" group.");
                responseObject.put("status" , 400);
            }
        }else {
            Group group = new Group(loggededUser);
            group.setName(groupDto.getName());
            Group insertedGroup =  permissionRepository.save(group);
            if (insertedGroup.getId() > 0){
                responseObject.put("message" , groupDto.getName()+" successfully created.");
                responseObject.put("status" , 200);
            }else{
                responseObject.put("message" , "Something went wrong during create "+groupDto.getName()+" group.");
                responseObject.put("status" , 400);
            }
        }
        return responseObject;
    }


    public Group findGroupBySlug(String slug){
       return permissionRepository.findGroupBySlug(slug);
    }



}
