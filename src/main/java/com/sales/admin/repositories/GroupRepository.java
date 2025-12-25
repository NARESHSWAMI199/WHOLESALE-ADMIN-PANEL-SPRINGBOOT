package com.sales.admin.repositories;


import com.sales.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group,Long> , JpaSpecificationExecutor {

    Group findGroupBySlug(String slug);

}
