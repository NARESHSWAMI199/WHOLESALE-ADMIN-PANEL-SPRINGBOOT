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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.sales.specifications.ItemCommentSpecifications.isUserId;
import static com.sales.specifications.ItemCommentSpecifications.isWholesaleId;
import static com.sales.utils.Utils.getCurrentMillis;

@Service
public class WholesaleStoreService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleStoreService.class);

    @Value("${store.absolute}")
    String storeImagePath;

    @Value("${store.relative}")
    String storeImageRelativePath;

    @Transactional(rollbackOn = {IllegalArgumentException.class, MyException.class, RuntimeException.class})
    public Map<String, Object> updateStoreBySlug(StoreDto storeDto, User loggedUser) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting updateStoreBySlug method with storeDto: {}, loggedUser: {}", storeDto, loggedUser);

        // Validating required fields. If their we found any required field is null, this will throw an Exception
        Utils.checkRequiredFields(storeDto, List.of("storeSlug", "storeName", "storePic", "storeEmail", "storePhone", "categoryId", "subCategoryId"));

        Map<String, Object> responseObj = new HashMap<>();
        String storeName = Utils.isValidName(storeDto.getStoreName(), "store");
        storeDto.setStoreName(storeName);

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
        Store store = getStoreByUserId(loggedUser.getId());
        String slug = store.getSlug();
        storeDto.setStoreSlug(slug);

        // before update store and store's address get address id from store
        Integer addressId = wholesaleStoreRepository.getAddressIdBySlug(storeDto.getStoreSlug());
        if (addressId == null) throw new IllegalArgumentException("No store found to update.");  // wrong wholesale slug.
        storeDto.setAddressId(addressId);

        String imageName = getStoreImagePath(storeDto.getStorePic(), slug);
        if (imageName != null) {
            storeDto.setStoreAvatar(imageName);
        } else {
            storeDto.setStoreAvatar(store.getAvtar());
        }
        int isUpdated = updateStore(storeDto, loggedUser); // Update operation
        if (isUpdated > 0) {
            responseObj.put("message", "successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No store found to update");
            responseObj.put("status", 404);
        }
        logger.info("Completed updateStoreBySlug method");
        return responseObj;
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, MyException.class, RuntimeException.class})
    public int updateStore(StoreDto storeDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting updateStore method with storeDto: {}, loggedUser: {}", storeDto, loggedUser);
        AddressDto address = new AddressDto();
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(storeDto, List.of("street", "zipCode", "city", "state"));
        address.setStreet(storeDto.getStreet());
        address.setZipCode(storeDto.getZipCode());
        address.setCity(storeDto.getCity());
        address.setState(storeDto.getState());
        address.setAddressId(storeDto.getAddressId());
        int isUpdatedAddress = wholesaleAddressHbRepository.updateAddress(address, loggedUser); // Update operation
        if (isUpdatedAddress < 1) return isUpdatedAddress;
        int isUpdatedStore = wholesaleStoreHbRepository.updateStore(storeDto, loggedUser); // Update operation
        logger.info("Completed updateStore method");
        return isUpdatedStore;
    }

    @Transactional
    public Store getStoreDetails(String slug) {
        logger.info("Starting getStoreDetails method with slug: {}", slug);
        Store store = wholesaleStoreRepository.findStoreBySlug(slug);
        logger.info("Completed getStoreDetails method");
        return store;
    }

    public Store getStoreByUserSlug(Integer userId) {
        logger.info("Starting getStoreByUserSlug method with userId: {}", userId);
        Store store = wholesaleStoreRepository.findStoreByUserId(userId);
        logger.info("Completed getStoreByUserSlug method");
        return store;
    }

    public Store getStoreByUserId(Integer userId) {
        logger.info("Starting getStoreByUserId method with userId: {}", userId);
        Store store = wholesaleStoreRepository.findStoreByUserId(userId);
        logger.info("Completed getStoreByUserId method");
        return store;
    }

    public Integer getStoreIdByUserSlug(Integer userId) {
        logger.info("Starting getStoreIdByUserSlug method with userId: {}", userId);
        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(userId);
        logger.info("Completed getStoreIdByUserSlug method");
        return storeId;
    }

    @Transactional
    public String getStoreImagePath(MultipartFile storeImage, String slug) throws MyException, IOException {
        logger.info("Starting getStoreImagePath method with storeImage: {}, slug: {}", storeImage, slug);
        if (storeImage != null) {
            if (UploadImageValidator.isValidImage(storeImage, GlobalConstant.minWidth,
                    GlobalConstant.minHeight, GlobalConstant.maxWidth, GlobalConstant.maxHeight,
                    GlobalConstant.allowedAspectRatios, GlobalConstant.allowedFormats)) {
                String fileOriginalName = Objects.requireNonNull(storeImage.getOriginalFilename()).replaceAll(" ", "_");
                String dirPath = storeImagePath + slug + "/";
                File dir = new File(dirPath);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dirPath + fileOriginalName);
                storeImage.transferTo(file);
                logger.info("Completed getStoreImagePath method");
                return fileOriginalName;
            } else {
                throw new MyException("Image is not fit in accept ratio. please resize your image before upload.");
            }
        }
        return null;
    }

    @Transactional(rollbackOn = {MyException.class, IllegalArgumentException.class, RuntimeException.class, Exception.class})
    public Store createStore(StoreDto storeDto, User loggedUser) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting createStore method with storeDto: {}, loggedUser: {}", storeDto, loggedUser);

        // Validating required fields. If their we found any required field is null, this will throw an Exception
        Utils.checkRequiredFields(storeDto, List.of("storeName", "storePic", "storeEmail", "storePhone", "categoryId", "subCategoryId"));

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
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(addressDto, List.of("street", "zipCode", "city", "state"));
        Address address = insertAddress(addressDto, loggedUser); // Create operation

        Store store = new Store(loggedUser);
        store.setUser(loggedUser);
        store.setStoreName(storeDto.getStoreName());
        store.setEmail(storeDto.getStoreEmail());
        store.setAddress(address);
        store.setDescription(storeDto.getDescription());
        store.setPhone(storeDto.getStorePhone());
        store.setRating(0f);
        store.setStoreCategory(storeDto.getStoreCategory());
        store.setStoreSubCategory(storeDto.getStoreSubCategory());
        Store insertedStore = wholesaleStoreRepository.save(store); // Create operation
        String imageName = getStoreImagePath(storeDto.getStorePic(), insertedStore.getSlug());
        if (imageName != null) {
            store.setAvtar(imageName); /** I know save function called before set this, but it will save automatically due to same transaction */
        } else {
            throw new MyException("Store image can't be blank.");
        }
        logger.info("Completed createStore method");
        return insertedStore;
    }

    @Transactional
    public Address insertAddress(AddressDto addressDto, User loggedUser) {
        logger.info("Starting insertAddress method with addressDto: {}, loggedUser: {}", addressDto, loggedUser);
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
        Address savedAddress = addressRepository.save(address); // Create operation
        logger.info("Completed insertAddress method");
        return savedAddress;
    }

    public AddressDto getAddressObjFromStore(StoreDto storeDto) {
        logger.info("Starting getAddressObjFromStore method with storeDto: {}", storeDto);
        AddressDto addressDto = AddressDto.builder()
            .street(storeDto.getStreet())
            .zipCode(storeDto.getZipCode())
            .city(storeDto.getCity())
            .state(storeDto.getState())
            .latitude(storeDto.getLatitude())
            .altitude(storeDto.getAltitude())
            .build();
        logger.info("Completed getAddressObjFromStore method");
        return addressDto;
    }

    public Page<StoreNotifications> getAllStoreNotification(SearchFilters filters, User loggedUser) {
        logger.info("Starting getAllStoreNotification method with filters: {}, loggedUser: {}", filters, loggedUser);
        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(loggedUser.getId());
        Specification<StoreNotifications> specification = Specification.where(isUserId(loggedUser.getId()).or(isWholesaleId(storeId)));
        Pageable pageable = getPageable(filters);
        Page<StoreNotifications> notifications = wholesaleNotificationRepository.findAll(specification, pageable);
        logger.info("Completed getAllStoreNotification method");
        return notifications;
    }

    public void updateSeen(StoreDto storeDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting updateSeen method with storeDto: {}", storeDto);
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(storeDto, List.of("seenIds"));
        List<Long> seenIds = storeDto.getSeenIds();
        for (long id : seenIds) {
            wholesaleStoreHbRepository.updateSeenNotifications(id); // Update operation
        }
        logger.info("Completed updateSeen method");
    }

    public List<StoreCategory> getAllStoreCategory() {
        logger.info("Starting getAllStoreCategory method");
        Sort sort = Sort.by("category").ascending();
        List<StoreCategory> categories = wholesaleCategoryRepository.findAll(sort);
        logger.info("Completed getAllStoreCategory method");
        return categories;
    }

    public List<StoreSubCategory> getAllStoreSubCategories(int categoryId) {
        logger.info("Starting getAllStoreSubCategories method with categoryId: {}", categoryId);
        List<StoreSubCategory> subCategories = wholesaleSubCategoryRepository.getSubCategories(categoryId);
        logger.info("Completed getAllStoreSubCategories method");
        return subCategories;
    }

}
