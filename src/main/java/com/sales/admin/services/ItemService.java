package com.sales.admin.services;


import com.google.gson.Gson;
import com.sales.dto.*;
import com.sales.entities.*;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.utils.UploadImageValidator;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${item.absolute}")
    String itemImagePath;

    @Value("${item.relative}")
    String itemImageRelativePath;


    public Page<Item> getAllItems(ItemSearchFields searchFilters) {
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
        return itemRepository.findAll(specification,pageable);
    }


    public Map<String, List> createItemsExcelSheet(SearchFilters searchFilters) throws IOException {
        int wholesaleId = searchFilters.getStoreId();
        Long fromDate = searchFilters.getFromDate();
        Long toDate = searchFilters.getToDate();
        List<Item> itemsList =  itemRepository.getAllItemsWithFilters(wholesaleId,fromDate,toDate);
        Map<String,List> result = new HashMap<>();
        for (Item item : itemsList){
            String items = new Gson().toJson(item);
            Map<String,Object> itemMap = new Gson().fromJson(items,Map.class);
            itemMap.forEach((key,value)->{
                if(key.equals("wholesale")){
                    /** skip... */
                }
                else if (result.containsKey(key.toUpperCase())){
                    ((List)result.get(key.toUpperCase())).add(itemMap.get(key));
                }else {
                    List valueList = new ArrayList<>();
                    valueList.add(value);
                    result.put(key.toUpperCase(),valueList);
                }
            });
        }
        int totalItem = itemsList.size();
        String [] headers = {"SLUG","NAME","LABEL", "DESCRIPTION", "PRICE", "DISCOUNT", "RATING","INSTOCK","STATUS","CREATEDAT","UPDATEDAT"};
        writeExcel.writeExcel(result,totalItem,Arrays.asList(headers));
        return  result;
    }

    public Map<String, Integer> getItemCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",itemRepository.totalItemCount());
        responseObj.put("active",itemRepository.optionItemCount("A"));
        responseObj.put("deactive",itemRepository.optionItemCount("D"));
        return responseObj;
    }


    public Item findItemBySLug(String slug) {
        return itemRepository.findItemBySlug(slug);
    }



    public void validateRequiredFields(ItemDto itemDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(itemDto,List.of(
                "name",
                "price",
                "discount",
                "description",
                "capacity",
                "categoryId",
                "avtars",
                "subCategoryId"
        ));
    }

    public void validateRequiredFieldsBeforeCreateItem(ItemDto itemDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        /** @Note : During creation we are checking only extra required params  */
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(itemDto,List.of(
                "wholesaleSlug",
                "rating",
                "inStock",
                "label",
                "newItemImages"
        ));
    }

    public Map<String, Object> createOrUpdateItem(ItemDto itemDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException {
        // if there is any required field null then this will throw IllegalArgumentException
        validateRequiredFields(itemDto);

        if(itemDto.getPrice() < itemDto.getDiscount()) throw new IllegalArgumentException("Discount can't be greater then price.");
        Map<String, Object> responseObj = new HashMap<>();

        // Verify item name syntax
        String itemName = Utils.isValidName( itemDto.getName(),"item");
        itemDto.setName(itemName);

        // retrieve category and subcategory
        ItemCategory itemCategory = itemCategoryRepository.findById(itemDto.getCategoryId()).get();
        ItemSubCategory itemSubCategory = itemSubCategoryRepository.findById(itemDto.getSubCategoryId()).get();
        itemDto.setItemCategory(itemCategory);
        itemDto.setItemSubCategory(itemSubCategory);

        // Going to update item
        if (!Utils.isEmpty(itemDto.getSlug())) {
            int isUpdated = updateItem(itemDto, loggedUser);
            // updating item images
            updateStoreImage(itemDto.getPreviousItemImages(),itemDto.getNewItemImages(),itemDto.getSlug(),"update");
            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 201);
            } else {
                responseObj.put("message", "No item found to update.");
                responseObj.put("status", 404);
            }
            return responseObj;
        } else { // Going to create item
            // if there is any required field null then this will throw IllegalArgumentException
            validateRequiredFieldsBeforeCreateItem(itemDto);
            Item createdItem = createItem(itemDto, loggedUser);
            responseObj.put("res", createdItem);
            responseObj.put("message", "successfully inserted.");
            responseObj.put("status", 200);
            return responseObj;
        }

    }


    public Item createItem (ItemDto itemDto, User loggedUser) throws IOException {
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
        return itemRepository.save(item);
    }



    public int updateItem(ItemDto itemDto, User loggedUser) {
        Item item = findItemBySLug(itemDto.getSlug());
        String title = "Item " + item.getName() + " updated.";
        String messageBody = "Item " + item.getName() + " key : " + item.getSlug() + " updated by admin previous data was "+
                item.toString()
                +". If you have any issue please contact to administrator.";
        sendNotification(title,messageBody,item.getWholesaleId(),loggedUser);
        return itemHbRepository.updateItems(itemDto,loggedUser);
    }


    @Transactional
    public void sendNotification(String title,String messageBody,int storeId,User loggedUser){
        StoreNotifications storeNotifications = new StoreNotifications();
        storeNotifications.setTitle(title);
        storeNotifications.setMessageBody(messageBody);
        storeNotifications.setWholesaleId(storeId);
        storeNotifications.setCreatedBy(loggedUser);
        storeHbRepository.insertStoreNotifications(storeNotifications);
    }

    @Transactional
    public int deleteItem(String slug,User loggedUser) {
        Item item = findItemBySLug(slug);
        String title = "Item " + item.getName() + " deleted.";
        String messageBody = "Item " + item.getName() + " key : " + item.getSlug() + " deleted by admin. If you have any issue please contact to administrator.";
        sendNotification(title,messageBody,item.getWholesaleId(),loggedUser);
        return itemHbRepository.deleteItem(slug);
    }


    public int updateStock(String stock, String slug) {
        return itemHbRepository.updateStock(stock,slug);
    }

    public int updateStatusBySlug(StatusDto statusDto,User loggedUser){
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
        return itemHbRepository.updateStatus(statusDto.getSlug(),statusDto.getStatus());
    }

    public int insertAllItems (Map excel,Integer userId, Integer wholesaleId){
        userId = userId == null ? 0 : userId;
        wholesaleId = wholesaleId == null ? 0 : wholesaleId;
        return  itemHbRepository.insertItemsList(excel,userId,wholesaleId);
    }



    public String updateStoreImage(String previousImages, List<MultipartFile> itemImages,String slug,String action) throws IOException {
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
            /** because it's contains ',' at the end */
            updatedImages = previousImages.substring(0,previousImages.length()-1);
        }
        if(!Utils.isEmpty(updatedImages) && action.equalsIgnoreCase("update")){
            itemHbRepository.updateItemImage(slug, updatedImages);
        }
        return updatedImages;
    }


    @Transactional
    public String saveItemImageName(MultipartFile itemImage, String slug) throws IOException {
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
                    if (UploadImageValidator.hasWhiteBackground(new File(filePath))) {
                        return fileOriginalName;
                    }else{
                        throw new MyException("Image must have a white background");
                    }
            } else {
                throw new MyException("Image is not fit in accept ratio. please resize you image before upload.");
            }
        }
        throw new MyException("Something went wrong.Please contact to administrator");
    }



    public List<ItemCategory> getAllCategory(SearchFilters searchFilters) {
        Sort sort = searchFilters.getOrder().equals("asc") ?
            Sort.by(searchFilters.getOrderBy()).ascending() :
            Sort.by(searchFilters.getOrderBy()).descending() ;
        return itemCategoryRepository.findAll(sort);
    }



    public ItemCategory getItemCategoryById(int categoryId) {
        return itemCategoryRepository.findById(categoryId).get();
    }

    public int deleteItemCategory(String slug,User user) {
        if(user.getUserType().equals("SA")) {
            int categoryId = itemHbRepository.getItemCategoryIdBySLug(slug);
            itemHbRepository.switchCategoryToOther(categoryId);
           return itemHbRepository.deleteItemCategory(slug);
        }
        return 0;
    }

    public int deleteItemSubCategory(String slug,User user) {
        if(user.getUserType().equals("SA")) {
            int subCategoryId = itemSubCategoryRepository.getItemSubCategoryIdBySlug(slug);
            itemHbRepository.switchSubCategoryToOther(subCategoryId);
            return itemHbRepository.deleteItemSubCategory(slug);
        }
        return 0;
    }


    public List<ItemSubCategory> getAllItemsSubCategories(SearchFilters searchFilters) {
        Sort sort = Sort.by(searchFilters.getOrderBy());
        sort  = searchFilters.getOrder().equals("asc") ? sort.ascending() : sort.descending();
        return itemSubCategoryRepository.getSubCategories(searchFilters.getCategoryId(),sort);
    }


    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    public ItemCategory saveOrUpdateItemCategory(CategoryDto categoryDto){
        ItemCategory itemCategory = new ItemCategory();
        if(categoryDto.getId() != null)
        itemCategory.setId(categoryDto.getId());
        itemCategory.setCategory(categoryDto.getCategory());
        itemCategory.setIcon(categoryDto.getIcon());
        return itemCategoryRepository.save(itemCategory);
    }

    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    public ItemSubCategory saveOrUpdateItemSubCategory(SubCategoryDto subCategoryDto){
        ItemSubCategory itemSubCategory = new ItemSubCategory();
        if(subCategoryDto.getId() != null)
        itemSubCategory.setId(subCategoryDto.getId());
        itemSubCategory.setCategoryId(subCategoryDto.getCategoryId());
        itemSubCategory.setSubcategory(subCategoryDto.getSubcategory());
        itemSubCategory.setIcon(subCategoryDto.getIcon());
        itemSubCategory.setUnit(subCategoryDto.getUnit());
        itemSubCategory.setUpdatedAt(Utils.getCurrentMillis());
        return itemSubCategoryRepository.save(itemSubCategory);
    }


    public List<MeasurementUnit> getAllMeasurementUnit() {
        Sort sort = Sort.by("unit").ascending();
        return measurementUnitRepository.findAll(sort);
    }


}
