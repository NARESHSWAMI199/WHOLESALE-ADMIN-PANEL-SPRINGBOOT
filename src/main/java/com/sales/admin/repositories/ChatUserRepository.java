package com.sales.admin.repositories;


import com.sales.entities.ChatUser;
import com.sales.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser,Integer> {

    @Query("select chatUser from ChatUser where userId=:userId")
   List<User> getChatUserByUserId(Integer userId);

}
