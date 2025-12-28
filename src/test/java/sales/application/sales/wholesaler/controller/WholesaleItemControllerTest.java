package sales.application.sales.wholesaler.controller;

import com.sales.SalesApplication;
import com.sales.global.GlobalConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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


    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    public void loginUser() throws Exception {
        token = loginUser(GlobalConstantTest.WHOLESALER);
    }


    @Test
    public void testGetAllItems() throws Exception {
        createItem();
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
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        MockMultipartFile file = getImageMultipartFileToUpload("newItemImages");
        List<MockMultipartFile> imageFiles = List.of(file);
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/wholesale/item/add");
        imageFiles.forEach(requestBuilder::file);
        MvcResult result = mockMvc.perform(requestBuilder
                        .param("name", "Mock test item updated")
                        .param("price", "100")
                        .param("discount", "10")
                        .param("description", "Mock test.")
                        .param("capacity", "1")
                        .param("categoryId", "0")
                        .param("subCategoryId", "0")
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

        // Update added
        String slug = extractSlugFromResponseViaRes(result);
        testUpdateItem(slug);

        // update stock
        testUpdateStock(slug);

        // delete added item
        testDeleteItem(slug);
    }


    public void testUpdateItem(String slug) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION, token);
        MockMultipartFile file = getImageMultipartFileToUpload("newItemImages");
        List<MockMultipartFile> imageFiles = List.of(file);
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/wholesale/item/update");
        imageFiles.forEach(requestBuilder::file);
        mockMvc.perform(requestBuilder
                        .param("slug", slug)
                        .param("name", "Mock test item updated")
                        .param("price", "20")
                        .param("discount", "10")
                        .param("rating", "0")
                        .param("description", "Mock test.")
                        .param("capacity", "1")
                        .param("categoryId", "0")
                        .param("subCategoryId", "0")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }


    public void testUpdateStock(String slug) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);

        String json = """
                {
                "slug" : "{slug}",
                "stock" : "Y"
                }
                """
                .replace("{slug}",slug);
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


    public void testDeleteItem(String slug) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                {
                "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
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
