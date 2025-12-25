package com.sales.wholesaler.repository;


import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class WholesaleUserPlanHbRepository {

    @Autowired
    EntityManager entityManager;

}
