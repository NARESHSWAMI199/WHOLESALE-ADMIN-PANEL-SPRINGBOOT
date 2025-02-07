package com.sales.admin.services;

import com.sales.dto.AddressDto;
import com.sales.entities.Address;
import com.sales.entities.City;
import com.sales.entities.State;
import com.sales.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.sales.utils.Utils.getCurrentMillis;

@Service
public class AddressService extends RepoContainer {

    @Transactional
    public Address insertAddress(AddressDto addressDto, User loggedUser){
        Address address = new Address();
        address.setSlug(UUID.randomUUID().toString());

        address.setStreet(addressDto.getStreet());
        address.setZipCode(addressDto.getZipCode());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setLatitude(addressDto.getLatitude());
        address.setAltitude(addressDto.getAltitude());

        address.setCreatedAt(getCurrentMillis());
        address.setCreatedBy(loggedUser.getId());
        address.setUpdatedAt(getCurrentMillis());
        address.setUpdatedBy(loggedUser.getId());
        return  addressRepository.save(address);
    }

    public List<City> getCityList(int stateId){
        return addressHbRepository.getCityList(stateId);
    }

    public List<State> getStateList(){
        return addressHbRepository.getStateList();
    }

}
