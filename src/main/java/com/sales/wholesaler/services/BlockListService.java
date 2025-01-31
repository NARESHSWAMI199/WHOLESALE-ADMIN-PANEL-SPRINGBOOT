package com.sales.wholesaler.services;


import com.sales.entities.BlockedUser;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import org.springframework.stereotype.Service;

@Service
public class BlockListService extends WholesaleRepoContainer {


    public BlockedUser addAUserInBlockList(User blockingBy , String blockedUserSlug){
        User blockedUser = wholesaleUserRepository.findUserBySlug(blockedUserSlug);
        if(blockedUser == null) throw new MyException("Blocked user not exists");
        BlockedUser blockList = BlockedUser.builder()
            .userId(blockingBy.getId())
            .blockedUser(blockedUser)
            .build();
        return blockListRepository.save(blockList);
    }


    public boolean isUserExistInBlockList(User loggedUser, String receiverSlug){
        User receiver = wholesaleUserRepository.findUserBySlug(receiverSlug);
        if(receiver == null) return false;
        BlockedUser blockedUser = blockListRepository.findByUserIdAndBlockedUser(receiver.getId(),loggedUser);
        return blockedUser != null;
    }


}
