package com.sales.admin.repositories;


import com.sales.entities.Pagination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaginationRepository extends JpaRepository<Pagination,Integer> {
    Pagination findByFieldFor(String fieldFor);
}
