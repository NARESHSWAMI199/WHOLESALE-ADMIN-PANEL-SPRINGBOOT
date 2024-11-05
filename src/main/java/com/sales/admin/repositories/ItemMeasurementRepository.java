package com.sales.admin.repositories;


import com.sales.entities.ItemMeasurementUnit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMeasurementRepository extends JpaRepository<ItemMeasurementUnit,Integer> {

    @Query(name = "from ItemMeasurementUnit where subcategoryId = :subcategoryId")
    List<ItemMeasurementUnit> getALlMeasuringUnitBySubcategoryId(@Param("subcategoryId") Integer subcategoryId , Sort sort);
}
