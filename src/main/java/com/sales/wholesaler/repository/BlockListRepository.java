package com.sales.wholesaler.repository;


import com.sales.entities.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockListRepository extends JpaRepository<BlockedUser,Integer> {

}
