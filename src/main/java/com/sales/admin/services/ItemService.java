package com.sales.admin.services;


import com.google.gson.Gson;
import com.sales.admin.repositories.ItemHbRepository;
import com.sales.dto.*;
import com.sales.entities.*;
import com.sales.exceptions.MyException;
import com.sales.exceptions.NotFoundException;
import com.sales.global.GlobalConstant;
import com.sales.utils.UploadImageValidator;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.sales.specifications.ItemsSpecifications.*;

@Service
public class ItemService extends RepoContainer{

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Value("${item.absolute}")
    String itemImagePath;


    public Page<Item> getAllItems(ItemSearchFields searchFilters) {
        logger.info("Entering getAllItems with searchFilters: {}", searchFilters);
        Sort sort = searchFilters.getOrder().equalsIgnoreCase("asc") ?
                Sort.by(searchFilters.getOrderBy()).ascending() :
                Sort.by(searchFilters.getOrderBy()).descending();
        Specification<Item> specification = Specification.where(
            containsName(searchFilters.getSearchKey().trim())
                .and(isWholesale(searchFilters.getStoreId()))
                .and(isStatus(searchFilters.getStatus()))
                .and(inStock(searchFilters.getInStock()))
                .and(greaterThanOrEqualFromDate(searchFilters.getFromDate()))
                .and(lessThanOrEqualToToDate(searchFilters.getToDate()))
        );
        Pageable pageable = PageRequest.of(searchFilters.getPageNumber(), searchFilters.getSize(), sort);
        Page<Item> result = itemRepository.findAll(specification,pageable);
        logger.info("Exiting getAllItems");
        return result;
    }


    public Map<String, List<Object>> createItemsExcelSheet(SearchFilters searchFilters) throws IOException {
        logger.info("Entering createItemsExcelSheet with searchFilters: {}", searchFilters);
        int wholesaleId = searchFilters.getStoreId();
        Long fromDate = searchFilters.getFromDate();
        Long toDate = searchFilters.getToDate();
        List<Item> itemsList =  itemRepository.getAllItemsWithFilters(wholesaleId,fromDate,toDate);
        Map<String,List<Object>> result = new HashMap<>();
        for (Item item : itemsList){
            String items = new Gson().toJson(item);
            Map<String,Object> itemMap = new Gson().fromJson(items,Map.class);
            itemMap.forEach((key,value)->{
                if(key.equals("wholesale")){
                    // skip...
                }
                else if (result.containsKey(key.toUpperCase())){
                    result.get(key.toUpperCase()).add(itemMap.get(key));
                }else {
                    List<Object> valueList = new ArrayList<>();
                    valueList.add((String) value);
                    result.put(key.toUpperCase(),valueList);
                }
            });
        }
        int totalItem = itemsList.size();
        String [] headers = {"SLUG","NAME","LABEL","CAPACITY","DESCRIPTION", "PRICE", "DISCOUNT", "RATING","INSTOCK","STATUS","CREATEDAT","UPDATEDAT"};
        writeExcel.writeExcel(result,totalItem,Arrays.asList(headers));
        logger.info("Exiting createItemsExcelSheet");
        return  result;
    }

    public Map<String, Integer> getItemCounts () {
        logger.info("Entering getItemCounts");
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",itemRepository.totalItemCount());
        responseObj.put("active",itemRepository.optionItemCount("A"));
        responseObj.put("deactive",itemRepository.optionItemCount("D"));
        logger.info("Exiting getItemCounts");
        return responseObj;
    }


    public Item findItemBySLug(String slug) {
        logger.info("Entering findItemBySLug with slug: {}", slug);
        Item result = itemRepository.findItemBySlug(slug);
        logger.info("Exiting findItemBySLug");
        return result;
    }



    public void validateRequiredFields(ItemDto itemDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering validateRequiredFields with itemDto: {}", itemDto);
        // if there is any required field null, then this will throw IllegalArgumentException
        Utils.checkRequiredFields(itemDto,List.of(
                "name",
                "price",
                "discount",
                "description",
//                "capacity",
                "categoryId",
                "subCategoryId"
        ));
        logger.info("Exiting validateRequiredFields");
    }

    public void validateRequiredFieldsBeforeCreateItem(ItemDto itemDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering validateRequiredFieldsBeforeCreateItem with itemDto: {}", itemDto);
        /** @Note during creation, we are checking only extra required params  */
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(itemDto,List.of(
                "wholesaleSlug",
                "rating",
                "inStock",
                "label",
                "newItemImages"
        ));
        logger.info("Exiting validateRequiredFieldsBeforeCreateItem");
    }

