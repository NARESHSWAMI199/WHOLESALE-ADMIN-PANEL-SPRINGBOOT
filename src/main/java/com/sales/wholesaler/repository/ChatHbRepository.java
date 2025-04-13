package com.sales.wholesaler.repository;


import com.google.gson.Gson;
import com.sales.entities.Chat;
import com.sales.entities.DeletedChat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class ChatHbRepository {

    @Autowired
    private EntityManager entityManager;



    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private DeletedChatRepository deletedChatRepository;


    public boolean updateMessageToSent(long id){
        String hql = "update Chat set isSent='S' where id =:id ";
        Query query = entityManager.createQuery(hql);
        query.setParameter("id",id);
        return query.executeUpdate() > 0;
    }

    public void moveAndDeleteChat(String sender,String receiver){
        // @Note we're moving the chats in separate table then will delete it from the actual Chat table
        List<Chat> chats = chatRepository.getChatBySenderKeyOrReceiverKey(sender, receiver);
        Gson gson = new Gson();
        List<DeletedChat> deletedChats = new ArrayList<>();
        for(Chat chat : chats){
            DeletedChat deletedChat  = gson.fromJson(gson.toJson(chat),DeletedChat.class);
            deletedChats.add(deletedChat);
        }
        deletedChatRepository.saveAll(deletedChats);
        deleteChats(sender,receiver);
    }

    public void deleteChats(String sender , String receiver){
        String hql = "delete from Chat where (sender=:sender and receiver=:receiver) or (sender=:receiver and receiver=:sender)";
        Query query = entityManager.createQuery(hql);
        query.setParameter("sender",sender);
        query.setParameter("receiver",receiver);
        query.executeUpdate();
    }





}
