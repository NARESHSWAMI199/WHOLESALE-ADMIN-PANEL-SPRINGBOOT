package com.sales.wholesaler.repository;


import com.sales.dto.ChatRoomDto;
import com.sales.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ChatRoomHbRepository {

    @Autowired
    private EntityManager entityManager;


    public int updateChatRoom(ChatRoomDto chatRoomDto, User loggedUser) {
        // TODO : Must add updated_by to db side and also here.
        String hql = "update set name=:name,description =:description where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("name", chatRoomDto.getName());
        query.setParameter("description",chatRoomDto.getDescription());
        query.setParameter("slug",chatRoomDto.getSlug());
        return query.executeUpdate();
    }


}
