package com.sales.admin.repositories;

import com.sales.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Integer> {

    @Query(" from Chat where senderKey=:senderKey or senderKey = :receiverKey")
    List<Chat> getChatBySenderKeyOrReceiverKey(String senderKey,String receiverKey);

}
