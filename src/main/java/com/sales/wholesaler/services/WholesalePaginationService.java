package com.sales.wholesaler.services;


import com.sales.dto.UserPaginationDto;
import com.sales.entities.Pagination;
import com.sales.entities.User;
import com.sales.entities.UserPagination;
import com.sales.exceptions.NotFoundException;
import com.sales.specifications.PaginationSpecification;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WholesalePaginationService extends  WholesaleRepoContainer{



    public List<UserPagination> findAllUserPaginations(){
        return wholesaleUserPaginationsRepository.findAll();
    }

    public Map<String,Object> findUserPaginationsByUserId(User loggedUser){
        List<UserPagination> userPaginations = wholesaleUserPaginationsRepository.getUserPaginationByUserId(loggedUser.getId());
        Map<String,Object> result = new LinkedHashMap<>();
        for(UserPagination userPagination : userPaginations) {
            String key = userPagination.getPagination().getFieldFor();
            // remove all whitespaces and changed with uppercase like:
            // abc d → ABCD
            key = key.replaceAll("\\s+", "").toUpperCase();
            result.put(key,userPagination);
        }
        return result;
    }


    public Pagination findByFieldFor(String fieldsFor){
        return wholesalePaginationRepository.findByFieldFor(fieldsFor);
    }


    @Transactional(rollbackOn = {InternalException.class, RuntimeException.class,Exception.class })
    public void setUserDefaultPaginationForSettings(User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Specification<Pagination> specification = Specification.where(PaginationSpecification.whoCanSee("B")
                .or(PaginationSpecification.whoCanSee("W"))
        );
        List<Pagination> allPagination = wholesalePaginationRepository.findAll(specification);
        for (Pagination pagination : allPagination) {
            UserPagination userPagination = insertUserPagination(pagination, loggedUser , 25); // default rows are 25
            if(userPagination == null) throw new InternalException("We are unable to save your default pagination settings.");

        }
    }

    @Transactional(rollbackOn = {InternalException.class, RuntimeException.class,Exception.class })
    public UserPagination insertUserPagination(Pagination pagination,User loggedUser,Integer rowNumbers) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        UserPagination userPagination = new UserPagination();
        userPagination.setPagination(pagination);
        userPagination.setUserId(loggedUser.getId());
        userPagination.setRowsNumber(rowNumbers);
        return wholesaleUserPaginationsRepository.save(userPagination);
    }



    public int updateUserPaginationRowsNumber(UserPaginationDto userPaginationDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Check required fields are not null
        Utils.checkRequiredFields(userPaginationDto,List.of("paginationId","userId","rowsNumber"));
        Optional<Pagination> pagination  = wholesalePaginationRepository.findById(userPaginationDto.getPaginationId());
        if(pagination.isEmpty()) throw new NotFoundException("No fields are found to update.");
        // check pagination field available or not
        return wholesalePaginationHbRepository.updateUserPaginations(pagination.get(),userPaginationDto);
    }

}
