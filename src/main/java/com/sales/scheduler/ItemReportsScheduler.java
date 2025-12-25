package com.sales.scheduler;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ItemReportsScheduler {


    @Autowired
    EntityManager entityManager;


    @Scheduled(cron = "0 0 0,12 * * *") // Runs at midnight and noon
    public void updatingItemReportsCounts() {
        String hql = """
                UPDATE Item i
                SET i.totalReportsCount = COALESCE(
                    (SELECT COUNT(ir.id) FROM ItemReport ir WHERE ir.itemId = i.id),0
                )
                """;
        Query query = entityManager.createQuery(hql);
        query.executeUpdate();
    }


}
