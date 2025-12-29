package sales.application.sales.admin.controller;


import com.google.gson.Gson;
import com.sales.SalesApplication;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class StoreControllerTest extends TestUtil {

    /**@Note : We are not adding any new store in this test, but from userController we can create store and wholesale user */


    private String token;

    @BeforeEach
    public void loginUserTest() throws Exception {
        token = loginUser(GlobalConstantTest.ADMIN);
    }

    @Test
    public  void testAddStoreWithStaffSlug() throws Exception {

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();

        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        String slug = loggedUserResponse.get("slug");
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                {
                    "userSlug" : "{userSlug}",
                    "storeName" : "Mock test store",
                    "storeEmail" : "{storeEmail}",
                    "description" : "test",
                    "categoryId" : "0",
                    "subCategoryId"  : "0",
                    "storePhone" : "{storePhone}",
                    "zipCode" : "302013",
                    "city" : "1",
                    "state" : "1",
                    "street" : "1 Mock test jaipur"
                }
            """
            .replace("{userSlug}",slug)
            .replace("{storeEmail}",randomEmail)
            .replace("{storePhone}",randomPhone);

        Map<String,String> params = new Gson().fromJson(json,Map.class);
        MockMultipartHttpServletRequestBuilder requestBuilder  = multipart("/admin/store/add");
        params.forEach(requestBuilder::param);
        mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is((403)) // userSlug : userType must be wholesaler (W).
        );

    }


    @Test
    public  void testUpdateStore() throws Exception {

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();

        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                {
                    "storeSlug" : "{storeSlug}",
                    "storeName" : "Mock test store",
                    "storeEmail" : "{storeEmail}",
                    "description" : "test",
                    "categoryId" : "0",
                    "subCategoryId"  : "0",
                    "storePhone" : "{storePhone}",
                    "zipCode" : "302013",
                    "city" : "1",
                    "state" : "1",
                    "street" : "1 Mock test jaipur"
                }
            """
                .replace("{storeSlug}",GlobalConstantTest.TEST_STORE_SLUG)
                .replace("{storeEmail}",randomEmail)
                .replace("{storePhone}",randomPhone);

        Map<String,String> params = new Gson().fromJson(json,Map.class);
        MockMultipartHttpServletRequestBuilder requestBuilder  = multipart("/admin/store/update");
        params.forEach(requestBuilder::param);
        mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is((200))
        );

    }



    @Test
    public void testGetAllStores() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();

        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                    {
                    }
                """;

        mockMvc.perform(post("/admin/store/all")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.content", notNullValue())
        );

    }




    @Test
    public void testGetStoreDetail() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();

        headers.set(GlobalConstant.AUTHORIZATION , token);
        mockMvc.perform(get("/admin/store/detail/"+GlobalConstantTest.TEST_STORE_SLUG)
                .headers(headers)
        ).andExpectAll(
                status().isOk()
        ).andDo(print());

    }


    @Test
    public void testGetStoreDetailWithWrongSlug() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();

        headers.set(GlobalConstant.AUTHORIZATION , token);
        mockMvc.perform(get("/admin/store/detail/"+GlobalConstantTest.TEST_STORE_SLUG+"ddsf")
                .headers(headers)
        ).andExpectAll(
                status().is(404)
        ).andDo(print());

    }



    @Test
    public void testGetStoreDetailByUserWithWrongSlug() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();

        headers.set(GlobalConstant.AUTHORIZATION , token);
        mockMvc.perform(get("/admin/store/detailbyuser/"+GlobalConstantTest.WHOLESALER_SLUG+"ddsf")
                .headers(headers)
        ).andExpectAll(
                status().is(404)
        ).andDo(print());

    }


    @Test
    public void testGetStoreDetailByUser() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();

        headers.set(GlobalConstant.AUTHORIZATION , token);
        mockMvc.perform(get("/admin/store/detailbyuser/"+GlobalConstantTest.WHOLESALER_SLUG)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());

    }


    @Test
    public void testUpdateStoreStatusWithoutParams() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);

        String json = """
                {
                }
                """;

        mockMvc.perform(post("/admin/store/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(406)
        ).andDo(print());

    }







    @Test
    public void testUpdateStoreStatusWithWrongSlug() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);

        String json = """
                {
                    "slug" : "sdfsdfsd"
                }
                """;

        mockMvc.perform(post("/admin/store/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(404)
        ).andDo(print());

    }


    @Test
    public void testUpdateStoreStatus() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);

        // Make sure by default status is deactivated
        String json = """
                {
                    "slug" : "{slug}",
                    "status" : "A"
                }
                """
                .replace("{slug}",GlobalConstantTest.TEST_STORE_SLUG)
                ;

        mockMvc.perform(post("/admin/store/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());

    }





    @Test
    public void testGetStoreSubcategory() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                  {
                  }
                """;
        mockMvc.perform(post("/admin/store/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(406)
        );
    }


    @Test
    public void testGetStoreCategory() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                  {
                  }
                """;
        MvcResult result = mockMvc.perform(post("/admin/store/category")
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
            headers.set(GlobalConstant.AUTHORIZATION, token);
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
    public void testStoreCategoryAdd() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                  {
                  "category" : "Mock Test Store Category {random}",
                  "icon": "Mock test icon"
                  }
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6)); // randomly added due to duplicate category issue.
        mockMvc.perform(post("/admin/store/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(201)
        ).andDo(print());
    }


    @Test
    public void testStoreCategoryUpdate() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                  {
                  "id" : 1,
                  "category" : "Mock Test Store updated Category {random}",
                  "icon": "Mock test icon"
                  }
                """.replace("{random}", UUID.randomUUID().toString().substring(0,6)); // randomly added due to duplicate category issue.
        MvcResult result = mockMvc.perform(post("/admin/store/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                ).andExpectAll(
                        status().is(200)
                ).andDo(print())
                .andReturn();
        String slug = extractSlugFromResponseViaRes(result);

        // update store category via staff account
        testDeleteCategoryViaStaff(slug);

        // update store category via super admin account
        testDeleteCategoryViaSuperAdmin(slug);
    }



    public void testDeleteCategoryViaStaff(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                {
                    "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/store/category/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(403), // Only super admin can delete item's category
                jsonPath("$.message",is("Only super admin can delete a store category."))
        );
    }


    public void testDeleteCategoryViaSuperAdmin(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                {
                    "slug" : "{slug}"
                }`
                """
                .replace("{slug}",slug); // use @Test and use valid slug for separate test
        mockMvc.perform(post("/admin/store/category/delete")
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
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                {
                    "slug" : "{slug}"
                }`
                """
                .replace("{slug}",slug); // use @Test and use valid slug for separate test
        mockMvc.perform(post("/admin/store/subcategory/delete")
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
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                {
                    "slug" : "{slug}"
                }`
                """
                .replace("{slug}",slug); // use @Test and use valid slug for separate test
        mockMvc.perform(post("/admin/store/subcategory/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        );
    }




    @Test
    public void testStoreSubSCategoryAdd() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                  {
                    "categoryId" : 0,
                    "subcategory" : "Mock test subcategory {random}",
                    "icon" : "test"
                  }
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6)); // random added due to duplicate category issue.
        mockMvc.perform(post("/admin/store/subcategory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                status().is(201)
        ).andDo(print());
    }



    @Test
    public void testStoreSubCategoryUpdate() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION,token);
        String json = """
                  {
                    "id" : 31,
                    "categoryId" : 0,
                    "subcategory" : "Mock test subcategory {random}",
                    "icon" : "test"
                  }
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6)); // randomly added due to duplicate category issue.
        MvcResult result = mockMvc.perform(post("/admin/store/subcategory/update")
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




}
