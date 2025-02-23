package com.sales.wholesaler.services;


import com.sales.dto.UserPaginationDto;
import com.sales.entities.Pagination;
import com.sales.entities.User;
import com.sales.entities.UserPagination;
import com.sales.exceptions.NotFoundException;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

@Service
public class WholesalePaginationService extends  WholesaleRepoContainer{



    public List<Pagination> findAllPaginations(){
        return wholesalePaginationRepository.findAll();
    }

    public UserPagination findUserPaginationsByUserId(User loggedUser){
        return wholesaleUserPaginationsRepository.findByUserId(loggedUser.getId());
    }


    public Pagination findByFieldFor(String fieldsFor){
        return wholesalePaginationRepository.findByFieldFor(fieldsFor);
    }


    @Transactional(rollbackOn = {InternalException.class, RuntimeException.class,Exception.class })
    public void setUserDefaultPaginationForSettings(User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<Pagination> allPagination = wholesalePaginationRepository.findAll();
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
        Utils.checkRequiredFields(userPaginationDto,List.of("paginationId","userId"));
        Optional<Pagination> pagination  = wholesalePaginationRepository.findById(userPaginationDto.getPaginationId());
        if(pagination.isEmpty()) throw new NotFoundException("No fields are found to update.");
        // check pagination field available or not
        return wholesalePaginationHbRepository.updateUserPaginations(pagination.get(),userPaginationDto);
    }

}
