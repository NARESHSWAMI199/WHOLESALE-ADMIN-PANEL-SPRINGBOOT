package sales.application.sales.controller;


import com.google.gson.Gson;
import com.sales.SalesApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
public class StoreControllerTest extends TestUtil {

    @Autowired
    MockMvc mockMvc;

    @Test
    public  void testAddStoreWithStaffSlug() throws Exception {

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();

        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        String slug = loggedUserResponse.get("slug");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
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
    public  void testAddStore() throws Exception {

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();

        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        String slug = loggedUserResponse.get("slug");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json = """
                {
                    "storeSlug" : "5030af06-bba9-4e77-bce8-bd38c6cf13f2",
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
        MockMultipartHttpServletRequestBuilder requestBuilder  = multipart("/admin/store/update");
        params.forEach(requestBuilder::param);
        mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is((201))
        );

    }

}
