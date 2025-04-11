package com.sales.wholesaler.controller;

import com.sales.dto.ChatRoomDto;
import com.sales.entities.ChatRoom;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("chat_room")
public class ChatRoomController extends WholesaleServiceContainer{


    @GetMapping("all")
    public ResponseEntity<List<ChatRoom>> getAllCharRooms() {
        List<ChatRoom> chatRooms = chatRoomService.getAllChatRoom();
        return new ResponseEntity<>(chatRooms, HttpStatusCode.valueOf(200));
    }

    @PostMapping("add")
    public ResponseEntity<Map<String,String>> addNewChatRoom(@RequestBody ChatRoomDto chatRoomDto) {
        Map<String,String> result = new HashMap<>();
        ChatRoom chatRoom = chatRoomService.createRoom(chatRoomDto);
        result.put("roomId",chatRoom.getSlug());
        return new ResponseEntity<>(result,HttpStatus.valueOf(201));
    }

    @PostMapping("update")
    public ResponseEntity<Map<String, Object>> updateChatRoom(@RequestBody ChatRoomDto chatRoomDto, HttpServletRequest request) {
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> result = new HashMap<>();
        int isUpdated = chatRoomService.updateRoom(chatRoomDto, loggedUser);
        if(isUpdated > 0){
            result.put("message","Chat room updated successfully");
            result.put("status",200);
        }else{
            result.put("message","No room found for : "+chatRoomDto.getSlug());
            result.put("status",404);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }




}
