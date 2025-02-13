package com.sales.wholesaler.repository;


import com.sales.dto.AddressDto;
import com.sales.entities.City;
import com.sales.entities.State;
import com.sales.entities.User;
import com.sales.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Transactional
public class WholesaleAddressHbRepository {

    @Autowired
    EntityManager entityManager;

    public int updateAddress(AddressDto addressDto, User loggedUser){
        String hqQuery ="update Address set " +
                "city =:city,"+
                "state =:state," +
                "latitude =:latitude," +
                "altitude =:altitude, " +
                "updatedAt =:updatedAt, " +
                "updatedBy =:updatedBy " +
                "where id =:id ";
        Query query = entityManager.createQuery(hqQuery);
        query.setParameter("city",addressDto.getCity());
        query.setParameter("state",addressDto.getState());
        query.setParameter("latitude",addressDto.getLatitude());
        query.setParameter("altitude",addressDto.getAltitude());
        query.setParameter("updatedBy", loggedUser.getId());
        query.setParameter("updatedAt", Utils.getCurrentMillis());
        query.setParameter("id",addressDto.getAddressId());
        return  query.executeUpdate();
    }

    public List<City> getCityList(int stateId){
        String hql = "from City where stateId = :stateId";
        Query query = entityManager.createQuery(hql);
        query.setParameter("stateId", stateId);
        return query.getResultList();
    }

    public List<State> getStateList(){
        String hql = "from State";
        Query query = entityManager.createQuery(hql);
        return query.getResultList();
    }


}
