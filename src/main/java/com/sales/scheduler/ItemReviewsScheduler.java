package com.sales.scheduler;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Transactional
public class ItemReviewsScheduler {


    // NOTE: If in future we need indexing on itemId, we can go with it from db side.


    @Autowired
    EntityManager entityManager;


    @Scheduled(cron = "0 0/15 * * * *") // Auto run in every 15 minutes
    public void updatedLikesAndDisLikes() {

    }


    @Scheduled(cron = "0 0/15 * * * *") // Auto run in every 15 minutes
    public void updateTotalCountsOfComments() {

    }



    @Scheduled(cron = "0 0/15 * * * *") // Auto run in every 15 minutes
    public void updateCommentCounts(){
        String hql = """
                UPDATE Item i
                    SET i.totalReviews = COALESCE(
                        (SELECT COUNT(ir.id) FROM ItemReviews ir WHERE ir.itemId = i.id),0
                    )
                """;
        Query query = entityManager.createQuery(hql);
        query.executeUpdate();

    }



}
