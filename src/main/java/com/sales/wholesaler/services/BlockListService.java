package com.sales.wholesaler.services;


import com.sales.entities.BlockedUser;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import com.sales.wholesaler.repository.BlockListHbRepository;
import com.sales.wholesaler.repository.BlockListRepository;
import com.sales.wholesaler.repository.WholesaleUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockListService  {

  private final WholesaleUserRepository wholesaleUserRepository;
  private final BlockListRepository blockListRepository;
  private final BlockListHbRepository blockListHbRepository;
  private static final Logger logger = LoggerFactory.getLogger(BlockListService.class);

    public BlockedUser addAUserInBlockList(User blockingBy, String blockedUserSlug) {
        logger.debug("Starting addAUserInBlockList method");
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
        logger.debug("Completed addAUserInBlockList method");
        return savedBlockList;
    }

    public boolean removeUserFromBlockList(Integer userId, String recipient){
        User receiver = wholesaleUserRepository.findUserBySlug(recipient);
        return blockListHbRepository.deleteUserFromBlockList(userId,receiver);
    }



    public boolean isReceiverBlockedBySender(User loggedUser, User receiver) {
        logger.debug("Starting isReceiverBlockedBySender method the loggedUser : {} and the receiver : {} ",loggedUser,receiver);
        BlockedUser blockedUser = blockListRepository.findByUserIdAndBlockedUser(loggedUser.getId(),receiver);
        boolean exists = blockedUser != null;
        logger.debug("Completed isReceiverBlockedBySender method returning : {}",exists);
        return exists;
    }


    public boolean isSenderBlockedByReceiver(User loggedUser, User receiver) {
        logger.debug("Starting isSenderBlockedGyReceiver method");
        if (receiver == null) {
            logger.debug("Receiver is null, returning false");
            return false;
        }
        BlockedUser blockedUser = blockListRepository.findByUserIdAndBlockedUser(receiver.getId(), loggedUser);
        boolean exists = blockedUser != null;
        logger.debug("Completed isSenderBlockedGyReceiver method");
        return exists;
    }

}
