package com.sales.wholesaler.repository;


import com.sales.entities.ItemComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesaleItemCommentRepository extends JpaRepository<ItemComments,Long>, JpaSpecificationExecutor<ItemComments> {

    @Query("select count(id) from ItemComments where parentId=:parentId")
    Integer totalReplies(@Param("parentId") Integer parentId);
}