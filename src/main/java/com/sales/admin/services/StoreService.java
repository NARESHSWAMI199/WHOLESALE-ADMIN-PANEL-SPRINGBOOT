package com.sales.admin.services;

import com.sales.dto.*;
import com.sales.entities.*;
import com.sales.exceptions.MyException;
import com.sales.exceptions.NotFoundException;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import com.sales.utils.UploadImageValidator;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static com.sales.specifications.StoreSpecifications.*;


@Service
@RequiredArgsConstructor
public class StoreService extends RepoContainer{

    private final com.sales.helpers.Logger log;
    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    @Autowired
    AddressService addressService;


    @Value("${store.absolute}")
    String storeImagePath;



    public Page<Store> getAllStore(SearchFilters filters) {
        log.info(logger,"Entering getAllStore with filters: {}", filters);
        Specification<Store> specification = Specification.allOf(
                (containsName(filters.getSearchKey()).or(containsEmail(filters.getSearchKey())))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(isStatus(filters.getStatus()))
                        .and(hasSlug(filters.getSlug()))
        );
        Pageable pageable = getPageable(filters);
        Page<Store> storePage = storeRepository.findAll(specification,pageable);
        List<Store> storeList = storePage.getContent();
        storeList.forEach(store -> store.setTotalStoreItems(itemRepository.totalItemCountByWholesaleId(store.getId())));
        storePage = new PageImpl<>(storeList,pageable,storePage.getTotalElements());
        log.info(logger,"Exiting getAllStore");
        return storePage;
    }


    public Map<String, Integer> getWholesaleCounts () {
        log.info(logger,"Entering getWholesaleCounts");
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",storeRepository.totalWholesaleCount());
        responseObj.put("active",storeRepository.optionWholesaleCount("A"));
        responseObj.put("deactive",storeRepository.optionWholesaleCount("D"));
        log.info(logger,"Exiting getWholesaleCounts");
        return responseObj;
    }


    public AddressDto getAddressObjFromStore(StoreDto storeDto){
        log.info(logger,"Entering getAddressObjFromStore with storeDto: {}", storeDto);
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet(storeDto.getStreet());
        addressDto.setZipCode(storeDto.getZipCode());
        addressDto.setCity(storeDto.getCity());
        addressDto.setState(storeDto.getState());
        addressDto.setLatitude(storeDto.getLatitude());
        addressDto.setAltitude(storeDto.getAltitude());
        log.info(logger,"Exiting getAddressObjFromStore");
        return addressDto;
    }

