package com.sales.wholesaler.services;


import com.sales.dto.DeleteDto;
import com.sales.dto.GraphDto;
import com.sales.dto.ItemDto;
import com.sales.dto.ItemSearchFields;
import com.sales.entities.Item;
import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.exceptions.NotFoundException;
import com.sales.global.GlobalConstant;
import com.sales.utils.UploadImageValidator;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static com.sales.specifications.ItemsSpecifications.*;

@Service
public class WholesaleItemService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleItemService.class);

    @Value("${item.absolute}")
    String itemImagePath;


    public Page<Item> getAllItems(ItemSearchFields searchFilters, Integer storeId) {
        logger.info("Starting getAllItems method with searchFilters: {}, storeId: {}", searchFilters, storeId);
        Sort sort = searchFilters.getOrder().equalsIgnoreCase("asc") ?
                Sort.by(searchFilters.getOrderBy()).ascending() :
                Sort.by(searchFilters.getOrderBy()).descending();
        Specification<Item> specification = Specification.where(
                (containsName(searchFilters.getSearchKey().trim())
                        .or(hasSlug(searchFilters.getSearchKey())))
                        .and(isWholesale(storeId))
                        .and(isStatus(searchFilters.getStatus()))
                        .and(inStock(searchFilters.getInStock()))
                        .and(isLabel(searchFilters.getLabel()))
                        .and(greaterThanOrEqualFromDate(searchFilters.getFromDate()))
                        .and(lessThanOrEqualToToDate(searchFilters.getToDate()))
        );
        Pageable pageable = PageRequest.of(searchFilters.getPageNumber(), searchFilters.getSize(), sort);
        Page<Item> items = wholesaleItemRepository.findAll(specification, pageable);
        logger.info("Completed getAllItems method");
        return items;
    }




    public Map<String, Integer> getItemCounts (Integer storeId) {
        logger.info("Starting getItemCounts method with storeId: {}", storeId);
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.totalItemCount(storeId));
        responseObj.put("inStock",wholesaleItemRepository.getItemCountInStock("Y",storeId));
        responseObj.put("outStock",wholesaleItemRepository.getItemCountInStock("N",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCount("A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCount("D",storeId));
        logger.info("Completed getItemCounts method");
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForNewLabel (Integer storeId) {
        logger.info("Starting getItemCountsForNewLabel method with storeId: {}", storeId);
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountLabel("N",storeId));
        responseObj.put("inStock",wholesaleItemRepository.getItemCountInStock("Y","N",storeId));
        responseObj.put("outStock",wholesaleItemRepository.getItemCountInStock("N","N",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("N","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("N","D",storeId));
        logger.info("Completed getItemCountsForNewLabel method");
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForOldLabel (Integer storeId) {
        logger.info("Starting getItemCountsForOldLabel method with storeId: {}", storeId);
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountLabel("O",storeId));
        responseObj.put("inStock",wholesaleItemRepository.getItemCountInStock("Y","O",storeId));
        responseObj.put("outStock",wholesaleItemRepository.getItemCountInStock("N","O",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("O","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("O","D",storeId));
        logger.info("Completed getItemCountsForOldLabel method");
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForInStock (Integer storeId) {
        logger.info("Starting getItemCountsForInStock method with storeId: {}", storeId);
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountInStock("Y",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountInStock("Y","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountInStock("Y","D",storeId));
        logger.info("Completed getItemCountsForInStock method");
        return responseObj;
    }


    public Map<String, Integer> getItemCountsForOutStock (Integer storeId) {
        logger.info("Starting getItemCountsForOutStock method with storeId: {}", storeId);
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountInStock("N",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("N","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("N","D",storeId));
        logger.info("Completed getItemCountsForOutStock method");
        return responseObj;
    }



    public Item findItemBySLug(String slug) {
        logger.info("Starting findItemBySLug method with slug: {}", slug);
        Item item = wholesaleItemRepository.findItemBySlug(slug);
        logger.info("Completed findItemBySLug method");
        return item;
    }


    public String getItemStatus(String slug) {
        logger.info("Starting getItemStatus method with slug: {}", slug);
        String status = wholesaleItemRepository.getItemStatus(slug);
        logger.info("Completed getItemStatus method");
        return status;
    }

    public void validateRequiredFields(ItemDto itemDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting validateRequiredFields method with itemDto: {}", itemDto);
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(itemDto,List.of(
                "name",
                "price",
                "discount",
                "description",
//                "capacity",
                "categoryId",
                "subCategoryId"
        ));
        logger.info("Completed validateRequiredFields method");
    }

    public void validateRequiredFieldsBeforeCreateItem(ItemDto itemDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting validateRequiredFieldsBeforeCreateItem method with itemDto: {}", itemDto);
        /** @Note : During creation we are checking only extra required params  */
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(itemDto,List.of(
                "rating",
                "inStock",
                "label",
                "newItemImages"
        ));
        logger.info("Completed validateRequiredFieldsBeforeCreateItem method");
    }



    @Transactional(rollbackOn = {IllegalArgumentException.class,MyException.class, RuntimeException.class,Exception.class})
    public Map<String, Object> createOrUpdateItem(ItemDto itemDto, User loggedUser,String path) throws Exception {
        logger.info("Starting createOrUpdateItem method with itemDto: {}, loggedUser: {}, path: {}", itemDto, loggedUser, path);
        // if there is any required field null then this will throw IllegalArgumentException
        validateRequiredFields(itemDto);

        // Discount can't be less than item's price
        if(itemDto.getPrice() < itemDto.getDiscount()) throw new IllegalArgumentException("Discount can't be greater then price.");
        // If item name not in proper syntax this will throw Exception
        String itemName = Utils.isValidName( itemDto.getName(),"item");
        itemDto.setName(itemName);

        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(loggedUser.getId());
        itemDto.setStoreId(storeId);

        // Getting category and subcategory from database behalf on provided Ids.
        ItemCategory itemCategory = wholesaleItemCategoryRepository.findById(itemDto.getCategoryId()).get();
        if(itemCategory == null) throw new IllegalArgumentException("Invalid categoryId.");
        itemDto.setItemCategory(itemCategory);
        ItemSubCategory itemSubCategory = wholesaleItemSubCategoryRepository.findById(itemDto.getSubCategoryId()).get();
        if(itemSubCategory == null) throw new IllegalArgumentException("Invalid subCategoryId.");
        itemDto.setItemSubCategory(itemSubCategory);

        Map<String, Object> responseObj = new HashMap<>();

        // Going to update Item
        if (!Utils.isEmpty(itemDto.getSlug()) || path.contains("update")) {

            // if there is any required field null then this will throw IllegalArgumentException
            Utils.checkRequiredFields(itemDto,List.of("slug"));

            // Getting item's status from database and validating the item not blocked
            String itemStatus = getItemStatus(itemDto.getSlug());
            if(itemStatus == null) throw new NotFoundException("No item found to update.");
            if (itemStatus.equals("D")) throw new MyException("You can't update a blocked item.");

            // Update item images
            updateStoreImage(itemDto.getPreviousItemImages(),itemDto.getNewItemImages(), itemDto.getSlug(),"update");
            int isUpdated = updateItem(itemDto, loggedUser); // Update operation
            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 200);
            } else {
                responseObj.put("message", "No item found to update.");
                responseObj.put("status", 404);
            }
        } else {  // Going to crate Item
            // if there is any required field null then this will throw IllegalArgumentException
            validateRequiredFieldsBeforeCreateItem(itemDto);

            Item createdItem = createItem(itemDto, loggedUser); // Create operation
            responseObj.put("res", createdItem);
            responseObj.put("message", "Successfully inserted.");
            responseObj.put("status", 201);
        }
        logger.info("Completed createOrUpdateItem method");
        return responseObj;
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class,MyException.class, RuntimeException.class,Exception.class})
    public Item createItem (ItemDto itemDto, User loggedUser) throws MyException, IOException {
        logger.info("Starting createItem method with itemDto: {}, loggedUser: {}", itemDto, loggedUser);
        String slug = UUID.randomUUID().toString();
        Item item = new Item();
        item.setWholesaleId(itemDto.getStoreId());
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
        item.setItemCategory(itemDto.getItemCategory());
        item.setItemSubCategory(itemDto.getItemSubCategory());
        item.setSlug(slug);
        item.setAvtars(updateStoreImage("",itemDto.getNewItemImages(),slug,"create"));
        Item savedItem = wholesaleItemRepository.save(item); // Create operation
        logger.info("Completed createItem method");
        return savedItem;
    }

    public String updateStoreImage(String previousImages, List<MultipartFile> itemImages,String slug,String action) throws IOException {
        logger.info("Starting updateStoreImage method with previousImages: {}, itemImages: {}, slug: {}, action: {}", previousImages, itemImages, slug, action);
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
            wholesaleItemHbRepository.updateItemImages(slug, updatedImages); // Update operation
        }
        logger.info("Completed updateStoreImage method");
        return updatedImages;
    }


    @Transactional
    public String saveItemImageName(MultipartFile itemImage, String slug) throws IOException {
        logger.info("Starting saveItemImageName method with itemImage: {}, slug: {}", itemImage, slug);
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
                logger.info("Completed saveItemImageName method");
                return fileOriginalName;
            } else {
                throw new MyException("Image is not fit in accept ratio. please resize you image before upload.");
            }
        }
        throw new MyException("Something went wrong.Please contact to administrator");
    }




    public int updateItem(ItemDto itemDto, User loggedUser) {
        logger.info("Starting updateItem method with itemDto: {}, loggedUser: {}", itemDto, loggedUser);
        int updateCount = wholesaleItemHbRepository.updateItems(itemDto,loggedUser); // Update operation
        logger.info("Completed updateItem method");
        return updateCount;
    }

    public int deleteItem(DeleteDto deleteDto, Integer storeId) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting deleteItem method with deleteDto: {}, storeId: {}", deleteDto, storeId);
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(deleteDto,List.of("slug"));
        String slug = deleteDto.getSlug();
        String status = getItemStatus(slug);
        if(status == null) throw new NotFoundException("No item to delete.");
        if (status.equals("D")) throw new IllegalArgumentException("Can't deactivated items.");
        int deleteCount = wholesaleItemHbRepository.deleteItem(slug,storeId); // Update operation
        logger.info("Completed deleteItem method");
        return deleteCount;
    }

    public int updateStock(Map<String,String> params,Integer storeId) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting updateStock method with params: {}, storeId: {}", params, storeId);
        // if there is any required field null then this will throw IllegalArgumentException
        Utils.checkRequiredFields(params, List.of("slug", "stock"));
        String slug = params.get("slug");
        String stock = params.get("stock");
        int updateCount = wholesaleItemHbRepository.updateStock(stock,slug,storeId); // Update operation
        logger.info("Completed updateStock method");
        return updateCount;
    }



    public Map<String,Object> getItemCountByMonths(GraphDto graphDto,Integer storeId){
        logger.info("Starting getItemCountByMonths method with graphDto: {}, storeId: {}", graphDto, storeId);
        List<Integer> months = graphDto.getMonths();
        months = (months == null || months.isEmpty()) ?
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12) : months;
        Integer year = graphDto.getYear();
        Map<String,Object> monthsObj= new LinkedHashMap<>();
        for(Integer month : months) {
            monthsObj.put(getMonthName(month),wholesaleItemRepository.totalItemsViaMonth(month,year,storeId));
        }
        logger.info("Completed getItemCountByMonths method");
        return monthsObj;
    }


    public String getMonthName(int month) {
        logger.info("Starting getMonthName method with month: {}", month);
        if (month <= 0 || month > 12) {
            return null;
        }
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, new Locale("eng"));
        logger.info("Completed getMonthName method");
        return monthName;
    }

    public List<ItemCategory> getAllCategory() {
        logger.info("Starting getAllCategory method");
        Sort sort = Sort.by("category").ascending();
        List<ItemCategory> categories = wholesaleItemCategoryRepository.findAll(sort);
        logger.info("Completed getAllCategory method");
        return categories;
    }


    public List<ItemSubCategory> getAllItemsSubCategories(int categoryId) {
        logger.info("Starting getAllItemsSubCategories method with categoryId: {}", categoryId);
        List<ItemSubCategory> subCategories = wholesaleItemSubCategoryRepository.getSubCategories(categoryId);
        logger.info("Completed getAllItemsSubCategories method");
        return subCategories;
    }

}
