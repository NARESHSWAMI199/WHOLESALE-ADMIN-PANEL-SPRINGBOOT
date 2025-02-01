package com.sales.wholesaler.repository;


import com.sales.entities.ChatUser;
import com.sales.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser,Integer> {

    @Query("from ChatUser where userId=:userId")
   List<ChatUser> getChatUserByUserId(Integer userId);


    ChatUser findByUserIdAndChatUser(Integer userId , User chatUser);


}