    @Transactional(rollbackOn = {MyException.class,IllegalArgumentException.class,RuntimeException.class,})
    public Map<String, Object> createOrUpdateItem(ItemDto itemDto, User loggedUser,String path) throws InvocationTargetException, NoSuchMethodException, IOException, IllegalAccessException {
        logger.info("Entering createOrUpdateItem with itemDto: {}, loggedUser: {}, path: {}", itemDto, loggedUser, path);
        // if there is any required field null then this will throw IllegalArgumentException
        validateRequiredFields(itemDto);

        // Validate inStock
        if (!(itemDto.getInStock().equals("N") || itemDto.getInStock().equals("Y"))) throw new IllegalArgumentException("inStock must be 'Y' or 'N'.");
        // Validate label
        if (!(itemDto.getLabel().equals("N") || itemDto.getLabel().equals("O"))) throw new IllegalArgumentException("label must be 'O' or 'N'.");
        // Validate price and discount
        if(itemDto.getPrice() < itemDto.getDiscount() || itemDto.getDiscount() < 0) throw new IllegalArgumentException("Discount can't be greater then price and can't be less then 0.");

        // Verify item name syntax
        String itemName = Utils.isValidName( itemDto.getName(),"item");
        itemDto.setName(itemName);

        // retrieve category and subcategory
        ItemCategory itemCategory = itemCategoryRepository.findById(itemDto.getCategoryId()).get();
        if(itemCategory == null) throw new IllegalArgumentException("Invalid categoryId.");
        ItemSubCategory itemSubCategory = itemSubCategoryRepository.findById(itemDto.getSubCategoryId()).get();
        if(itemSubCategory == null) throw new IllegalArgumentException("Invalid subCategoryId.");
        itemDto.setItemCategory(itemCategory);
        itemDto.setItemSubCategory(itemSubCategory);

        Map<String, Object> responseObj = new HashMap<>();

        // Going to update item
        if (!Utils.isEmpty(itemDto.getSlug()) || path.contains("update")) {
            logger.info("We are going to update the item.");
            // if there is any required field null then this will throw IllegalArgumentException
            Utils.checkRequiredFields(itemDto,List.of("slug"));

            int isUpdated = updateItem(itemDto, loggedUser);
            // updating item images
            updateStoreImage(itemDto.getPreviousItemImages(),itemDto.getNewItemImages(),itemDto.getSlug(),"update");
            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 200);
            } else {
                responseObj.put("message", "No item found to update.");
                responseObj.put("status", 404);
            }
        } else { // Going to create item
            logger.info("We are going to create the item.");
            // if there is any required field null then this will throw IllegalArgumentException
            validateRequiredFieldsBeforeCreateItem(itemDto);
            Item createdItem = createItem(itemDto, loggedUser);
            responseObj.put("res", createdItem);
            responseObj.put("message", "Successfully inserted.");
            responseObj.put("status", 201);
        }
        logger.info("Exiting createOrUpdateItem");
        return responseObj;

    }


    @Transactional
    public Item createItem (ItemDto itemDto, User loggedUser) throws IOException {
        logger.info("Entering createItem with itemDto: {}, loggedUser: {}", itemDto, loggedUser);
        Item item = new Item();
        Store store = storeRepository.findStoreBySlug(itemDto.getWholesaleSlug());
        if (store == null) throw new IllegalArgumentException("Not a valid store.");
        String slug = UUID.randomUUID().toString();
        item.setWholesaleId(store.getId());
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        item.setDiscount(itemDto.getDiscount());
        item.setRating(itemDto.getRating());
        item.setDescription(itemDto.getDescription());
        item.setInStock(itemDto.getInStock());
        item.setUpdatedAt(Utils.getCurrentMillis());
        item.setCreatedAt(Utils.getCurrentMillis());
        item.setCreatedBy(loggedUser.getId());
        item.setUpdatedBy(loggedUser.getId());
        item.setLabel(itemDto.getLabel());
        item.setCapacity(itemDto.getCapacity());
        item.setSlug(slug);
        item.setItemCategory(itemDto.getItemCategory());
        item.setItemSubCategory(itemDto.getItemSubCategory());
        item.setAvtars(updateStoreImage("",itemDto.getNewItemImages(),slug,"create"));
        Item result = itemRepository.save(item);
        logger.info("Exiting createItem");
        return result;
    }



    @Transactional
    public int updateItem(ItemDto itemDto, User loggedUser) {
        logger.info("Entering updateItem with itemDto: {}, loggedUser: {}", itemDto, loggedUser);
        Item item = findItemBySLug(itemDto.getSlug());
        String title = "Item " + item.getName() + " updated.";
        String messageBody = "Item " + item.getName() + " key : " + item.getSlug() + " updated by admin previous data was "+
                item.toString()
                +". If you have any issue please contact to administrator.";
        sendNotification(title,messageBody,item.getWholesaleId(),loggedUser);
        int result = itemHbRepository.updateItems(itemDto,loggedUser);
        logger.info("Exiting updateItem");
        return result;
    }


    @Transactional
    public void sendNotification(String title,String messageBody,int storeId,User loggedUser){
        logger.info("Entering sendNotification with title: {}, messageBody: {}, storeId: {}, loggedUser: {}", title, messageBody, storeId, loggedUser);
        StoreNotifications storeNotifications = new StoreNotifications();
        storeNotifications.setTitle(title);
        storeNotifications.setMessageBody(messageBody);
        storeNotifications.setWholesaleId(storeId);
        storeNotifications.setCreatedBy(loggedUser);
        storeHbRepository.insertStoreNotifications(storeNotifications);
        logger.info("Exiting sendNotification");
    }

    @Transactional
    public int deleteItem(DeleteDto deleteDto,User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering deleteItem with deleteDto: {}, loggedUser: {}", deleteDto, loggedUser);
        // Verify required fields if any issue found this will throw  IllegalArgumentException
        Utils.checkRequiredFields(deleteDto,List.of("slug"));
        String slug = deleteDto.getSlug();
        Item item = findItemBySLug(slug);
        if (item == null) throw new NotFoundException("Item not found to delete.");
        String title = "Item " + item.getName() + " deleted.";
        String messageBody = "Item " + item.getName() + " key : " + item.getSlug() + " deleted by admin. If you have any issue please contact to administrator.";
        sendNotification(title,messageBody,item.getWholesaleId(),loggedUser);
        int result = itemHbRepository.deleteItem(slug);
        logger.info("Exiting deleteItem");
        return result;
    }


    public int updateStock(String stock, String slug) {
        logger.info("Entering updateStock with stock: {}, slug: {}", stock, slug);
        if(!Utils.isEmpty(slug)){
            if(Utils.isEmpty(stock) || !(stock.equals("Y") || stock.equals("N")))
                throw new IllegalArgumentException("The key stock must be 'Y' or 'N'.");
            int result = itemHbRepository.updateStock(stock,slug);
            logger.info("Exiting updateStock with result: {}", result);
            return result;
        }
        throw new IllegalArgumentException("The key slug can't be blank.");
    }

    public int updateStatusBySlug(StatusDto statusDto,User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering updateStatusBySlug with statusDto: {}, loggedUser: {}", statusDto, loggedUser);
        // Verify required fields update item status
        Utils.checkRequiredFields(statusDto, List.of("status","slug"));
        switch (statusDto.getStatus()){
            case "A", "D":
                Item item = findItemBySLug(statusDto.getSlug());
                if(item == null) return 0;
                String title = "";
                String messageBody= "";
                if(statusDto.getStatus().equals("D")) {
                    title = "Item " + item.getName() + " deactivated";
                    messageBody = "Item " + item.getName() + " key : " + item.getSlug() + " deactivated by admin because it's legal policy issue. If you have any issue please contact to administrator.";
                }else{
                    title= "Item " + item.getName() + " activated";
                    messageBody = "Item " + item.getName() + " key : " + item.getSlug() + " activated successfully by admin.";
                }
                sendNotification(title,messageBody,item.getWholesaleId(),loggedUser);
                int result = itemHbRepository.updateStatus(statusDto.getSlug(),statusDto.getStatus());
                logger.info("Exiting updateStatusBySlug with result: {}", result);
                return result;
            default:
                throw new IllegalArgumentException("Status must be A or D.");
        }
    }


    @Transactional(rollbackOn = {RuntimeException.class, Exception.class})
    public int insertAllItemsWithExcel (Map<String,List<String>> excel,Integer userId, Integer wholesaleId){
        logger.info("Entering insertAllItems with excel: {}, userId: {}, wholesaleId: {}", excel, userId, wholesaleId);
        userId = userId == null ? 0 : userId;
        wholesaleId = wholesaleId == null ? 0 : wholesaleId;
        int result = itemHbRepository.insertItemsList(excel,userId,wholesaleId);
        logger.info("Exiting insertAllItems with result: {}", result);
        return result;
    }


    @Transactional(rollbackOn = {RuntimeException.class,Exception.class})
    public List<ItemHbRepository.ItemUpdateError> updateItemsWithExcel(Map<String,List<String>> excelData, Integer userId, Integer wholesaleId){
        logger.info("Updating items using excel sheet : {} and userId : {} and wholesaleId : {}",excelData,userId,wholesaleId);
        List<ItemHbRepository.ItemUpdateError> result = itemHbRepository.updateItemsViaExcelSheet(excelData,userId,wholesaleId);
        logger.info("Exiting updateItemsWithExcel with result: {}", result);
        return result;
    }

    public String updateStoreImage(String previousImages, List<MultipartFile> itemImages,String slug,String action) throws IOException {
        logger.info("Entering updateStoreImage with previousImages: {}, itemImages: {}, slug: {}, action: {}", previousImages, itemImages, slug, action);
        String newImages = "";
        int index = 0;
        if(itemImages != null) {
            for (MultipartFile multipartFile : itemImages) {
                if (index == itemImages.size() - 1) {
                    newImages += saveItemImageName(multipartFile, slug);
                } else {
                    newImages += saveItemImageName(multipartFile, slug) + ",";
                }
                index += 1;
            }
        }
        String updatedImages = "";
        if(!Utils.isEmpty(previousImages) && !Utils.isEmpty(newImages)) {
            updatedImages =  previousImages+newImages;
        }else if(Utils.isEmpty(previousImages)){
            updatedImages = newImages;
        }else {
            // because it's contained ',' at the end
            updatedImages = previousImages.substring(0,previousImages.length()-1);
        }
        if(!Utils.isEmpty(updatedImages) && action.equalsIgnoreCase("update")){
            itemHbRepository.updateItemImage(slug, updatedImages);
        }
        logger.info("Exiting updateStoreImage with result: {}", updatedImages);
        return updatedImages;
    }


    @Transactional
    public String saveItemImageName(MultipartFile itemImage, String slug) throws IOException {
        logger.info("Entering saveItemImageName with itemImage: {}, slug: {}", itemImage, slug);
        if(itemImage !=null) {
            if (UploadImageValidator.isValidImage(itemImage, GlobalConstant.minWidth,
                    GlobalConstant.minHeight, GlobalConstant.maxWidth, GlobalConstant.maxHeight,
                    GlobalConstant.allowedAspectRatios, GlobalConstant.allowedFormats)) {

                    String fileOriginalName = UUID.randomUUID()+itemImage.getOriginalFilename().replaceAll(" ", "_");
                    String dirPath = itemImagePath+slug+"/";
                    File dir = new File(dirPath);
                    if(!dir.exists()) dir.mkdirs();
                    String filePath = dirPath+fileOriginalName;
                    File file = new File(filePath);

                    itemImage.transferTo(file);
                    //if (!UploadImageValidator.hasWhiteBackground(new File(filePath))) throw new MyException("Image must have a white background");
                    logger.info("Exiting saveItemImageName with result: {}", fileOriginalName);
                    return fileOriginalName;
            } else {
                throw new MyException("Image is not fit in accept ratio. please resize you image before upload.");
            }
        }
        throw new MyException("Item image can't be null. Something went wrong please contact to administrator.");
    }



    public List<ItemCategory> getAllCategory(SearchFilters searchFilters) {
        logger.info("Entering getAllCategory with searchFilters: {}", searchFilters);
        Sort sort = searchFilters.getOrder().equals("asc") ?
            Sort.by(searchFilters.getOrderBy()).ascending() :
            Sort.by(searchFilters.getOrderBy()).descending() ;
        List<ItemCategory> result = itemCategoryRepository.findAll(sort);
        logger.info("Exiting getAllCategory with result: {}", result);
        return result;
    }



    public ItemCategory getItemCategoryById(int categoryId) {
        logger.info("Entering getItemCategoryById with categoryId: {}", categoryId);
        ItemCategory result = itemCategoryRepository.findById(categoryId).get();
        logger.info("Exiting getItemCategoryById with result: {}", result);
        return result;
    }

    public int deleteItemCategory(DeleteDto deleteDto,User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering deleteItemCategory with deleteDto: {}, loggedUser: {}", deleteDto, loggedUser);
        // Validating required fields if they are null, this will throw an Exception
        Utils.checkRequiredFields(deleteDto,List.of("slug"));
        String slug = deleteDto.getSlug();
        // only super admin can delete it subcategory
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("Only super admin can delete item's category.",null);
        Integer categoryId = itemCategoryRepository.getItemCategoryIdBySlug(slug);
        if (categoryId == null) throw new NotFoundException("Category not found.");
        itemHbRepository.switchCategoryToOther(categoryId); // before delete category, assign item to another category.
        int result = itemHbRepository.deleteItemCategory(slug);
        logger.info("Exiting deleteItemCategory with result: {}", result);
        return result;

    }

    public int deleteItemSubCategory(DeleteDto deleteDto,User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering deleteItemSubCategory with deleteDto: {}, loggedUser: {}", deleteDto, loggedUser);
        // Validating required fields if they are null, this will throw an Exception
        Utils.checkRequiredFields(deleteDto,List.of("slug"));
        String slug = deleteDto.getSlug();
        // only super admin can delete it subcategory
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("Only super admin can delete subcategory.",null);
        Integer subCategoryId = itemSubCategoryRepository.getItemSubCategoryIdBySlug(slug);
        if (subCategoryId == null) throw new NotFoundException("Subcategory not found.");
        itemHbRepository.switchSubCategoryToOther(subCategoryId); // before delete category assign item to other subcategory.
        int result = itemHbRepository.deleteItemSubCategory(slug);
        logger.info("Exiting deleteItemSubCategory with result: {}", result);
        return result;

    }


    public List<ItemSubCategory> getAllItemsSubCategories(SearchFilters searchFilters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering getAllItemsSubCategories with searchFilters: {}", searchFilters);
        // Validating required fields if found any required field is null, this will throw IllegalArgumentException
        Utils.checkRequiredFields(searchFilters,List.of("categoryId"));

        Sort sort = Sort.by(searchFilters.getOrderBy());
        sort  = searchFilters.getOrder().equals("asc") ? sort.ascending() : sort.descending();
        List<ItemSubCategory> result = itemSubCategoryRepository.getSubCategories(searchFilters.getCategoryId(),sort);
        logger.info("Exiting getAllItemsSubCategories with result: {}", result);
        return result;
    }


    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    public ItemCategory saveOrUpdateItemCategory(CategoryDto categoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering saveOrUpdateItemCategory with categoryDto: {}", categoryDto);
        // Validate required fields if we found any given field is null, then this will throw Exception
        Utils.checkRequiredFields(categoryDto,List.of("category","icon"));
        ItemCategory itemCategory = new ItemCategory();
        if(categoryDto.getId() != null && categoryDto.getId() !=0) // because we are using 0 for the other category.
            itemCategory.setId(categoryDto.getId());
        itemCategory.setSlug(UUID.randomUUID().toString());  // slug will also change after during update
        itemCategory.setCategory(categoryDto.getCategory());
        itemCategory.setIcon(categoryDto.getIcon());
        ItemCategory result = itemCategoryRepository.save(itemCategory);
        logger.info("Exiting saveOrUpdateItemCategory with result: {}", result);
        return result;
    }

    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    public ItemSubCategory saveOrUpdateItemSubCategory(SubCategoryDto subCategoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering saveOrUpdateItemSubCategory with subCategoryDto: {}", subCategoryDto);
        // Validate required fields if we found any given field is null, then this will throw Exception
        Utils.checkRequiredFields(subCategoryDto,List.of("categoryId","subcategory","unit","icon"));
        ItemSubCategory itemSubCategory = new ItemSubCategory();
        if(subCategoryDto.getId() != null && subCategoryDto.getId() != 0) // because we are using 0 for the other subcategory.
            itemSubCategory.setId(subCategoryDto.getId());
        itemSubCategory.setSlug(UUID.randomUUID().toString()); // slug will also change after during update
        itemSubCategory.setCategoryId(subCategoryDto.getCategoryId());
        itemSubCategory.setSubcategory(subCategoryDto.getSubcategory());
        itemSubCategory.setIcon(subCategoryDto.getIcon());
        itemSubCategory.setUnit(subCategoryDto.getUnit());
        itemSubCategory.setUpdatedAt(Utils.getCurrentMillis());
        ItemSubCategory result = itemSubCategoryRepository.save(itemSubCategory);
        logger.info("Exiting saveOrUpdateItemSubCategory with result: {}", result);
        return result;
    }


    public List<MeasurementUnit> getAllMeasurementUnit() {
        logger.info("Entering getAllMeasurementUnit");
        Sort sort = Sort.by("unit").ascending();
        List<MeasurementUnit> result = measurementUnitRepository.findAll(sort);
        logger.info("Exiting getAllMeasurementUnit with result: {}", result);
        return result;
    }


}
