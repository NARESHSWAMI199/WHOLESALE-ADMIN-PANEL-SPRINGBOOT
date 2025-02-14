package com.sales.admin.services;

import com.sales.dto.AddressDto;
import com.sales.entities.Address;
import com.sales.entities.City;
import com.sales.entities.State;
import com.sales.entities.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.sales.utils.Utils.getCurrentMillis;

@Service
public class AddressService extends RepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Transactional
    public Address insertAddress(AddressDto addressDto, User loggedUser) {
        logger.info("Entering insertAddress with addressDto: {}, loggedUser: {}", addressDto, loggedUser);
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
        Address result = addressRepository.save(address);
        logger.info("Exiting insertAddress with result: {}", result);
        return result;
    }

    public List<City> getCityList(int stateId) {
        logger.info("Entering getCityList with stateId: {}", stateId);
        List<City> result = addressHbRepository.getCityList(stateId);
        logger.info("Exiting getCityList with result: {}", result);
        return result;
    }

    public List<State> getStateList() {
        logger.info("Entering getStateList");
        List<State> result = addressHbRepository.getStateList();
        logger.info("Exiting getStateList with result: {}", result);
        return result;
    }

}
