package com.sales.admin.repositories;


import com.sales.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GroupRepository extends JpaRepository<Group,Long> , JpaSpecificationExecutor<Group> {

    Group findGroupBySlug(String slug);


    @Query(value = """
            select 
                g.name as name,
                p.id as id,
                p.permission as permission 
            from Group g
            left join g.permissions p
            where g.id =:groupId
            """)
    List<Map<String,Object>> findGroupAndPermissionsByGroupId(@Param("groupId") Integer groupId);


}
