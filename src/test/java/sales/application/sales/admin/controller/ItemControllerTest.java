package sales.application.sales.admin.controller;


import com.sales.SalesApplication;
import com.sales.global.ConstantResponseKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes=SalesApplication.class)
@AutoConfigureMockMvc
public class ItemControllerTest extends TestUtil {


    /** @Important : Here we created new functions for each Test, but all called in one function
     * If you want test to separately use @Test annotation and provide you custom slug.
     * */
    @Autowired
    MockMvc mockMvc;



    @Test
    public void testGetAllItems() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                    {}
                """;

        mockMvc.perform(post("/admin/item/all")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.numberOfElements", notNullValue())
        )


        ;
    }




    @Test
    public void testAddItem() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        MockMultipartFile file = getImageMultipartFileToUpload("newItemImages");
        List<MockMultipartFile> imageFiles = List.of(file);
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/admin/item/add");
        imageFiles.forEach(requestBuilder::file);
        MvcResult result = mockMvc.perform(requestBuilder
                        .param("name", "Mock test item updated")
                        .param("wholesaleSlug", "842dab10-ee29-45b6-9240-ad860c17548b")
                        .param("price", "100")
                        .param("discount", "10")
                        .param("rating", "0")
                        .param("description", "Mock test.")
                        .param("capacity", "1") // optional
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

        // update status
        testUpdateStatus(slug);
        // Delete
        testDeleteItem(slug);

    }


    public void testUpdateItem(String slug) throws Exception {
        Map<String, String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        MockMultipartFile file = getImageMultipartFileToUpload("newItemImages");
        List<MockMultipartFile> imageFiles = List.of(file);
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/admin/item/update");
        imageFiles.forEach(requestBuilder::file);
        mockMvc.perform(requestBuilder
                        .param("slug", slug)
                        .param("name", "Mock test item")
                        .param("wholesaleSlug", "842dab10-ee29-45b6-9240-ad860c17548b")
                        .param("price", "20")
                        .param("discount", "10")
                        .param("rating", "0")
                        .param("description", "Mock test.")
                        .param("capacity", "1")
                        .param("categoryId", "0")
                        .param("subCategoryId", "0")
                        .param("price", "20")
                        .param("inStock", "Y")
                        .param("label", "N")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }


    public void testUpdateStock(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);

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



    public void testUpdateStatus(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
//        String slug = "c626ff4b-0118-47d9-964a-a8b7f6ac8569";

        String json = """
                {
                "slug" : "{slug}",
                "status" : "A"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/item/status")
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
    public void testDeleteItemWithWrongSlug() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        String slug = "sdfsdfs";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/item/delete")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(404)
                )
                .andDo(print());
    }



    public void testDeleteItem(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/item/delete")
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
    public void testGetSubcategory() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                  {
                  }
                """;
        mockMvc.perform(post("/admin/item/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(406)
        );
    }


    @Test
    public void testGetCategory() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                  {
                  }
                """;
        MvcResult result = mockMvc.perform(post("/admin/item/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
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
            headers.set("Authorization", token);
            String subCategoryJson = """
                  {
                    "categoryId" : {categoryId}
                  }
                    """
                    .replace("{categoryId}", categoryId + "");
            mockMvc.perform(post("/admin/item/subcategory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(subCategoryJson)
                    .headers(headers)
            ).andExpectAll(
                    status().is(200)
            );
        }
    }

    @Test
    public void testCategoryAdd() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                  {
                  "category" : "Mock Test Category {random}",
                  "icon": "Mock test icon"
                  }
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6)); // random added due to duplicate category issue.
        mockMvc.perform(post("/admin/item/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(201)
        ).andDo(print());
    }


    @Test
    public void testCategoryUpdate() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                  {
                  "id" : 17,
                  "category" : "Mock update Test Category {random}",
                  "icon": "Mock test icon"
                  }
                """.replace("{random}", UUID.randomUUID().toString().substring(0,6)); // randomly added due to duplicate category issue.
        MvcResult result = mockMvc.perform(post("/admin/item/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                ).andExpectAll(
                        status().is(200)
                ).andDo(print())
                .andReturn();
        String slug = extractSlugFromResponseViaRes(result);

        // delete category via staff account
        testDeleteCategoryViaStaff(slug);
        // delete category via super admin account
        testDeleteCategoryViaSuperAdmin(slug);
    }


    @Test
    public void testSubCategoryAdd() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                  {
                    "categoryId" : 0,
                    "subcategory" : "Mock test subcategory {random}",
                    "unit" : "kg",
                    "icon" : "test"
                  }
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6)); // random added due to duplicate category issue.
        mockMvc.perform(post("/admin/item/subcategory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(201)
        ).andDo(print());
    }



    @Test
    public void testSubCategoryUpdate() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                  {
                    "id" : 31,
                    "categoryId" : 0,
                    "subcategory" : "Mock test subcategory {random}",
                    "unit" : "liter",
                    "icon" : "test"
                  }
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6)); // randomly added due to duplicate category issue.
        MvcResult result = mockMvc.perform(post("/admin/item/subcategory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                ).andExpectAll(
                        status().is(200)
                ).andDo(print())
                .andReturn();

        String slug = extractSlugFromResponseViaRes(result);
        // delete subcategory via staff account
        testDeleteSubcategoryViaStaff(slug);

        // delete subcategory via super admin account
        testDeleteSubcategoryViaSuperAdmin(slug);

    }



    public void testDeleteCategoryViaStaff(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/item/category/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(403), // Only super admin can delete item's category
                jsonPath("$.message",is("Only super admin can delete item's category."))
        );
    }


    public void testDeleteCategoryViaSuperAdmin(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }`
                """
                .replace("{slug}",slug); // use @Test and use valid slug for separate test
        mockMvc.perform(post("/admin/item/category/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        );
    }

    public void testDeleteSubcategoryViaStaff(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }`
                """
                .replace("{slug}",slug); // use @Test and use valid slug for separate test
        mockMvc.perform(post("/admin/item/subcategory/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(403)
        );
    }


    public void testDeleteSubcategoryViaSuperAdmin(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }`
                """
                .replace("{slug}",slug); // use @Test and use valid slug for separate test
        mockMvc.perform(post("/admin/item/subcategory/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        );
    }




}
