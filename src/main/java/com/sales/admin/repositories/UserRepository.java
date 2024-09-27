package com.sales.admin.repositories;


import com.sales.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Integer> , JpaSpecificationExecutor {

    @Query(value = "from User where email=:email and password=:password and userType='S' ")
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);


    @Query(value = "from User where slug=:slug and userType='W'")
    Optional<User> findByWholesalerSLug(@Param("slug")String slug);

    User findUserBySlug(String slug);

    @Query("select id from User where slug=:slug")
    Integer getUserIdBySlug(String slug);

    @Query(value = "select count(id) as count from User")
    Integer totalUserCount();
    @Query(value = "select count(id) as count from User where status=:status")
    Integer optionUserCount(@Param("status") String status);


    @Query(value = "select count(id) as count from User where userType=:userType")
    Integer getUserWithUserType(@Param("userType") String userType);

    @Query(value = "select count(id) as count from User where status=:status and userType=:userType")
    Integer getUserWithUserType(@Param("status") String status,@Param("userType") String userType);


    @Query(value = "select ug.group_id as groupId  from user_groups ug left join `user` u on ug.user_id = u.user_id where u.slug = :slug",nativeQuery = true)
    List<Integer> getUserGroupsIdBySlug(String slug);

}
