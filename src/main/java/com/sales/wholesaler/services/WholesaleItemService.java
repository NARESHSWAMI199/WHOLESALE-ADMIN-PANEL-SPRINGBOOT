package com.sales.wholesaler.services;


import com.sales.dto.GraphDto;
import com.sales.dto.ItemDto;
import com.sales.dto.SearchFilters;
import com.sales.entities.Item;
import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
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


    public Page<Item> getAllItems(SearchFilters searchFilters,Integer storeId) {
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
    public Map<String, Object> createOrUpdateItem(ItemDto itemDto, User loggedUser) throws Exception {
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
        if (!Utils.isEmpty(itemDto.getSlug())) {
            Item item = findItemBySLug(itemDto.getSlug());
            if (item.getStatus().equals("D")) throw new MyException("You can't update a blocked item.");;
            String itemImage = saveItemImage(itemDto.getItemImage(), itemDto.getSlug());
            if(itemImage != null){
                itemDto.setAvtar(itemImage);
            }else{
                itemDto.setAvtar(item.getAvtar());
            }
            int isUpdated = updateItem(itemDto, loggedUser);

            if (isUpdated > 0) {
                responseObj.put("message", "successfully updated.");
                responseObj.put("status", 201);
            } else {
                responseObj.put("message", "nothing to updated. may be something went wrong");
                responseObj.put("status", 400);
            }
            return responseObj;
        } else {
            Item createdItem = createItem(itemDto, loggedUser);
            if (createdItem.getId() > 0) {
                responseObj.put("res", createdItem);
                responseObj.put("message", "successfully inserted.");
                responseObj.put("status", 200);
            } else {
                responseObj.put("message", "nothing to insert. may be something went wrong");
                responseObj.put("status", 400);
            }
        }
        return responseObj;
    }

    @Transactional
    public Item createItem (ItemDto itemDto, User loggedUser) throws IOException {
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
        item.setItemCategory(itemDto.getItemCategory());
        item.setItemSubCategory(itemDto.getItemSubCategory());
        item.setSlug(UUID.randomUUID().toString());
        String itemImage = saveItemImage(itemDto.getItemImage(), item.getSlug());
        if(itemImage != null){
            item.setAvtar(itemImage);
        }else{
            item.setAvtar(itemDto.getAvtar());
        }
        return wholesaleItemRepository.save(item);
    }

    @Transactional
    public String saveItemImage(MultipartFile profileImage, String slug) throws MyException, IOException {
        if(profileImage !=null) {
            String fileOriginalName = profileImage.getOriginalFilename().replaceAll(" ", "_");
            if (!Utils.isValidImage(fileOriginalName)) throw new MyException("Not a valid file.");
            String dirPath = itemImagePath+slug+"/";
            File dir = new File(dirPath);
            if(!dir.exists()) dir.mkdirs();
            profileImage.transferTo(new File(dirPath+fileOriginalName));
            return fileOriginalName;
        }
        return null;
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
