package sales.application.sales.wholesaler.controller;

import com.sales.SalesApplication;
import com.sales.entities.Item;
import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import com.sales.global.GlobalConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class WholesaleItemControllerTest extends TestUtil {

    Logger logger = LoggerFactory.getLogger(WholesaleItemControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    public void loginUserTest() throws Exception {
        token = loginUser(GlobalConstantTest.WHOLESALER);
    }


    @Test
    public void testGetAllItems() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                    {}
                """;

        mockMvc.perform(post("/wholesale/item/all")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.numberOfElements", notNullValue())
        );
    }





    @Test
    public void testAddItem() throws Exception {
        ItemCategory itemCategory = createItemCategory();
        ItemSubCategory itemSubCategory = createItemSubCategory(itemCategory.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        MockMultipartFile file = getImageMultipartFileToUpload("newItemImages");
        List<MockMultipartFile> imageFiles = List.of(file);
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/wholesale/item/add");
        imageFiles.forEach(requestBuilder::file);
        mockMvc.perform(requestBuilder
                        .param("name", "Mock test item updated")
                        .param("price", "100")
                        .param("discount", "10")
                        .param("description", "Mock test.")
                        .param("capacity", "1")
                        .param("categoryId", String.valueOf(itemCategory.getId()))
                        .param("subCategoryId", String.valueOf(itemSubCategory.getId()))
                        .param("inStock", "Y")
                        .param("label", "N")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(201)
                )
                .andDo(print())
                .andReturn();
    }


    @Test
    public void testUpdateItem() throws Exception {
        Item item = createItem();
        logger.info("Item : {} {}",item,item.getWholesaleId());
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION, token);
        MockMultipartFile file = getImageMultipartFileToUpload("newItemImages");
        List<MockMultipartFile> imageFiles = List.of(file);
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/wholesale/item/update");
        imageFiles.forEach(requestBuilder::file);
        mockMvc.perform(requestBuilder
                        .param("slug", item.getSlug())
                        .param("name", "Mock test item updated")
                        .param("price", "20")
                        .param("discount", "10")
                        .param("rating", "0")
                        .param("description", "Mock test.")
                        .param("capacity", "1")
                        .param("categoryId", String.valueOf(item.getItemCategory().getId()))
                        .param("subCategoryId", String.valueOf(item.getItemSubCategory().getId()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }


    @Test
    public void testUpdateStock() throws Exception {
        Item item = createItem();
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);

        String json = """
                {
                "slug" : "{slug}",
                "stock" : "Y"
                }
                """
                .replace("{slug}",item.getSlug());
        mockMvc.perform(post("/admin/item/stock")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }


    @Test
    public void testDeleteItem() throws Exception {
        Item item = createItem();
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                {
                "slug" : "{slug}"
                }
                """
                .replace("{slug}", item.getSlug());
        mockMvc.perform(post("/wholesale/item/delete")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }




    @Test
    public void testGetCategory() throws Exception {
        createItemCategory();
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/item/category")
                        .headers(headers)
                ).andExpectAll(
                        status().isOk()
                )
                .andDo(print())
                .andReturn();

        List categoryList = extractCategoryListFromResponse(result);
        if(!categoryList.isEmpty()) {
            Map<String, Object> categoryDto = (Map<String, Object>) categoryList.get(0);
            Integer categoryId = (Integer) categoryDto.get("id");

            // Getting subcategories also
            headers.set(GlobalConstant.AUTHORIZATION, token);
            mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/item/subcategory/"+categoryId)
                    .headers(headers)
            ).andExpectAll(
                    status().is(200)
            );
        }
    }


}
