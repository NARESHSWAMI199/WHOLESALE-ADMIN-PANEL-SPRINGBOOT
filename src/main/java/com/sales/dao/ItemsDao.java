package com.sales.dao;

import com.sales.dto.ItemDto;
import com.sales.dto.SearchFilters;
import com.sales.entities.Item;
import com.sales.entities.SalesUser;
import org.springframework.data.domain.Page;

public interface ItemsDao {

    Page<Item> getAllItems(SearchFilters searchFilters);

    int updateItem(ItemDto itemDto, SalesUser loggedUser);

    Item createItem(ItemDto itemDto, SalesUser loggedUser) throws Exception;

    Item findItemBySLug(String slug);

    int deleteItem(String slug);

    int updateStock(String stock, String slug);


}
