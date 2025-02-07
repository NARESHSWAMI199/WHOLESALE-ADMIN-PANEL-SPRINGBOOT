package com.sales.admin.services;

import com.sales.dto.*;
import com.sales.entities.*;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.utils.UploadImageValidator;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        addressDto.setStreet(storeDto.getStreet());
        addressDto.setZipCode(storeDto.getZipCode());
        addressDto.setCity(storeDto.getCity());
        addressDto.setState(storeDto.getState());
        addressDto.setLatitude(storeDto.getLatitude());
        addressDto.setAltitude(storeDto.getAltitude());
        return addressDto;
    }

    public Map<String,Object> getStoreCountByMonths(GraphDto graphDto){
        List<Integer> months = graphDto.getMonths();
        months = (months == null || months.isEmpty()) ?
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

    public void validateRequiredFieldsForStore(StoreDto storeDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> requiredFields = new ArrayList<>(List.of(
            "storeName",
            "storeEmail",
            "storePhone",
            "rating",
            "categoryId",
            "subCategoryId",
            "description"
        ));
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(storeDto,requiredFields);
    }

    public void validateRequiredFieldsForCreateStore(StoreDto storeDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> requiredFields = new ArrayList<>(List.of("userSlug"));
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(storeDto,requiredFields);
    }


    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class})
    public Map<String, Object> createOrUpdateStore(StoreDto storeDto,User loggedUser,String path) throws MyException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            Map<String, Object> responseObj = new HashMap<>();
            // if there is any required field null then this will throw IllegalArgumentException
            validateRequiredFieldsForStore(storeDto);
            try {
                StoreCategory storeCategory = storeCategoryRepository.findById(storeDto.getCategoryId()).get();
                storeDto.setStoreCategory(storeCategory);
                StoreSubCategory storeSubCategory = storeSubCategoryRepository.findById(storeDto.getSubCategoryId()).get();
                storeDto.setStoreSubCategory(storeSubCategory);
            } catch (Exception e){
                throw new IllegalArgumentException("Invalid arguments for category and subcategory");
            }

            if (!Utils.isEmpty(storeDto.getStoreSlug()) && path.contains("update")) { // We are going to update store.
                String storeName = Utils.isValidName(storeDto.getStoreName(),"Store");
                storeDto.setStoreName(storeName);
                // If we found any issue with email and mobile this will throw exception
                Utils.mobileAndEmailValidation(storeDto.getStoreEmail(), storeDto.getStorePhone(), "Not a valid store's _ recheck your and store's _.");
                updateStoreImage(storeDto.getStorePic(), storeDto.getStoreSlug());

                // before update store and store's address get address id from store
                Integer addressId = storeRepository.getAddressIdBySlug(storeDto.getStoreSlug());
                if(addressId == null) throw new IllegalArgumentException("No store found to update.");  // wrong wholesale slug.
                storeDto.setAddressId(addressId);

                int isUpdated = updateStore(storeDto, loggedUser);
                if (isUpdated > 0) {
                    responseObj.put("message", "Successfully updated.");
                    responseObj.put("status", 201);
                } else {
                    responseObj.put("message", "Nothing found to updated.");
                    responseObj.put("status", 404);
                }
            } else {  // We are going to create store.

                // if there is any required field null then this will throw IllegalArgumentException
                validateRequiredFieldsForCreateStore(storeDto);

                // if there is any issue this will throw IllegalArgumentException
                Utils.mobileAndEmailValidation(
                    storeDto.getStoreEmail(),
                    storeDto.getStorePhone(),
                    "Not a valid store's _ recheck your and store's _."
                );

                String storeName = Utils.isValidName(storeDto.getStoreName(),"Store");
                storeDto.setStoreName(storeName);
                Store createdStore = createStore(storeDto, loggedUser);
                responseObj.put("res", createdStore);
                responseObj.put("message", "Successfully inserted.");
                responseObj.put("status", 200);
            }
        return responseObj;

    }



    public void validateRequiredFieldsForCreateAddress(AddressDto addressDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<String> requiredFields = new ArrayList<>(List.of("street","zipCode", "city","state"));
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(addressDto,requiredFields);
    }


    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class})
    public Store createStore(StoreDto storeDto , User loggedUser) throws MyException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        /** inserting address during create a wholesale */
        AddressDto addressDto = getAddressObjFromStore(storeDto);
        // if there is any required field null then this will throw IllegalArgumentException
        validateRequiredFieldsForCreateAddress(addressDto);
        Address address =  addressService.insertAddress(addressDto,loggedUser);

        /** @END inserting  address during create a wholesale */
        Optional<User> storeOwner = userRepository.findByWholesalerSlug(storeDto.getUserSlug());
        if (storeOwner.isEmpty())  throw new PermissionDeniedDataAccessException("User must be wholesaler.",null);


        // Saving the store data
        Store store = new Store(loggedUser);
        store.setUser(storeOwner.get());
        store.setStoreName(storeDto.getStoreName());
        store.setEmail(storeDto.getStoreEmail());
        store.setAddress(address);
        store.setDescription(storeDto.getDescription());
        store.setPhone(storeDto.getStorePhone());
        store.setRating(storeDto.getRating());
        store.setStoreCategory(storeDto.getStoreCategory());
        store.setStoreSubCategory(storeDto.getStoreSubCategory());

        return storeRepository.save(store);
    }

    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class})
    public int updateStore(StoreDto storeDto, User loggedUser){
        AddressDto address = new AddressDto();
        address.setStreet(storeDto.getStreet());
        address.setZipCode(storeDto.getZipCode());
        address.setState(storeDto.getState());
        address.setCity(storeDto.getCity());
        address.setState(storeDto.getState());
        address.setAddressId(storeDto.getAddressId());
        int isUpdatedAddress = addressHbRepository.updateAddress(address,loggedUser);
        if(isUpdatedAddress < 1) return isUpdatedAddress;
        return storeHbRepository.updateStore(storeDto,loggedUser);
    }

    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class})
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
        if (user == null) throw new MyException("sorry user not found.");
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
    public int updateStoreImage(MultipartFile storeImage, String slug) throws MyException, IOException {
        if(storeImage !=null ) {
            if (UploadImageValidator.isValidImage(storeImage, GlobalConstant.minWidth,
                GlobalConstant.minHeight, GlobalConstant.maxWidth, GlobalConstant.maxHeight,
                GlobalConstant.allowedAspectRatios, GlobalConstant.allowedFormats)) {
                String fileOriginalName = storeImage.getOriginalFilename().replaceAll(" ", "_");
                String dirPath = storeImagePath+"/"+slug+"/";
                File dir = new File(dirPath);
                if(!dir.exists()) dir.mkdirs();
                File file = new File(dirPath+fileOriginalName);
                storeImage.transferTo(file);
                return storeHbRepository.updateStoreAvatar(slug,fileOriginalName);
            } else {
                throw new IllegalArgumentException("Image is not fit in accept ratio. please resize you image before upload.");
            }
        }
        return 0;
    }


    public List<StoreCategory> getAllStoreCategory(SearchFilters searchFilters) {
        Sort sort = searchFilters.getOrder().equals("asc") ?
                Sort.by(searchFilters.getOrderBy()).ascending() :
                Sort.by(searchFilters.getOrderBy()).descending() ;
        return storeCategoryRepository.findAll(sort);
    }


    public List<StoreSubCategory> getAllStoreSubCategories(SearchFilters searchFilters) {
        Sort sort = searchFilters.getOrder().equals("asc") ?
                Sort.by(searchFilters.getOrderBy()).ascending() :
                Sort.by(searchFilters.getOrderBy()).descending() ;
        return storeSubCategoryRepository.getSubCategories(searchFilters.getCategoryId(),sort);
    }


    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    public StoreCategory saveOrUpdateStoreCategory(CategoryDto categoryDto){
        StoreCategory storeCategory = new StoreCategory();
        if(categoryDto.getId() != null)
        storeCategory.setId(categoryDto.getId());
        storeCategory.setCategory(categoryDto.getCategory());
        storeCategory.setIcon(categoryDto.getIcon());
        return storeCategoryRepository.save(storeCategory);
    }

    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    public StoreSubCategory saveOrUpdateStoreSubCategory(SubCategoryDto subCategoryDto){
        StoreSubCategory storeSubCategory = new StoreSubCategory();
        if(subCategoryDto.getId() != null)
        storeSubCategory.setId(subCategoryDto.getId());
        storeSubCategory.setCategoryId(subCategoryDto.getCategoryId());
        storeSubCategory.setSubcategory(subCategoryDto.getSubcategory());
        storeSubCategory.setIcon(subCategoryDto.getIcon());
        storeSubCategory.setUpdatedAt(Utils.getCurrentMillis());
        return storeSubCategoryRepository.save(storeSubCategory);
    }


    public StoreCategory getStoreCategoryById(int categoryId) {
        return storeCategoryRepository.findById(categoryId).get();
    }


    public int deleteStoreCategory(String slug,User user) {
        if(user.getUserType().equals("SA")) {
            int categoryId = storeHbRepository.getStoreCategoryIdBySLug(slug);
            storeHbRepository.switchCategoryToOther(categoryId);
            return storeHbRepository.deleteStoreCategory(slug);
        }
        return 0;
    }

    public int deleteStoreSubCategory(String slug,User user) {
        if(user.getUserType().equals("SA")) {
            int subCategoryId = storeSubCategoryRepository.getStoreSubCategoryIdBySlug(slug);
            storeHbRepository.switchSubCategoryToOther(subCategoryId);
            return storeHbRepository.deleteStoreSubCategory(slug);
        }
        return 0;
    }




}
