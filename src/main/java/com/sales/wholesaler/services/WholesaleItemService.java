package com.sales.wholesaler.services;


import com.sales.dto.GraphDto;
import com.sales.dto.ItemDto;
import com.sales.dto.ItemSearchFields;
import com.sales.entities.Item;
import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import com.sales.entities.User;
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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static com.sales.specifications.ItemsSpecifications.*;

@Service
public class WholesaleItemService extends WholesaleRepoContainer {

    @Value("${item.absolute}")
    String itemImagePath;

    @Value("${item.relative}")
    String itemImageRelativePath;


    public Page<Item> getAllItems(ItemSearchFields searchFilters, Integer storeId) {
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
        return wholesaleItemRepository.findAll(specification,pageable);
    }




    public Map<String, Integer> getItemCounts (Integer storeId) {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.totalItemCount(storeId));
        responseObj.put("inStock",wholesaleItemRepository.getItemCountInStock("Y",storeId));
        responseObj.put("outStock",wholesaleItemRepository.getItemCountInStock("N",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCount("A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCount("D",storeId));
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForNewLabel (Integer storeId) {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountLabel("N",storeId));
        responseObj.put("inStock",wholesaleItemRepository.getItemCountInStock("Y","N",storeId));
        responseObj.put("outStock",wholesaleItemRepository.getItemCountInStock("N","N",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("N","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("N","D",storeId));
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForOldLabel (Integer storeId) {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountLabel("O",storeId));
        responseObj.put("inStock",wholesaleItemRepository.getItemCountInStock("Y","O",storeId));
        responseObj.put("outStock",wholesaleItemRepository.getItemCountInStock("N","O",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("O","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("O","D",storeId));
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForInStock (Integer storeId) {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountInStock("Y",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountInStock("Y","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountInStock("Y","D",storeId));
        return responseObj;
    }


    public Map<String, Integer> getItemCountsForOutStock (Integer storeId) {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountInStock("N",storeId));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("N","A",storeId));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("N","D",storeId));
        return responseObj;
    }



    public Item findItemBySLug(String slug) {
        return wholesaleItemRepository.findItemBySlug(slug);
    }



    @Transactional(rollbackOn = {MyException.class, RuntimeException.class})
    public Map<String, Object> createOrUpdateItem(ItemDto itemDto, User loggedUser,String path) throws Exception {
        if(itemDto.getPrice() < itemDto.getDiscount()) throw new MyException("Discount can't be greater then price.");
        Map<String, Object> responseObj = new HashMap<>();
        String itemName = Utils.isValidName( itemDto.getName(),"item");
        itemDto.setName(itemName);
        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(loggedUser.getId());
        itemDto.setStoreId(storeId);
        ItemCategory itemCategory = wholesaleItemCategoryRepository.findById(itemDto.getCategoryId()).get();
        itemDto.setItemCategory(itemCategory);
        ItemSubCategory itemSubCategory = wholesaleItemSubCategoryRepository.findById(itemDto.getSubCategoryId()).get();
        itemDto.setItemSubCategory(itemSubCategory);

        // Going to update Item
        if (!Utils.isEmpty(itemDto.getSlug()) && path.contains("update")) {
            Item item = findItemBySLug(itemDto.getSlug());
            if (item.getStatus().equals("D")) throw new MyException("You can't update a blocked item.");;
            updateStoreImage(itemDto.getPreviousItemImages(),itemDto.getNewItemImages(), item.getSlug(),"update");
            int isUpdated = updateItem(itemDto, loggedUser);
            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 201);
            } else {
                responseObj.put("message", "No item found to update.");
                responseObj.put("status", 404);
            }
        } else {  // Going to crate Item
            Item createdItem = createItem(itemDto, loggedUser);
            responseObj.put("res", createdItem);
            responseObj.put("message", "Successfully inserted.");
            responseObj.put("status", 200);
        }
        return responseObj;
    }

    @Transactional
    public Item createItem (ItemDto itemDto, User loggedUser) throws MyException, IOException {
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
        return wholesaleItemRepository.save(item);
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
            wholesaleItemHbRepository.updateItemImage(slug, updatedImages);
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
                //if (!UploadImageValidator.hasWhiteBackground(new File(filePath))) throw new MyException("Image must have a white background");
                return fileOriginalName;
            } else {
                throw new MyException("Image is not fit in accept ratio. please resize you image before upload.");
            }
        }
        throw new MyException("Something went wrong.Please contact to administrator");
    }




    public int updateItem(ItemDto itemDto, User loggedUser) {
        return wholesaleItemHbRepository.updateItems(itemDto,loggedUser);
    }

    public int deleteItem(String slug,Integer storeId) {
        Item item = findItemBySLug(slug);
        if (item.getStatus().equals("D")) return 0;
        return wholesaleItemHbRepository.deleteItem(slug,storeId);
    }

    public int updateStock(String stock, String slug,Integer storeId) {
        return wholesaleItemHbRepository.updateStock(stock,slug,storeId);
    }



    public Map<String,Object> getItemCountByMonths(GraphDto graphDto,Integer storeId){
        List<Integer> months = graphDto.getMonths();
        months = (months == null || months.isEmpty()) ?
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12) : months;
        Integer year = graphDto.getYear();
        Map<String,Object> monthsObj= new LinkedHashMap<>();
        for(Integer month : months) {
            monthsObj.put(getMonthName(month),wholesaleItemRepository.totalItemsViaMonth(month,year,storeId));
        }
        return monthsObj;
    }


    public String getMonthName(int month) {
        if (month <= 0 || month > 12) {
            return null;
        }
        return Month.of(month).getDisplayName(TextStyle.FULL, new Locale("eng"));
    }

    public List<ItemCategory> getAllCategory() {
        Sort sort = Sort.by("category").ascending();
        return wholesaleItemCategoryRepository.findAll(sort);
    }


    public List<ItemSubCategory> getAllItemsSubCategories(int categoryId) {
        return wholesaleItemSubCategoryRepository.getSubCategories(categoryId);
    }

}
