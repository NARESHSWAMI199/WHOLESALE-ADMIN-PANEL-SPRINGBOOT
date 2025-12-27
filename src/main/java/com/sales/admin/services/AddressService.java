package com.sales.admin.services;

import com.sales.dto.AddressDto;
import com.sales.entities.Address;
import com.sales.entities.City;
import com.sales.entities.State;
import com.sales.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.sales.utils.Utils.getCurrentMillis;

@Service
@RequiredArgsConstructor
public class AddressService extends RepoContainer {

    private final com.sales.helpers.Logger log;
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Transactional
    public Address insertAddress(AddressDto addressDto, User loggedUser) {
        log.info(logger,"Entering insertAddress with addressDto: {}, loggedUser: {}", addressDto, loggedUser);
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
        log.info(logger,"Exiting insertAddress with result: {}", result);
        return result;
    }

    public List<City> getCityList(int stateId) {
        log.info(logger,"Entering getCityList with stateId: {}", stateId);
        List<City> result = addressHbRepository.getCityList(stateId);
        log.info(logger,"Exiting getCityList with result: {}", result);
        return result;
    }

    public List<State> getStateList() {
        log.info(logger,"Entering getStateList");
        List<State> result = addressHbRepository.getStateList();
        log.info(logger,"Exiting getStateList with result: {}", result);
        return result;
    }

}
