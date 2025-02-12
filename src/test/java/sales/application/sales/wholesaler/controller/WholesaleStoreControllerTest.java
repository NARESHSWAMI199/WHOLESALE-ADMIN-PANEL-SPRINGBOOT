package sales.application.sales.wholesaler.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Map;
import java.util.UUID;

@SpringBootTest
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

        String json = """
                {
                    "string" : ""
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        );
    }
    @Test
    public void testUpdateStore() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/create"));
    }
    @Test
    public void testNotifications() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/create"));
    }
    @Test
    public void testUpdateNotifications() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/create"));
    }
    @Test
    public void testGetWholesaleStoreCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/create"));
    }
    @Test
    public void testGetWholesaleStoreSubcategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/store/create"));
    }


}
