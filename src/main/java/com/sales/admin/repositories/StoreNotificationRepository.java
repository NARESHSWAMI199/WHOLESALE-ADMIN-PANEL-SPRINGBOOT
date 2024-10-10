package com.sales.admin.repositories;

import com.sales.entities.StoreNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreNotificationRepository extends JpaRepository<StoreNotifications, Long>, JpaSpecificationExecutor<StoreNotifications> {
}
