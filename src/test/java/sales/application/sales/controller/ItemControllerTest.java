package sales.application.sales.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest extends TestUtil {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testAddItem() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json= """
                {
                  "name": "Mock test item",
                  "wholesaleSlug": "string",
                  "price": 0,
                  "discount": 0,
                  "rating": 0,
                  "description": "string",
                  "slug": "string",
                  "label": "string",
                  "capacity": 0,
                  "avtars": "string",
                  "itemImage": "string",
                  "storeId": 0,
                  "categoryId": 0,
                  "subCategoryId": 0
                }
                """;
        mockMvc.perform(
                post("/admin/auth/add")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers)
        )
                .andExpectAll(
                        status().isOk()
                );

    }




}
