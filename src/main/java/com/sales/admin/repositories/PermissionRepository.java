package com.sales.admin.repositories;


import com.sales.entities.GroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<GroupPermission,Long> {


    @Query(value = """
            select 
                p.access_url
            from user_groups ug
            right join group_permissions gp on gp.group_id = ug.group_id
            right join permissions p on p.id = gp.permission_id 
            where ug.user_id = :userId
            """,nativeQuery = true)
    Set<String> getUserAllPermission(@Param("userId") int userId);



    @Query(value = """
            select 
                g.name,
                p.id,
                p.permission 
            from group_permissions gp 
            left join `groups` g on g.id = gp.group_id
            left join permissions p on p.id= gp.permission_id 
            where g.id =:groupId
            """,nativeQuery = true)
    List<Map<String,Object>> getGroupPermissionByGroupId(@Param("groupId") Integer groupId);



    @Query(value = "select * from permissions",nativeQuery = true)
    List<Map<String,Object>> getAllPermissions();

//    @Query
//    List<Map<Integer, String>> getAllPermissionByGroupId(@Param("groupId") Integer groupId);



}
