package com.sales.admin.services;

import com.sales.dto.*;
import com.sales.entities.Address;
import com.sales.entities.Store;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static com.sales.specifications.StoreSpecifications.*;


@Service
public class StoreService extends RepoContainer{


    @Autowired
    AddressService addressService;


    @Value("${store.absolute}")
    String storeImagePath;

    @Value("${store.relative}")
    String storeImageRelativePath;


    public Page<Store> getAllStore(SearchFilters filters) {
        Specification<Store> specification = Specification.where(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(isStatus(filters.getStatus()))
                        .and(hasSlug(filters.getSlug()))
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

    public Map<String,Object> getStoreCountByMonths(GraphDto graphDto){
        List<Integer> months = graphDto.getMonths();
        months = (months == null || months.size() < 1) ?
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12) : months;
        Integer year = graphDto.getYear();
        Map<String,Object> monthsObj= new LinkedHashMap<>();
        for(Integer month : months) {
            monthsObj.put(getMonthName(month),storeRepository.totalStoreViaMonth(month,year));
        }
        return monthsObj;
    }

    public String getMonthName(int month) {
        if (month <= 0 || month > 12) {
            return null;
        }
        return Month.of(month).getDisplayName(TextStyle.FULL, new Locale("eng"));
    }

    @Transactional
    public Map<String, Object> createOrUpdateStore(StoreDto storeDto,User loggedUser) throws Exception {
        try {
            Map<String, Object> responseObj = new HashMap<>();
            /** if user want update only his profile */
            if (storeDto.getStoreSlug() != null && storeDto.getStoreSlug().equals("only-profile")) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 200);
            } else if (!Utils.isEmpty(storeDto.getStoreSlug())) {
                updateStoreImage(storeDto.getStorePic(),storeDto.getStoreSlug());
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
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw  e;
        }
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

    @Transactional
    public int deleteStoreBySlug(String slug){
        try {
            Store store = storeRepository.findStoreBySlug(slug);
            Optional<User> user = userRepository.findById(store.getUser().getId());
            userHbRepository.deleteUserBySlug(user.get().getSlug());
            return storeHbRepository.deleteStore(slug);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
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

    @Transactional
    public int updateStatusBySlug(StatusDto statusDto){
        try {
            Store store = storeRepository.findStoreBySlug(statusDto.getSlug());
            String status = statusDto.getStatus();
            store.getUser().setStatus(statusDto.getStatus());
            store.setStatus(status);
            return storeRepository.save(store).getId();
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }



    @Transactional
    public int updateStoreImage(MultipartFile profileImage, String slug) throws Exception {
        if(profileImage !=null ) {
            String fileOriginalName = profileImage.getOriginalFilename().replaceAll(" ", "_");
            if (!Utils.isValidImage(fileOriginalName)) throw new Exception("Not a valid file.");
            profileImage.transferTo(new File(storeImagePath + slug + fileOriginalName));
            return storeHbRepository.updateStoreAvatar(slug, storeImageRelativePath + slug + fileOriginalName);
        }
        return 0;
    }





}