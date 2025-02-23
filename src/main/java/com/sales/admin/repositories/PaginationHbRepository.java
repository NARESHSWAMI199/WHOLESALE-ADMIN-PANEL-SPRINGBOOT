package com.sales.admin.repositories;


import com.sales.dto.UserPaginationDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class PaginationHbRepository {


    @Autowired
    EntityManager entityManager;;

    public int updateUserPaginations(UserPaginationDto userPaginationDto){
        String hql = """
                update UserPagination 
                set rowsNumber =:rowsNumber
                where userId = :userId and paginationId = :paginationId
                """;
        Query query = entityManager.createQuery(hql);
        return query.executeUpdate();
    }


}
