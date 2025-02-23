package com.sales.wholesaler.repository;


import com.sales.entities.UserPagination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesaleUserPaginationsRepository extends JpaRepository<UserPagination,Integer> {
    public UserPagination findByUserId(Integer userId);
}
