package com.sales.admin.services;


import com.sales.dto.DeleteDto;
import com.sales.dto.GroupDto;
import com.sales.dto.SearchFilters;
import com.sales.dto.UserPermissionsDto;
import com.sales.entities.Group;
import com.sales.entities.User;
import com.sales.exceptions.NotFoundException;
import com.sales.global.GlobalConstant;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sales.specifications.GroupSpecifications.*;

@Service
public class GroupService extends RepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    public Page<Group> getAllGroups(SearchFilters filters, User loggedUser) {
        logger.info("Entering getAllGroups with filters: {}, loggedUser: {}", filters, loggedUser);
        Specification<Group> specification = Specification.where(
                (containsName(filters.getSearchKey()))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(hasSlug(filters.getSlug()))
                        .and(notSuperAdmin(loggedUser))
        );
        Pageable pageable = getPageable(filters);
        Page<Group> result = groupRepository.findAll(specification, pageable);
        logger.info("Exiting getAllGroups with result: {}", result);
        return result;
    }

    public void validateRequiredFieldsForGroup(GroupDto groupDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering validateRequiredFieldsForGroup with groupDto: {}", groupDto);
        List<String> requiredFields = new ArrayList<>(List.of("name"));
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(groupDto, requiredFields);
        logger.info("Exiting validateRequiredFieldsForGroup");
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NotFoundException.class, RuntimeException.class, Exception.class})
    public Map<String, Object> createOrUpdateGroup(GroupDto groupDto, User loggedUser, String path) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering createOrUpdateGroup with groupDto: {}, loggedUser: {}, path: {}", groupDto, loggedUser, path);
        Map<String, Object> responseObject = new HashMap<>();

        // Validating the required fields if there is any required field is null then this is throw Exception
        validateRequiredFieldsForGroup(groupDto);

        //Only super admin can create or update a group.
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("You don't have permission to create or update a group. Please contact a super admin",null);

        if (!Utils.isEmpty(groupDto.getSlug()) || path.contains("update")) {
            logger.info("We are going to update the group.");
            // if there is any required field null then this will throw IllegalArgumentException
            Utils.checkRequiredFields(groupDto, List.of("slug"));

            Group group = groupRepository.findGroupBySlug(groupDto.getSlug());
            if (group == null) throw new NotFoundException("No group found to update.");
            if(group.getId() == GlobalConstant.groupId && loggedUser.getId() != GlobalConstant.suId) throw  new NotFoundException("There is nothing to update.");

            // Going to update existing group.
            int isUpdated = permissionHbRepository.updateGroup(groupDto, group.getId());
            if (isUpdated > 0 && group.getId() == 0) {
                responseObject.put("message", "The group has been updated successfully. But dear " + loggedUser.getUsername() + " ji We are not able to remove permissions. from " + group.getName() + " New permissions updated.");
                responseObject.put("status", 200);
            } else if (isUpdated > 0) {
                responseObject.put("message", "The group has been updated successfully.");
                responseObject.put("status", 200);
            } else {
                responseObject.put("message", "No record found to update.");
                responseObject.put("status", 404);
            }
        } else { // Going to insert a new group
            logger.info("We are going to create the group.");
            Group group = new Group(loggedUser);
            group.setName(groupDto.getName());
            Group insertedGroup = groupRepository.save(group);
            // Updating given permissions.
            permissionHbRepository.updatePermissions(insertedGroup.getId(), groupDto.getPermissions());
            responseObject.put("res", insertedGroup);
            responseObject.put("message", groupDto.getName() + " successfully created.");
            responseObject.put("status", 201);
        }
        logger.info("Exiting createOrUpdateGroup with responseObject: {}", responseObject);
        return responseObject;
    }

    public Map<String, Object> findGroupBySlug(String slug) {
        logger.info("Entering findGroupBySlug with slug: {}", slug);
        if (Utils.isEmpty(slug)) throw new IllegalArgumentException("slug can't be null");
        Group group = groupRepository.findGroupBySlug(slug);
        if (group == null) throw new NotFoundException("No record found.");

        List<Map<String, Object>> groupWithPermission = permissionRepository.getGroupPermissionByGroupId(group.getId());

        Map<String, Object> formattedGroup = new HashMap<>();
        List<Integer> permissionList = new ArrayList<>();
        // Only getting permission id list
        for (Map<String, Object> map : groupWithPermission) {
            permissionList.add((Integer) map.get("id"));
        }
        formattedGroup.put("group", group.getName());
        formattedGroup.put("permissions", permissionList);

        logger.info("Exiting findGroupBySlug with formattedGroup: {}", formattedGroup);
        return formattedGroup;
    }

    public Map<String, List<Object>> getAllPermissions() {
        logger.info("Entering getAllPermissions");
        List<Map<String, Object>> permissionList = permissionRepository.getAllPermissions();
        Map<String, List<Object>> formattedPermissions = new HashMap<>();
        for (Map<String, Object> permission : permissionList) {
            String key = (String) permission.get("permission_for");
            if (formattedPermissions.containsKey(key)) {
                List<Object> addedPermissions = formattedPermissions.get(key);
                addedPermissions.add(permission);
                formattedPermissions.put(key, addedPermissions);
            } else {
                List<Object> newPermissions = new ArrayList<>();
                newPermissions.add(permission);
                formattedPermissions.put(key, newPermissions);
            }
        }
        logger.info("Exiting getAllPermissions with formattedPermissions: {}", formattedPermissions);
        return formattedPermissions;
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, PermissionDeniedDataAccessException.class, RuntimeException.class, Exception.class})
    public int deleteGroupBySlug(DeleteDto deleteDto, User loggedUser) throws Exception {
        logger.info("Entering deleteGroupBySlug with deleteDto: {}, loggedUser: {}", deleteDto, loggedUser);
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(deleteDto, List.of("slug"));

        //Only super admin can create or update a group.
        if (!loggedUser.getUserType().equals("SA"))
            throw new PermissionDeniedDataAccessException("You don't have permission to delete a group. Please contact a super admin", null);

        String slug = deleteDto.getSlug();
        Group group = groupRepository.findGroupBySlug(slug);
        if (group == null) throw new NotFoundException("No group found to delete");
        int result = permissionHbRepository.deleteGroupBySlug(slug, group.getId());
        logger.info("Exiting deleteGroupBySlug with result: {}", result);
        return result;
    }

    public int assignGroupsToUser(UserPermissionsDto userPermissionsDto, User loggedUser) throws Exception {
        logger.info("Entering assignGroupsToUser with userPermissionsDto: {}, loggedUser: {}", userPermissionsDto, loggedUser);
        int userId = userPermissionsDto.getUserId();
        int result = permissionHbRepository.assignGroupsToUser(userId, userPermissionsDto.getGroupList(), loggedUser);
        logger.info("Exiting assignGroupsToUser with result: {}", result);
        return result;
    }
}
