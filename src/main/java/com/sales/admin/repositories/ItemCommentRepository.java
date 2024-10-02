package com.sales.admin.repositories;


import com.sales.entities.ItemComments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCommentRepository  extends JpaRepository<ItemComments,Long>, JpaSpecificationExecutor<ItemComments> {


}