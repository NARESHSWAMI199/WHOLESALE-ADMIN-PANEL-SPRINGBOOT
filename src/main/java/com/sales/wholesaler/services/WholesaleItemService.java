package com.sales.wholesaler.services;


import com.sales.dto.ItemDto;
import com.sales.dto.SearchFilters;
import com.sales.dto.StatusDto;
import com.sales.entities.Item;
import com.sales.entities.Store;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.sales.specifications.ItemsSpecifications.*;

@Service
public class WholesaleItemService extends WholesaleRepoContainer {



    public Page<Item> getAllItems(SearchFilters searchFilters) {
        Sort sort = searchFilters.getOrder().equalsIgnoreCase("asc") ?
                Sort.by(searchFilters.getOrderBy()).ascending() :
                Sort.by(searchFilters.getOrderBy()).descending();
        Specification<Item> specification = Specification.where(
            containsName(searchFilters.getName())
                .and(isWholesale(searchFilters.getStoreId()))
                .and(isStatus(searchFilters.getStatus()))
                .and(inStock(searchFilters.getInStock()))
                .and(isLabel(searchFilters.getLabel()))
                .and(greaterThanOrEqualFromDate(searchFilters.getFromDate()))
                .and(lessThanOrEqualToToDate(searchFilters.getToDate()))
        );
        Pageable pageable = PageRequest.of(searchFilters.getPageNumber(), searchFilters.getSize(), sort);
        return wholesaleItemRepository.findAll(specification,pageable);
    }



    public Map<String, Integer> getItemCounts () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.totalItemCount());
        responseObj.put("active",wholesaleItemRepository.optionItemCount("A"));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCount("D"));
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForNewLabel () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountLabel("N"));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("N","A"));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("N","D"));
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForOldLabel () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountLabel("O"));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("O","A"));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("O","D"));
        return responseObj;
    }

    public Map<String, Integer> getItemCountsForInStock () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountInStock("Y"));
        responseObj.put("active",wholesaleItemRepository.optionItemCountInStock("Y","A"));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountInStock("Y","D"));
        return responseObj;
    }


    public Map<String, Integer> getItemCountsForOutStock () {
        Map<String,Integer> responseObj = new HashMap<>();
        responseObj.put("all",wholesaleItemRepository.getItemCountInStock("N"));
        responseObj.put("active",wholesaleItemRepository.optionItemCountLabel("N","A"));
        responseObj.put("deactive",wholesaleItemRepository.optionItemCountLabel("N","D"));
        return responseObj;
    }



    public Item findItemBySLug(String slug) {
        return wholesaleItemRepository.findItemBySlug(slug);
    }



    public Map<String, Object> createOrUpdateItem(ItemDto itemDto, User loggedUser) throws Exception {
        Map<String, Object> responseObj = new HashMap<>();
        if (!Utils.isEmpty(itemDto.getSlug())) {
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

    public Item createItem (ItemDto itemDto, User loggedUser) throws Exception {
        Item item = new Item();
        Store store = wholesaleStoreRepository.findStoreBySlug(itemDto.getWholesaleSlug());
        if (store == null) throw new Exception("Not a valid store.");
        item.setWholesale(store);
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
        item.setSlug(UUID.randomUUID().toString());
        return wholesaleItemRepository.save(item);
    }


    public int updateItem(ItemDto itemDto, User loggedUser) {
        return wholesaleItemHbRepository.updateItems(itemDto,loggedUser);
    }

    public int deleteItem(String slug) {
        return wholesaleItemHbRepository.deleteItem(slug);
    }

    public int updateStock(String stock, String slug) {
        return wholesaleItemHbRepository.updateStock(stock,slug);
    }

    public int updateStatusBySlug(StatusDto statusDto){
        return wholesaleItemHbRepository.updateStatus(statusDto.getSlug(),statusDto.getStatus());
    }

}
