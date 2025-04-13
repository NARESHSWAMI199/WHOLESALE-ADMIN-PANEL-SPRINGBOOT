package com.sales.wholesaler.repository;

import com.sales.entities.DeletedChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedChatRepository extends JpaRepository<DeletedChat,Integer> {
}
