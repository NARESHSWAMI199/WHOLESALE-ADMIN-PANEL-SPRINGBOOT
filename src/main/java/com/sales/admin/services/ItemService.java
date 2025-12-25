package com.sales.admin.services;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sales.admin.repositories.ItemHbRepository;
import com.sales.dto.*;
import com.sales.entities.*;
import com.sales.exceptions.MyException;
import com.sales.exceptions.NotFoundException;
import com.sales.global.ConstantResponseKeys;
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


    public Page<Item> getAllItems(ItemSearchFields searchFilters,User loggedUser) {
        logger.info("Entering getAllItems with searchFilters: {}", searchFilters);
        Sort sort = searchFilters.getOrder().equalsIgnoreCase("asc") ?
                Sort.by(searchFilters.getOrderBy()).ascending() :
                Sort.by(searchFilters.getOrderBy()).descending();
        Specification<Item> specification = Specification.allOf(
            containsName(searchFilters.getSearchKey().trim())
                .and(hasSlug(searchFilters.getSlug()))
                .and(isWholesale(searchFilters.getStoreId(),loggedUser.getUserType()))
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


    public String createItemsExcelSheet(ItemSearchFields searchFilters,String wholesaleSlug,User loggedUser) throws IOException {
        logger.info("Entering createItemsExcelSheet with searchFilters: {}", searchFilters);
        Specification<Item> specification = Specification.allOf(
                containsName(searchFilters.getSearchKey().trim())
                        .and(isWholesale(searchFilters.getStoreId(),loggedUser.getUserType()))
                        .and(isStatus(searchFilters.getStatus()))
                        .and(inStock(searchFilters.getInStock()))
                        .and(greaterThanOrEqualFromDate(searchFilters.getFromDate()))
                        .and(lessThanOrEqualToToDate(searchFilters.getToDate()))
        );
        List<Item> itemsList = itemRepository.findAll(specification);
        Map<String,List<Object>> result = new HashMap<>();
        for (Item item : itemsList){
            Gson itemsGson = new GsonBuilder().serializeNulls().create();
            String items = itemsGson.toJson(item);
            Map<String,Object> itemMap = new Gson().fromJson(items,Map.class);
            itemMap.forEach((key,value)->{
                if(key.equals("wholesale")){
                    // skip...
                }
                else if (result.containsKey(key.toUpperCase())){
                    result.get(key.toUpperCase()).add(itemMap.get(key));
                }else {
                    List<Object> valueList = new ArrayList<>();
                    valueList.add(value);
                    result.put(key.toUpperCase(),valueList);
                }
            });
        }
        int totalItem = itemsList.size();
        String [] headers = {"NAME","TOKEN","PRICE", "DISCOUNT","LABEL","CAPACITY","RATING","IN-STOCK","STATUS","CREATED-AT","UPDATED-AT"};
        String folderName  = wholesaleSlug;
        // When we're creating all items, excel without a specific user wholesale or store from admin pannel
        if(folderName == null) folderName = loggedUser.getSlug();
        String filePath = writeExcel.createExcelSheet(result, totalItem, Arrays.asList(headers), folderName);
        logger.info("Exiting createItemsExcelSheet");
        return filePath;
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
        // if there is any required field null, then this will throw IllegalArgumentException
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
        ItemCategory itemCategory = itemCategoryRepository.findById(itemDto.getCategoryId()).orElseThrow(() -> new NotFoundException("Item category not found."));
        ItemSubCategory itemSubCategory = itemSubCategoryRepository.findById(itemDto.getSubCategoryId()).orElseThrow(() -> new NotFoundException("Item subcategory not found."));
        itemDto.setItemCategory(itemCategory);
        itemDto.setItemSubCategory(itemSubCategory);

        Map<String, Object> responseObj = new HashMap<>();

        // Going to update item
        if (!Utils.isEmpty(itemDto.getSlug()) || path.contains("update")) {
            logger.info("We are going to update the item.");
            // if there is any required field null, then this will throw IllegalArgumentException
            Utils.checkRequiredFields(itemDto,List.of("slug"));

            int isUpdated = updateItem(itemDto, loggedUser);
            // updating item images
            updateStoreImage(itemDto.getPreviousItemImages(),itemDto.getNewItemImages(),itemDto.getSlug(),"update");
            if (isUpdated > 0) {
                responseObj.put(ConstantResponseKeys.MESSAGE, "successfully updated.");
                responseObj.put(ConstantResponseKeys.STATUS, 200);
            } else {
                responseObj.put(ConstantResponseKeys.MESSAGE, "No item found to update.");
                responseObj.put(ConstantResponseKeys.STATUS, 404);
            }
        } else { // Going to create item
            logger.info("We are going to create the item.");
            // if there is any required field null, then this will throw IllegalArgumentException
            validateRequiredFieldsBeforeCreateItem(itemDto);
            Item createdItem = createItem(itemDto, loggedUser);
            responseObj.put("res", createdItem);
            responseObj.put(ConstantResponseKeys.MESSAGE, "Successfully inserted.");
            responseObj.put(ConstantResponseKeys.STATUS, 201);
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



    public Map<String,Object> getItemDetail(List<String> nameList,
        List<String> labelList,
        List<String> slugList,
        List<String> capacityList,
        List<String> priceList,
        List<String> discountList,
        List<String> inStockList,
        int index) {
        Map<String,Object> itemDetailMap = new HashMap<>();
        itemDetailMap.put("NAME",nameList.get(index));
        itemDetailMap.put("LABEL",labelList.get(index));
        itemDetailMap.put("TOKEN",slugList.get(index));
        itemDetailMap.put("CAPACITY",capacityList.get(index));
        itemDetailMap.put("PRICE",priceList.get(index));
        itemDetailMap.put("DISCOUNT",discountList.get(index));
        itemDetailMap.put("IN-STOCK",inStockList.get(index));
        return itemDetailMap;
    }




    @Transactional(rollbackOn = {MyException.class})
    public List<ItemHbRepository.ItemUpdateError> updateItemsWithExcel(Map<String,List<String>> itemsData, Integer userId, Integer wholesaleId){
        logger.info("Updating items using excel sheet : {} and userId : {} and wholesaleId : {}",itemsData,userId,wholesaleId);
            List<String> prefix = List.of("N","O","Y"); // N=New or No | Y = Yes | O=Old
            ItemHbRepository.ItemUpdateError itemUpdateError = new ItemHbRepository.ItemUpdateError();
            List<ItemHbRepository.ItemUpdateError> errorsList = new ArrayList<>();
            List<String> nameList = itemsData.get("NAME") , labelList = itemsData.get("LABEL"),slugList = itemsData.get("TOKEN"),
                    capacityList = itemsData.get("CAPACITY"),priceList = itemsData.get("PRICE"),discountList = itemsData.get("DISCOUNT")
                    ,inStockList = itemsData.get("IN-STOCK");

            for (int i = 0; i < nameList.size(); i++) {
                Map<String,Object> itemStringDetail = null;
                try {
                    itemStringDetail = getItemDetail(nameList,labelList,slugList,capacityList,priceList,discountList,inStockList,i);
                    if(nameList.get(i).trim().isEmpty()) continue; // if there is no item name, leave that row.
                    String name = Utils.isValidName(nameList.get(i),"item");
                    String label = labelList.get(i);
                    String inStock = inStockList.get(i);
                    if(!Utils.isEmpty(label)) label = String.valueOf(labelList.get(i).charAt(0)).toUpperCase();
                    if(!Utils.isEmpty(inStock)) inStock = String.valueOf(inStockList.get(i).charAt(0)).toUpperCase();
                    if(!prefix.contains(label)) throw new MyException("Label must be New or Old.");
                    if(!prefix.contains(inStock)) throw new MyException("Stock must be Yes or NO.");
                    Float capacity = capacityList.get(i).isEmpty() ? 0f : Float.parseFloat(capacityList.get(i));
                    Float discount = discountList.get(i).isEmpty() ? 0f : Float.parseFloat(discountList.get(i));
                    Float price = priceList.get(i).isEmpty() ? 0f : Float.parseFloat(priceList.get(i));
                    if (price < discount) throw new MyException("Price can't be less then discount.");

                    // creating itemDto object for update action
                    ItemDto itemDto = new ItemDto();
                    itemDto.setName(name);
                    itemDto.setLabel(label);
                    itemDto.setInStock(inStock);
                    itemDto.setCapacity(capacity);
                    itemDto.setPrice(price);
                    itemDto.setDiscount(discount);
                    itemDto.setSlug(slugList.get(i));

                    int updated = itemHbRepository.updateExcelSheetItems(itemDto,userId,wholesaleId);
                    if(updated < 1){
                        itemUpdateError.setItemRowDetail(itemStringDetail);
                        itemUpdateError.setErrorMessage("Item not found.");
                        errorsList.add(itemUpdateError);
                    }

                } catch (MyException | IllegalArgumentException e) {
                    itemUpdateError.setItemRowDetail(itemStringDetail);
                    itemUpdateError.setErrorMessage(e.getMessage());
                    errorsList.add(itemUpdateError);
                }
            }
        logger.info("Exiting updateItemsWithExcel with result: {}", errorsList);
        return errorsList;
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
                    String dirPath = itemImagePath+slug+GlobalConstant.PATH_SEPARATOR;
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
        ItemCategory result = itemCategoryRepository.findById(categoryId).orElseThrow(()-> new NotFoundException("Item category not found."));
        logger.info("Exiting getItemCategoryById with result: {}", result);
        return result;
    }

    public int deleteItemCategory(DeleteDto deleteDto,User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering deleteItemCategory with deleteDto: {}, loggedUser: {}", deleteDto, loggedUser);
        // Validating required fields if they are null, this will throw an Exception
        Utils.checkRequiredFields(deleteDto,List.of("slug"));
        String slug = deleteDto.getSlug();
        // only super admin can delete it subcategory
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("Only super admin can delete item's category.",new Exception());
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
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("Only super admin can delete subcategory.",new Exception());
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
