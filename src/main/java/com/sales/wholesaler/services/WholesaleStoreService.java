package com.sales.wholesaler.services;

import com.sales.dto.AddressDto;
import com.sales.dto.SearchFilters;
import com.sales.dto.StoreDto;
import com.sales.entities.*;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.utils.UploadImageValidator;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.sales.specifications.ItemCommentSpecifications.isUserId;
import static com.sales.specifications.ItemCommentSpecifications.isWholesaleId;
import static com.sales.utils.Utils.getCurrentMillis;

@Service
public class WholesaleStoreService extends WholesaleRepoContainer {



    @Value("${store.absolute}")
    String storeImagePath;

    @Value("${store.relative}")
    String storeImageRelativePath;

    @Transactional(rollbackOn = {MyException.class , RuntimeException.class})
    public Map<String, Object> updateStoreBySlug(StoreDto storeDto,User loggedUser) throws IOException {
        Map<String, Object> responseObj = new HashMap<>();
        String storeName = Utils.isValidName( storeDto.getStoreName(),"store");
        storeDto.setStoreName(storeName);

        /* '_' replaced by actual error message in mobileAndEmailValidation */
        Utils.mobileAndEmailValidation(storeDto.getStoreEmail(), storeDto.getStorePhone(),"Not a valid _");

        try {
            StoreCategory storeCategory = wholesaleCategoryRepository.findById(storeDto.getCategoryId()).get();
            storeDto.setStoreCategory(storeCategory);
            StoreSubCategory storeSubCategory = wholesaleSubCategoryRepository.findById(storeDto.getSubCategoryId()).get();
            storeDto.setStoreSubCategory(storeSubCategory);
        }catch (Exception e){
            throw new MyException("Invalid arguments for category and subcategory");
        }
        Store store = getStoreByUserId(loggedUser.getId());
        String slug = store.getSlug();
        storeDto.setStoreSlug(slug);

        // before update store and store's address get address id from store
        Integer addressId = wholesaleStoreRepository.getAddressIdBySlug(storeDto.getStoreSlug());
        if(addressId == null) throw new IllegalArgumentException("No store found to update.");  // wrong wholesale slug.
        storeDto.setAddressId(addressId);


        String imageName = getStoreImagePath(storeDto.getStorePic(), slug);
        if(imageName !=null){
            storeDto.setStoreAvatar(imageName);
        }else{
            storeDto.setStoreAvatar(store.getAvtar());
        }
        int isUpdated = updateStore(storeDto, loggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "successfully updated.");
            responseObj.put("status", 201);
        } else {
            responseObj.put("message", "nothing to updated. may be something went wrong");
            responseObj.put("status", 400);
        }
        return responseObj;
    }

    @Transactional(rollbackOn = {MyException.class,RuntimeException.class})
    public int updateStore(StoreDto storeDto, User loggedUser){
        AddressDto address = new AddressDto();
        address.setStreet(storeDto.getStreet());
        address.setZipCode(storeDto.getZipCode());
        address.setCity(storeDto.getCity());
        address.setState(storeDto.getState());
        address.setAddressId(storeDto.getAddressId());
        int isUpdatedAddress = wholesaleAddressHbRepository.updateAddress(address,loggedUser);
        if(isUpdatedAddress < 1) return isUpdatedAddress;
        return wholesaleStoreHbRepository.updateStore(storeDto,loggedUser);
    }


    @Transactional
    public Store getStoreDetails(String slug){
        return wholesaleStoreRepository.findStoreBySlug(slug);
    }


    public Store getStoreByUserSlug(Integer userId) {
        return wholesaleStoreRepository.findStoreByUserId(userId);
    }

    public Store getStoreByUserId(Integer userId){
        return wholesaleStoreRepository.findStoreByUserId(userId);
    }

    public Integer getStoreIdByUserSlug(Integer userId) {
        return wholesaleStoreRepository.getStoreIdByUserId(userId);
    }


    @Transactional
    public String getStoreImagePath(MultipartFile storeImage, String slug) throws MyException, IOException {
        if(storeImage !=null ) {
            if (UploadImageValidator.isValidImage(storeImage, GlobalConstant.minWidth,
                    GlobalConstant.minHeight, GlobalConstant.maxWidth, GlobalConstant.maxHeight,
                    GlobalConstant.allowedAspectRatios, GlobalConstant.allowedFormats)) {
                String fileOriginalName = Objects.requireNonNull(storeImage.getOriginalFilename()).replaceAll(" ", "_");
                String dirPath = storeImagePath+"/"+slug+"/";
                File dir = new File(dirPath);
                if(!dir.exists()) dir.mkdirs();
                File file = new File(dirPath+fileOriginalName);
                storeImage.transferTo(file);
                return fileOriginalName;
            } else {
                throw new MyException("Image is not fit in accept ratio. please resize you image before upload.");
            }
        }
        return null;
    }





    @Transactional(rollbackOn = {MyException.class, RuntimeException.class})
    public Store createStore(StoreDto storeDto , User loggedUser) {
        try {
            /* '_' replaced by actual error message in mobileAndEmailValidation */
            Utils.mobileAndEmailValidation(storeDto.getStoreEmail(), storeDto.getStorePhone(), "Not a valid _");
            try {
                StoreCategory storeCategory = wholesaleCategoryRepository.findById(storeDto.getCategoryId()).get();
                storeDto.setStoreCategory(storeCategory);
                StoreSubCategory storeSubCategory = wholesaleSubCategoryRepository.findById(storeDto.getSubCategoryId()).get();
                storeDto.setStoreSubCategory(storeSubCategory);
            } catch (Exception e) {
                throw new MyException("Invalid arguments for category and subcategory");
            }

            /* inserting  address during create a wholesale */
            AddressDto addressDto = getAddressObjFromStore(storeDto);
            Address address = insertAddress(addressDto, loggedUser);

            Store store = new Store(loggedUser);
            store.setUser(loggedUser);
            store.setStoreName(storeDto.getStoreName());
            store.setEmail(storeDto.getStoreEmail());
            store.setAddress(address);
            store.setDescription(storeDto.getDescription());
            store.setPhone(storeDto.getStorePhone());
            store.setRating(storeDto.getRating());
            store.setStoreCategory(storeDto.getStoreCategory());
            store.setStoreSubCategory(storeDto.getStoreSubCategory());
            Store insertedStore = wholesaleStoreRepository.save(store);
            String imageName = getStoreImagePath(storeDto.getStorePic(), insertedStore.getSlug());
            if (imageName != null) {
                store.setAvtar(imageName); /** I know save function called before set this, but it will save automatically due to same transaction */
            } else {
                throw new MyException("Store image can't be blank.");
            }
            return insertedStore;
        }catch (IOException e){
            throw new MyException("Store image is not valid image.");
        }
    }


    @Transactional
    public Address insertAddress(AddressDto addressDto, User loggedUser){
        Address address = Address.builder()
            .slug(UUID.randomUUID().toString())
            .street(addressDto.getStreet())
            .zipCode(addressDto.getZipCode())
            .city(addressDto.getCity())
            .state(addressDto.getState())
            .latitude(addressDto.getLatitude())
            .altitude(addressDto.getAltitude())
            .createdAt(getCurrentMillis())
            .createdBy(loggedUser.getId())
            .updatedAt(getCurrentMillis())
            .updatedBy(loggedUser.getId())
            .build();
        return  addressRepository.save(address);
    }


    public AddressDto getAddressObjFromStore(StoreDto storeDto){
        return  AddressDto.builder()
            .street(storeDto.getStreet())
            .zipCode(storeDto.getZipCode())
            .city(storeDto.getCity())
            .state(storeDto.getState())
            .latitude(storeDto.getLatitude())
            .altitude(storeDto.getAltitude())
            .build();
    }



    public Page<StoreNotifications> getAllStoreNotification(SearchFilters filters,User loggedUser) {
        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(loggedUser.getId());
        Specification<StoreNotifications> specification = Specification.where(isUserId(loggedUser.getId()).or(isWholesaleId(storeId)));
        Pageable pageable = getPageable(filters);
        return  wholesaleNotificationRepository.findAll(specification,pageable);
    }

    public void updateSeen(List<Long> seenIds) {
        for(long id : seenIds){
            wholesaleStoreHbRepository.updateSeenNotifications(id);
        }
    }


    public List<StoreCategory> getAllStoreCategory() {
        Sort sort = Sort.by("category").ascending();
        return wholesaleCategoryRepository.findAll(sort);
    }


    public List<StoreSubCategory> getAllStoreSubCategories(int categoryId) {
        return wholesaleSubCategoryRepository.getSubCategories(categoryId);
    }



}
