package com.sales.admin.services;


import com.sales.dto.GroupDto;
import com.sales.dto.SearchFilters;
import com.sales.entities.Group;
import com.sales.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sales.specifications.GroupSpecifications.*;

@Service
public class GroupService extends RepoContainer {



    public Page<Group> getALlGroups(SearchFilters filters) {
        Specification<Group> specification = Specification.where(
                (containsName(filters.getSearchKey()))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(hasSlug(filters.getSlug()))
        );
        Pageable pageable = getPageable(filters);
        return groupRepository.findAll(specification,pageable);
    }


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
            Group insertedGroup =  groupRepository.save(group);
            if (insertedGroup.getId() > 0){
                permissionHbRepository.updatePermissions(insertedGroup.getId(),groupDto.getPermissions());
                responseObject.put("message" , groupDto.getName()+" successfully created.");
                responseObject.put("status" , 200);
            }else{
                responseObject.put("message" , "Something went wrong during create "+groupDto.getName()+" group.");
                responseObject.put("status" , 400);
            }
        }
        return responseObject;
    }


    public Map<String,Object> findGroupBySlug(String slug){
        Group group = groupRepository.findGroupBySlug(slug);
        if(group == null) return null;
        List<Map<String,Object>> groupWithPermission =  permissionRepository.getGroupPermissionByGroupId(group.getId());
        Map<String,Object> formattedGroup = null;
        List<Map<String,Object>> permissionList = new ArrayList<Map<String,Object>>();
        for (Map<String, Object> map : groupWithPermission) {
            Map<String,Object> permissionData = new HashMap<String,Object>();
            permissionData.put("permission" ,map.get("permission"));
            permissionData.put("id" ,map.get("id"));
            permissionList.add(permissionData);
        }
        if(permissionList.size() > 0){
            formattedGroup = new HashMap<String,Object>();
            formattedGroup.put("group",(String) groupWithPermission.get(0).get("name"));
            formattedGroup.put("permissions",permissionList);
        }
        return formattedGroup;
    }


    public Map<String,List<Object>> getAllPermissions(){
      List<Map<String,Object>> permissionList = permissionRepository.getAllPermissions();
      Map<String,List<Object>> formattedPermissions = new HashMap<>();
      for(Map<String,Object> permission : permissionList){
          String key = (String) permission.get("permission_for");
          if(formattedPermissions.containsKey(key)){
              List<Object> addedPermissions = formattedPermissions.get(key);
              addedPermissions.add(permission);
              formattedPermissions.put(key ,addedPermissions);
          }else {
              List<Object> newPermissions = new ArrayList<>();
              newPermissions.add(permission);
              formattedPermissions.put(key ,newPermissions);
          }
      }
      return formattedPermissions;
    }


}
