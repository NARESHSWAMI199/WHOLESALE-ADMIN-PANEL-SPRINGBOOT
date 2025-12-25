package com.sales.wholesaler.services;


import com.sales.entities.BlockedUser;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BlockListService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(BlockListService.class);

    public BlockedUser addAUserInBlockList(User blockingBy, String blockedUserSlug) {
        logger.info("Starting addAUserInBlockList method");
        User blockedUser = wholesaleUserRepository.findUserBySlug(blockedUserSlug);
        if (blockedUser == null) {
            logger.error("Blocked user not exists");
            throw new MyException("Blocked user not exists");
        }
        BlockedUser blockList = BlockedUser.builder()
            .userId(blockingBy.getId())
            .blockedUser(blockedUser)
            .createdAt(Utils.getCurrentMillis())
            .build();
        BlockedUser savedBlockList = blockListRepository.save(blockList); // Create operation
        logger.info("Completed addAUserInBlockList method");
        return savedBlockList;
    }

    public boolean removeUserFromBlockList(Integer userId, String recipient){
        User receiver = wholesaleUserRepository.findUserBySlug(recipient);
        return blockListHbRepository.deleteUserFromBlockList(userId,receiver);
    }



    public boolean isReceiverBlockedBySender(User loggedUser, User receiver) {
        logger.info("Starting isReceiverBlockedBySender method the loggedUser : {} and the receiver : {} ",loggedUser,receiver);
        BlockedUser blockedUser = blockListRepository.findByUserIdAndBlockedUser(loggedUser.getId(),receiver);
        boolean exists = blockedUser != null;
        logger.info("Completed isReceiverBlockedBySender method returning : {}",exists);
        return exists;
    }


    public boolean isSenderBlockedByReceiver(User loggedUser, User receiver) {
        logger.info("Starting isSenderBlockedGyReceiver method");
        if (receiver == null) {
            logger.info("Receiver is null, returning false");
            return false;
        }
        BlockedUser blockedUser = blockListRepository.findByUserIdAndBlockedUser(receiver.getId(), loggedUser);
        boolean exists = blockedUser != null;
        logger.info("Completed isSenderBlockedGyReceiver method");
        return exists;
    }

}
