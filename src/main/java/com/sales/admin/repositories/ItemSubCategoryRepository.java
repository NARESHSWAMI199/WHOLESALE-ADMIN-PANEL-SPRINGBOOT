package com.sales.admin.repositories;


import com.sales.entities.Item;
import com.sales.entities.ItemSubCategory;
import com.sales.entities.StoreSubCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemSubCategoryRepository extends JpaRepository<ItemSubCategory,Integer>, JpaSpecificationExecutor<ItemSubCategory> {

    @Query(value = "from ItemSubCategory isc where isc.categoryId =:categoryId")
    List<ItemSubCategory> getSubCategories(@Param("categoryId") int categoryId, Sort sort);

    @Query(value = "select id from ItemSubCategory ssc where ssc.slug =:slug")
    Integer getItemSubCategoryIdBySlug(@Param("slug") String slug);


}
