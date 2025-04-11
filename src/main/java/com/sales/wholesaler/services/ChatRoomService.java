package com.sales.wholesaler.services;


import com.sales.dto.ChatRoomDto;
import com.sales.entities.ChatRoom;
import com.sales.entities.ChatRoomUser;
import com.sales.entities.User;
import com.sales.exceptions.NotFoundException;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatRoomService extends WholesaleRepoContainer {


    public List<ChatRoom> getAllChatRoom(){
        return chatRoomRepository.findAll();
    }


    @Transactional(rollbackOn = {NotFoundException.class, RuntimeException.class,Exception.class})
    public ChatRoom createRoom(ChatRoomDto chatRoomDto) {
        // saving room
        ChatRoom chatRoom = ChatRoom.builder()
                .slug(UUID.randomUUID().toString())
                .name(chatRoomDto.getName())
                .description(chatRoomDto.getDescription())
                .createdAt(Utils.getCurrentMillis())
                .updatedAt(Utils.getCurrentMillis())
                .build();
        ChatRoom inserted =  chatRoomRepository.save(chatRoom);


        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
        for(String slug :chatRoomDto.getUsers()) {
            User user = wholesaleUserRepository.findUserBySlug(slug);
            if(user == null) throw new NotFoundException("Chat users are not valid.");
            ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                    .user(user)
                    .roomId(inserted.getId())
                    .createdAt(Utils.getCurrentMillis())
                    .build();
            chatRoomUsers.add(chatRoomUser);
        }
        // this will auto save
        inserted.setChatRoomUsers(chatRoomUsers);

        return  inserted;
    }



    @Transactional
    public int updateRoom(ChatRoomDto chatRoomDto,User loggedUser) {
        return chatRoomHbRepository.updateChatRoom(chatRoomDto,loggedUser);
    }




}
