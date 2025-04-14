package com.sales.wholesaler.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ChatHbRepository {

    @Autowired
    private EntityManager entityManager;



    @Autowired
    private ChatRepository chatRepository;


    public boolean updateMessageToSent(long id){
        String hql = "update Chat set isSent='S' where id =:id ";
        Query query = entityManager.createQuery(hql);
        query.setParameter("id",id);
        return query.executeUpdate() > 0;
    }


    public void deleteChats(String sender , String receiver){
        String hql = "update Chat set " +
                "isSenderDeleted = case when sender = :sender and receiver = :receiver then 'Y' else isSenderDeleted end, " +
                "isReceiverDeleted = case when receiver = :sender and sender = :receiver then 'Y' else isReceiverDeleted end " +
                "where sender = :sender or receiver = :sender";
        Query query = entityManager.createQuery(hql);
        query.setParameter("sender",sender);
        query.setParameter("receiver",receiver);
        query.executeUpdate();
    }





}
