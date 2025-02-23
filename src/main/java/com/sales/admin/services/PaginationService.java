package com.sales.admin.services;


import com.sales.dto.UserPaginationDto;
import com.sales.entities.Pagination;
import com.sales.entities.User;
import com.sales.entities.UserPagination;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
public class PaginationService extends  RepoContainer{



    public List<Pagination> findAllPaginations(){
        return paginationRepository.findAll();
    }

    public UserPagination findUserPaginationsByUserId(User loggedUser){
        return userPaginationsRepository.findByUserId(loggedUser.getId());
    }


    public Pagination findByFieldFor(String fieldsFor){
        return paginationRepository.findByFieldFor(fieldsFor);
    }

    @Transactional(rollbackOn = {InternalException.class, RuntimeException.class,Exception.class })
    public void setUserDefaultPaginationForSettings(User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<Pagination> allPagination = paginationRepository.findAll();
        for (Pagination pagination : allPagination) {
            UserPaginationDto userPaginationDto = UserPaginationDto.builder()
                    .paginationId(pagination.getId())
                    .rowsNumber(25)
                    .build();
            UserPagination userPagination = insertUserPagination(userPaginationDto, loggedUser);
            if(userPagination == null) throw new InternalException("We are unable to save your default pagination settings.");

        }
    }

    @Transactional(rollbackOn = {InternalException.class, RuntimeException.class,Exception.class })
    public UserPagination insertUserPagination(UserPaginationDto userPaginationDto,User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        // validate required fields
        Utils.checkRequiredFields(userPaginationDto,List.of("paginationId","rowsNumber"));
        // Going to save user_pagination
        UserPagination userPagination = new UserPagination();
        userPagination.setPaginationId(userPagination.getPaginationId());
        userPagination.setUserId(loggedUser.getId());
        userPagination.setRowsNumber(userPagination.getRowsNumber());
        return userPaginationsRepository.save(userPagination);
    }


    public int updateUserPaginationRowsNumber(UserPaginationDto userPaginationDto) {
        return paginationHbRepository.updateUserPaginations(userPaginationDto);
    }

}
