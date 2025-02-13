package sales.application.sales.wholesaler.controller;


import com.google.gson.Gson;
import com.sales.SalesApplication;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
public class WholesaleStoreControllerTest extends TestUtil {


    @Autowired
    MockMvc mockMvc;

    @Test
    public void testAddStore() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        MockMultipartFile file = getImageMultipartFileToUpload("storePic");
        String json = """
                {
                  "storeEmail": "{email}",
                  "storeName": "Mock test wholesaler",
                  "storePhone": "{phone}",
                  "description": "Mock test store created by mock test user",
                  "street": "Murlipura",
                  "zipCode": "302013",
                  "city": "1",
                  "state": "1",
                  "categoryId" : "0",
                  "subCategoryId" : "0"
                }
                """
                .replace("{email}",randomEmail)
                .replace("{phone}",randomPhone)
                ;

        Map<String,String> params = new Gson().fromJson(json,Map.class);
        MockMultipartHttpServletRequestBuilder multipart = MockMvcRequestBuilders.multipart("/wholesale/store/add");
        params.forEach(multipart::param);
        mockMvc.perform(multipart
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .headers(headers)
        ).andExpectAll(
                // TODO : If user don't have store then change it to 200
                MockMvcResultMatchers.status().is(409)
        ).andDo(MockMvcResultHandlers.print());
    }
    @Test
    public void testUpdateStore() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        MockMultipartFile file = getImageMultipartFileToUpload("storePic");
        String json = """
                {
                  "storeSlug" : "{storeSlug}",
                  "storeEmail": "{email}",
                  "storeName": "Mock test wholesaler updated",
                  "storePhone": "{phone}",
                  "description": "Mock test store created by mock test user",
                  "street": "Murlipura updated",
                  "zipCode": "302013",
                  "city": "1",
                  "state": "1",
                  "categoryId" : "0",
                  "subCategoryId" : "0"
                }
                """
                .replace("{storeSlug}",GlobalConstantTest.TEST_STORE_SLUG)
                .replace("{email}",randomEmail)
                .replace("{phone}",randomPhone)
                ;

        Map<String,String> params = new Gson().fromJson(json,Map.class);
        MockMultipartHttpServletRequestBuilder multipart = MockMvcRequestBuilders.multipart("/wholesale/store/update");
        params.forEach(multipart::param);
        mockMvc.perform(multipart
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .headers(headers)
        ).andExpectAll(
                MockMvcResultMatchers.status().is(200)
        ).andDo(MockMvcResultHandlers.print());
    }
    @Test
    public void testUpdateNotifications() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json = """
                {
                    "seenIds" : [0,0]
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/update/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                MockMvcResultMatchers.status().is(200)
        ).andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void testUpdateNotificationsWithoutParams() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/update/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers)
        ).andExpectAll(
                MockMvcResultMatchers.status().is(406)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testNotifications() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json = """
                {}
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/notifications")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                    MockMvcResultMatchers.status().isOk()
        )
        .andDo(MockMvcResultHandlers.print())

        ;
    }
    @Test
    public void testGetWholesaleStoreCategory() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/store/category")
                .headers(headers)
        ).andExpectAll(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

    }
    @Test
    public void testGetWholesaleStoreSubcategory() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/store/subcategory/0")
                .headers(headers)
        ).andExpectAll(
                MockMvcResultMatchers.status().isOk()
        );
    }


}
