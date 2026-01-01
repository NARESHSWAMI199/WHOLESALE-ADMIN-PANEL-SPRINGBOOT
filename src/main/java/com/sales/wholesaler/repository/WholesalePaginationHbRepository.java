package com.sales.wholesaler.repository;


import com.sales.dto.UserPaginationDto;
import com.sales.entities.Pagination;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class WholesalePaginationHbRepository {

    private final EntityManager entityManager;

    public int updateUserPaginations(Pagination pagination, UserPaginationDto userPaginationDto){
        String hql = """
                update UserPagination 
                set rowsNumber =:rowsNumber
                where userId = :userId and pagination = :pagination
                """;
        Query query = entityManager.createQuery(hql);
        query.setParameter("rowsNumber", userPaginationDto.getRowsNumber());
        query.setParameter("userId", userPaginationDto.getUserId());
        query.setParameter("pagination", pagination);
        return query.executeUpdate();
    }

}
