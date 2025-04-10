package com.sales.wholesaler.repository;


import com.sales.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository  extends JpaRepository<ChatRoom,Long> , JpaSpecificationExecutor<ChatRoom> {
}
