package com.sales.wholesaler.repository;


import com.sales.entities.BlockedUser;
import com.sales.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockListRepository extends JpaRepository<BlockedUser,Integer> {
    BlockedUser findByUserIdAndBlockedUser(Integer userId, User blockedUser);
}
