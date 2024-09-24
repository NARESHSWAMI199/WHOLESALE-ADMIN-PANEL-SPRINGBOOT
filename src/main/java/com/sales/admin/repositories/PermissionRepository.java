package com.sales.admin.repositories;


import com.sales.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Group,Long> {


    @Query(value = "select " +
            "p.access_url " +
            "from user_groups ug " +
            "right join group_permissions gp on gp.group_id = ug.group_id " +
            "right join permissions p on p.id = gp.permission_id where ug.user_id = :userId",nativeQuery = true)
    Set<String> getUserAllPermission(@Param("userId") int userId);



    Group findGroupBySlug(String slugId);


}