    public Map<String,Object> getStoreCountByMonths(GraphDto graphDto){
        log.info(logger,"Entering getStoreCountByMonths with graphDto: {}", graphDto);
        List<Integer> months = graphDto.getMonths();
        months = (months == null || months.isEmpty()) ?
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12) : months;
        Integer year = graphDto.getYear();
        Map<String,Object> monthsObj= new LinkedHashMap<>();
        for(Integer month : months) {
            monthsObj.put(getMonthName(month),storeRepository.totalStoreViaMonth(month,year));
        }
        log.info(logger,"Exiting getStoreCountByMonths");
        return monthsObj;
    }

    public String getMonthName(int month) {
        log.info(logger,"Entering getMonthName with month: {}", month);
        if (month <= 0 || month > 12) {
            return null;
        }
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, new Locale("eng"));
        log.info(logger,"Exiting getMonthName");
        return monthName;
    }

    public void validateRequiredFieldsForStore(StoreDto storeDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering validateRequiredFieldsForStore with storeDto: {}", storeDto);
        List<String> requiredFields = new ArrayList<>(List.of(
            "storeName",
            "storeEmail",
            "storePhone",
            "rating",
            "categoryId",
            "subCategoryId",
            "description"
        ));
        // if there is any required field null, then this will throw IllegalArgumentException
        Utils.checkRequiredFields(storeDto,requiredFields);
        log.info(logger,"Exiting validateRequiredFieldsForStore");
    }

    public void validateRequiredFieldsForCreateStore(StoreDto storeDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering validateRequiredFieldsForCreateStore with storeDto: {}", storeDto);
        List<String> requiredFields = new ArrayList<>(List.of("userSlug"));
        // if there is any required field null, then this will throw IllegalArgumentException
        Utils.checkRequiredFields(storeDto,requiredFields);
        log.info(logger,"Exiting validateRequiredFieldsForCreateStore");
    }


    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class})
    public Map<String, Object> createOrUpdateStore(StoreDto storeDto,User loggedUser,String path) throws MyException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering createOrUpdateStore with storeDto: {}, loggedUser: {}, path: {}", storeDto, loggedUser, path);
            Map<String, Object> responseObj = new HashMap<>();
            // if there is any required field null, then this will throw IllegalArgumentException
            validateRequiredFieldsForStore(storeDto);
            try {
                StoreCategory storeCategory = storeCategoryRepository.findById(storeDto.getCategoryId()).orElseThrow(() -> new NotFoundException("Store category not found."));
                storeDto.setStoreCategory(storeCategory);
                StoreSubCategory storeSubCategory = storeSubCategoryRepository.findById(storeDto.getSubCategoryId()).orElseThrow(() -> new NotFoundException("Store subcategory not found."));
                storeDto.setStoreSubCategory(storeSubCategory);
            } catch (Exception e){
                throw new IllegalArgumentException("Invalid arguments for category and subcategory");
            }

            if (!Utils.isEmpty(storeDto.getStoreSlug()) || path.contains("update")) { // We are going to update the store.
                log.info(logger,"We are going to update the store.");
                // if there is any required field null, then this will throw IllegalArgumentException
                Utils.checkRequiredFields(storeDto,List.of("storeSlug"));

                String storeName = Utils.isValidName(storeDto.getStoreName(),ConstantResponseKeys.STORE);
                storeDto.setStoreName(storeName);
                // If we found any issue with email and mobile, this will throw exception
                Utils.mobileAndEmailValidation(storeDto.getStoreEmail(), storeDto.getStorePhone(), "Not a valid store's _ recheck your and store's _.");
                updateStoreImage(storeDto.getStorePic(), storeDto.getStoreSlug());

                // before update store and store's address get address id from store
                Integer addressId = storeRepository.getAddressIdBySlug(storeDto.getStoreSlug());
                if(addressId == null) throw new IllegalArgumentException("No store found to update.");  // wrong wholesale slug.
                storeDto.setAddressId(addressId);

                int isUpdated = updateStore(storeDto, loggedUser);
                if (isUpdated > 0) {
                    responseObj.put(ConstantResponseKeys.MESSAGE, "Successfully updated.");
                    responseObj.put(ConstantResponseKeys.STATUS, 200);
                } else {
                    responseObj.put(ConstantResponseKeys.MESSAGE, "Nothing found to updated.");
                    responseObj.put(ConstantResponseKeys.STATUS, 404);
                }
            } else {  // We are going to create a store.
                log.info(logger,"We are going to create the store.");
                // if there is any required field null, then this will throw IllegalArgumentException
                validateRequiredFieldsForCreateStore(storeDto);

                // if there is any issue, this will throw IllegalArgumentException
                Utils.mobileAndEmailValidation(
                    storeDto.getStoreEmail(),
                    storeDto.getStorePhone(),
                    "Not a valid store's _ recheck your and store's _."
                );

                String storeName = Utils.isValidName(storeDto.getStoreName(),ConstantResponseKeys.STORE);
                storeDto.setStoreName(storeName);
                Store createdStore = createStore(storeDto, loggedUser);
                responseObj.put(ConstantResponseKeys.RES, createdStore);
                responseObj.put(ConstantResponseKeys.MESSAGE, "Successfully inserted.");
                responseObj.put(ConstantResponseKeys.STATUS, 201);
            }
        log.info(logger,"Exiting createOrUpdateStore");
        return responseObj;

    }



    public void validateRequiredFieldsForCreateAddress(AddressDto addressDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering validateRequiredFieldsForCreateAddress with addressDto: {}", addressDto);
        List<String> requiredFields = new ArrayList<>(List.of("street","zipCode", "city","state"));
        // if there is any required field null, then this will throw IllegalArgumentException
        Utils.checkRequiredFields(addressDto,requiredFields);
        log.info(logger,"Exiting validateRequiredFieldsForCreateAddress");
    }


    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class})
    public Store createStore(StoreDto storeDto , User loggedUser) throws MyException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering createStore with storeDto: {}, loggedUser: {}", storeDto, loggedUser);
        /** inserting address during create a wholesale */
        AddressDto addressDto = getAddressObjFromStore(storeDto);
        // if there is any required field null then this will throw IllegalArgumentException
        validateRequiredFieldsForCreateAddress(addressDto);
        Address address =  addressService.insertAddress(addressDto,loggedUser);

        /** @END inserting  address during create a wholesale */
        Optional<User> storeOwner = userRepository.findByWholesalerSlug(storeDto.getUserSlug());
        if (storeOwner.isEmpty())  throw new PermissionDeniedDataAccessException("User must be wholesaler.",new Exception());


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
        log.info(logger,"Exiting createStore");
        return storeRepository.save(store);
    }

    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class})
    public int updateStore(StoreDto storeDto, User loggedUser){
        log.info(logger,"Entering updateStore with storeDto: {}, loggedUser: {}", storeDto, loggedUser);
        AddressDto address = new AddressDto();
        address.setStreet(storeDto.getStreet());
        address.setZipCode(storeDto.getZipCode());
        address.setState(storeDto.getState());
        address.setCity(storeDto.getCity());
        address.setState(storeDto.getState());
        address.setAddressId(storeDto.getAddressId());
        int isUpdatedAddress = addressHbRepository.updateAddress(address,loggedUser);
        if(isUpdatedAddress < 1) return isUpdatedAddress;
        int result = storeHbRepository.updateStore(storeDto,loggedUser);
        log.info(logger,"Exiting updateStore");
        return result;
    }

    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class,Exception.class})
    public int deleteStoreBySlug(DeleteDto deleteDto,User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering deleteStoreBySlug with deleteDto: {}, loggedUser: {}", deleteDto, loggedUser);
        // Validate required fields. if we found any required field this will throw IllegalArgumentException
        Utils.checkRequiredFields(deleteDto,List.of("slug"));

        String slug = deleteDto.getSlug();
        Store store = storeRepository.findStoreBySlug(slug);
        if(store == null) throw new NotFoundException("No store found to delete.");
        User user = store.getUser();
        if(user != null) userHbRepository.deleteUserBySlug(user.getSlug());
        int result = storeHbRepository.deleteStore(slug,loggedUser);
        log.info(logger,"Exiting deleteStoreBySlug");
        return result;

    }

    public void deleteStoreByUserId(int userId){
        log.info(logger,"Entering deleteStoreByUserId with userId: {}", userId);
        storeHbRepository.deleteStore(userId);
        log.info(logger,"Exiting deleteStoreByUserId");
    }


    @Transactional
    public Store getStoreDetails(String slug){
        log.info(logger,"Entering getStoreDetails with slug: {}", slug);
        Store store = storeRepository.findStoreBySlug(slug);
        log.info(logger,"Exiting getStoreDetails");
        return store;
    }

    public Integer getStoreIdByStoreSlug(String slug){
        log.info(logger,"Entering getStoreIdByStoreSlug with slug: {}", slug);
        Integer storeId = storeRepository.getStoreIdByStoreSlug(slug);
        log.info(logger,"Exiting getStoreIdByStoreSlug");
        return storeId;
    }

    public Store getStoreByUserSlug(String userSlug) {
        log.info(logger,"Entering getStoreByUserSlug with userSlug: {}", userSlug);
        if(Utils.isEmpty(userSlug)) throw new IllegalArgumentException("User slug can't be null or blank.");
        User user = userRepository.findUserBySlug(userSlug);
        if (user == null) throw new NotFoundException("No user found.");
        Store store = storeRepository.findStoreByUserId(user.getId());
        if(store == null) throw new NotFoundException("Store not found.");
        // setting total items to with store detail
        store.setTotalStoreItems(itemRepository.totalItemCountByWholesaleId(store.getId()));
        log.info(logger,"Exiting getStoreByUserSlug");
        return store;
    }


    @Transactional(rollbackOn = {IllegalArgumentException.class, MyException.class,RuntimeException.class,Exception.class})
    public int updateStatusBySlug(StatusDto statusDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering updateStatusBySlug with statusDto: {}", statusDto);
        // Validate required fields. if we found any required field this will throw IllegalArgumentException
        Utils.checkRequiredFields(statusDto,List.of("status","slug"));

        switch (statusDto.getStatus()) {
            case "A", "D":
                Store store = storeRepository.findStoreBySlug(statusDto.getSlug());
                if (store == null) throw new NotFoundException("No store found to update.");
                String status = statusDto.getStatus();
                // updating store user status also
                store.getUser().setStatus(statusDto.getStatus());
                store.setStatus(status);
                int result = storeRepository.save(store).getId();
                log.info(logger,"Exiting updateStatusBySlug");
                return result;
            default:
                throw new IllegalArgumentException("Status must be A or D.");
        }
    }



    @Transactional(rollbackOn = {IllegalArgumentException.class, MyException.class,RuntimeException.class,Exception.class})
    public int updateStoreImage(MultipartFile storeImage, String slug) throws MyException, IOException {
        log.info(logger,"Entering updateStoreImage with storeImage: {}, slug: {}", storeImage, slug);
        if(storeImage !=null ) {
            if (UploadImageValidator.isValidImage(storeImage, GlobalConstant.bannerMinWidth,
                GlobalConstant.bannerMinHeight, GlobalConstant.bannerMaxWidth, GlobalConstant.bannerMaxHeight,
                GlobalConstant.allowedAspectRatios, GlobalConstant.allowedFormats)) {
                    String fileOriginalName = Objects.requireNonNull(storeImage.getOriginalFilename()).replaceAll(" ", "_");
                    String dirPath = storeImagePath+GlobalConstant.PATH_SEPARATOR+slug+GlobalConstant.PATH_SEPARATOR;
                    File dir = new File(dirPath);
                    if(!dir.exists()) dir.mkdirs();
                    File file = new File(dirPath+fileOriginalName);
                    storeImage.transferTo(file);
                    int result = storeHbRepository.updateStoreAvatar(slug,fileOriginalName);
                    log.info(logger,"Exiting updateStoreImage");
                    return result;
            } else {
                throw new IllegalArgumentException("Image is not fit in accept ratio. please resize you image before upload.");
            }
        }
        log.info(logger,"Exiting updateStoreImage");
        return 0;
    }


    public List<StoreCategory> getAllStoreCategory(SearchFilters searchFilters) {
        log.info(logger,"Entering getAllStoreCategory with searchFilters: {}", searchFilters);
        Sort sort = searchFilters.getOrder().equals("asc") ?
                Sort.by(searchFilters.getOrderBy()).ascending() :
                Sort.by(searchFilters.getOrderBy()).descending() ;
        List<StoreCategory> result = storeCategoryRepository.findAll(sort);
        log.info(logger,"Exiting getAllStoreCategory");
        return result;
    }


    public List<StoreSubCategory> getAllStoreSubCategories(SearchFilters searchFilters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering getAllStoreSubCategories with searchFilters: {}", searchFilters);
        // Validating required fields if found any required field is null, this will throw IllegalArgumentException
        Utils.checkRequiredFields(searchFilters,List.of("categoryId"));
        Sort sort = Sort.by(searchFilters.getOrderBy());
        sort  = searchFilters.getOrder().equals("asc") ? sort.ascending() : sort.descending();
        List<StoreSubCategory> result = storeSubCategoryRepository.getSubCategories(searchFilters.getCategoryId(),sort);
        log.info(logger,"Exiting getAllStoreSubCategories");
        return result;
    }


    @Transactional(rollbackOn = {MyException.class ,IllegalArgumentException.class,RuntimeException.class})
    public StoreCategory saveOrUpdateStoreCategory(CategoryDto categoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering saveOrUpdateStoreCategory with categoryDto: {}", categoryDto);
        // Validate required fields if we found any given field is null, then this will throw Exception
        Utils.checkRequiredFields(categoryDto,List.of("category","icon"));

        StoreCategory storeCategory = new StoreCategory();
        if(categoryDto.getId() != null)
            storeCategory.setId(categoryDto.getId());
        storeCategory.setCategory(categoryDto.getCategory());
        storeCategory.setIcon(categoryDto.getIcon());
        StoreCategory result = storeCategoryRepository.save(storeCategory);
        log.info(logger,"Exiting saveOrUpdateStoreCategory");
        return result;
    }

    @Transactional(rollbackOn = {MyException.class ,IllegalArgumentException.class,RuntimeException.class})
    public StoreSubCategory saveOrUpdateStoreSubCategory(SubCategoryDto subCategoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering saveOrUpdateStoreSubCategory with subCategoryDto: {}", subCategoryDto);
        // Validate required fields if we found any given field is null, then this will throw Exception
        Utils.checkRequiredFields(subCategoryDto,List.of("categoryId","subcategory","icon"));
        StoreSubCategory storeSubCategory = new StoreSubCategory();
        if(subCategoryDto.getId() != null)
            storeSubCategory.setId(subCategoryDto.getId());
        storeSubCategory.setCategoryId(subCategoryDto.getCategoryId());
        storeSubCategory.setSubcategory(subCategoryDto.getSubcategory());
        storeSubCategory.setIcon(subCategoryDto.getIcon());
        storeSubCategory.setUpdatedAt(Utils.getCurrentMillis());
        StoreSubCategory result = storeSubCategoryRepository.save(storeSubCategory);
        log.info(logger,"Exiting saveOrUpdateStoreSubCategory");
        return result;
    }


    public StoreCategory getStoreCategoryById(int categoryId) {
        log.info(logger,"Entering getStoreCategoryById with categoryId: {}", categoryId);
        StoreCategory result = storeCategoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Store category not found."));
        log.info(logger,"Exiting getStoreCategoryById");
        return result;
    }


    public int deleteStoreCategory(DeleteDto deleteDto,User user) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering deleteStoreCategory with deleteDto: {}, user: {}", deleteDto, user);
        // Validating required fields if they are null, this will throw an Exception
        Utils.checkRequiredFields(deleteDto,List.of("slug"));
        if (!user.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("Only super admin can delete a store category.",new Exception());
        String slug = deleteDto.getSlug();
        Integer categoryId = storeHbRepository.getStoreCategoryIdBySLug(slug);
        if (categoryId == null) throw new NotFoundException("Store's category not found.");
        storeHbRepository.switchCategoryToOther(categoryId);  // before delete category assign store to the other category.
        int result = storeHbRepository.deleteStoreCategory(slug);
        log.info(logger,"Exiting deleteStoreCategory");
        return result;
    }

    public int deleteStoreSubCategory(DeleteDto deleteDto,User user) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        log.info(logger,"Entering deleteStoreSubCategory with deleteDto: {}, user: {}", deleteDto, user);
        // Validating required fields if they are null this will throw an Exception
        Utils.checkRequiredFields(deleteDto,List.of("slug"));
        String slug = deleteDto.getSlug();
        if (!user.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("Only super admin can delete a store subcategory.",new Exception());
        Integer subCategoryId = storeSubCategoryRepository.getStoreSubCategoryIdBySlug(slug);
        if (subCategoryId == null) throw new NotFoundException("Store's subcategory not found.");
        storeHbRepository.switchSubCategoryToOther(subCategoryId);
        int result = storeHbRepository.deleteStoreSubCategory(slug);
        log.info(logger,"Exiting deleteStoreSubCategory");
        return result;
    }




}
