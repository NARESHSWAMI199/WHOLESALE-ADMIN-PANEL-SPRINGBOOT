package com.sales.admin.repositories;


import com.sales.entities.UserPagination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaginationsRepository extends JpaRepository<UserPagination,Integer> {

    UserPagination findByUserId(Integer userId);


}
