package com.sales.admin.repositories;

import com.sales.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Integer> {

    @Query(" from Chat where (sender=:sender and receiver=:receiver) or (sender=:receiver and receiver=:sender)")
    List<Chat> getChatBySenderKeyOrReceiverKey(String sender,String receiver);


    @Query(" select count(id) as count from Chat where (sender=:sender and receiver=:receiver) and seen=false ")
    Integer getUnSeenChatsCount(String sender,String receiver);

}
