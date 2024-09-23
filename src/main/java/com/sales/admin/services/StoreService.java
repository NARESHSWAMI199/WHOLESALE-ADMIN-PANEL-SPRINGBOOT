package com.sales.admin.services;

import com.sales.dto.AddressDto;
import com.sales.dto.SearchFilters;
import com.sales.dto.StatusDto;
import com.sales.dto.StoreDto;
import com.sales.entities.Address;
import com.sales.entities.Store;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.sales.specifications.StoreSpecifications.*;


@Service
public class StoreService extends RepoContainer{


    @Autowired
    AddressService addressService;

    public Page<Store> getAllStore(SearchFilters filters) {
        Specification<Store> specification = Specification.where(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(isStatus(filters.getStatus()))
        );
        Pageable pageable = getPageable(filters);
        return storeRepository.findAll(specification,pageable);
    }


    public Map<String, Integer> getWholesaleCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",storeRepository.totalWholesaleCount());
        responseObj.put("active",storeRepository.optionWholesaleCount("A"));
        responseObj.put("deactive",storeRepository.optionWholesaleCount("D"));
        return responseObj;
    }


    public AddressDto getAddressObjFromStore(StoreDto storeDto){
        AddressDto addressDto = new AddressDto();
        addressDto.setCity(storeDto.getCity());
        addressDto.setState(storeDto.getState());
        addressDto.setLatitude(storeDto.getLatitude());
        addressDto.setAltitude(storeDto.getAltitude());
        return addressDto;
    }


    @Transactional
    public Map<String, Object> createOrUpdateStore(StoreDto storeDto,User loggedUser) throws Exception {
        Map<String, Object> responseObj = new HashMap<>();
            if(storeDto.getStoreSlug().equals("only-profile")) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 200);
            }
            else if (!Utils.isEmpty(storeDto.getStoreSlug())) {
            int isUpdated = updateStore(storeDto, loggedUser);
            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 201);
            } else {
                responseObj.put("message", "nothing to updated. may be something went wrong");
                responseObj.put("status", 400);
            }
            return responseObj;
        } else {
            Store createdStore = createStore(storeDto, loggedUser);
            if (createdStore.getId() > 0) {
                responseObj.put("res", createdStore);
                responseObj.put("message", "successfully inserted.");
                responseObj.put("status", 200);
            } else {
                responseObj.put("message", "nothing to insert. may be something went wrong");
                responseObj.put("status", 400);
            }
        }
        return responseObj;
    }

    @Transactional
    public Store createStore(StoreDto storeDto , User loggedUser) throws Exception {


        /** inserting  address during create a wholesale */
        AddressDto addressDto = getAddressObjFromStore(storeDto);
        Address address =  addressService.insertAddress(addressDto,loggedUser);
        /** @END inserting  address during create a wholesale */

        Store store = new Store();
        if(Utils.isEmpty(storeDto.getUserSlug())){
            throw new Exception("Must provide a store user");
        }
        Optional<User> storeOwner = userRepository.findByWholesalerSLug(storeDto.getUserSlug());
        if (storeOwner == null)  throw new Exception("Make sure user is wholesaler.");

        store = new Store(loggedUser);
        store.setUser(storeOwner.get());
        store.setStoreName(storeDto.getStoreName());
        store.setEmail(storeDto.getStoreEmail());
        store.setAddress(address);
        store.setDescription(storeDto.getDescription());
        store.setPhone(storeDto.getStorePhone());
        store.setRating(storeDto.getRating());
        return storeRepository.save(store);
    }

    @Transactional
    public int updateStore(StoreDto storeDto, User loggedUser){
        AddressDto address = new AddressDto();
        address.setAddressSlug(storeDto.getAddressSlug());
        address.setCity(storeDto.getCity());
        address.setState(storeDto.getState());
        int isUpdatedAddress = addressHbRepository.updateAddress(address,loggedUser);
        if(isUpdatedAddress < 1) return isUpdatedAddress;
        return storeHbRepository.updateStore(storeDto,loggedUser);
    }

    public int deleteStoreBySlug(String slug){
        return storeHbRepository.deleteStore(slug);
    }

    public int deleteStoreByUserId(int userId){
        return storeHbRepository.deleteStore(userId);
    }


    @Transactional
    public Store getStoreDetails(String slug){
        return storeRepository.findStoreBySlug(slug);
    }


    public Store getStoreByUserSlug(String userSlug) throws Exception {
        User user = userRepository.findUserBySlug(userSlug);
        if (user == null) throw new Exception("sorry user not found.");
        return storeRepository.findStoreByUserId(user.getId());
    }


    public int updateStatusBySlug(StatusDto statusDto){
        return storeHbRepository.updateStatus(statusDto.getSlug(),statusDto.getStatus());
    }

}
