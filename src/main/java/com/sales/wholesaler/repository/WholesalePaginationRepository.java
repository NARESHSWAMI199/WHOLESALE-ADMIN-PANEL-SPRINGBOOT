package com.sales.wholesaler.repository;


import com.sales.entities.Pagination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesalePaginationRepository extends JpaRepository<Pagination,Integer> {
    Pagination findByFieldFor(String fieldFor);
}
