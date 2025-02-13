package sales.application.sales.wholesaler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class WholesaleItemControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreate () throws Exception {
        String json = """
                    {
                      "name": "string",
                      "price": 0,
                      "discount": 0,
                      "description": "string",
                      "label": "string",
                      "capacity": 0,
                      "itemImage": "string",
                      "storeId": 0,
                      "categoryId": 0,
                      "subCategoryId": 0,
                      "inStock" : "Y|N",
                       "newItemImages": [
                          "image"
                        ]
                    }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/item/add"))
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                ).andDo(MockMvcResultHandlers.print())
        ;

    }


}
