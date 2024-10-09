package com.sales.wholesaler.repository;

import com.sales.entities.StoreNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface WholesaleNotificationRepository extends JpaRepository<StoreNotifications, Long>, JpaSpecificationExecutor<StoreNotifications> {
}
