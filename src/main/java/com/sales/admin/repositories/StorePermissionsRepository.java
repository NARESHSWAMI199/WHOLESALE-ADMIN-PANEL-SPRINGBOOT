package com.sales.admin.repositories;


import com.sales.entities.StorePermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@Repository
public interface StorePermissionsRepository extends JpaRepository<StorePermissions,Long> {

    @Query("from StorePermissions where defaultPermission='Y'")
    List<StorePermissions> getAllDefaultPermissions();


    @Query("select id from StorePermissions where defaultPermission='Y'")
    List<Integer> getAllDefaultPermissionsIds();

    @Query(value = "select access_url from store_permissions sp left join wholesaler_permissions wp on wp.permission_id = sp.id where wp.user_id=:userId ",nativeQuery = true)
    Set<String> getAllAssignedPermissionByUserId(@Param("userId") Integer userId);


}
