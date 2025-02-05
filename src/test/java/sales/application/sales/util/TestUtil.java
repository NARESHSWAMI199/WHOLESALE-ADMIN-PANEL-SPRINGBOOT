package sales.application.sales.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestUtil {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For JSON parsing/creation


    protected String extractTokenFromResponse(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class); // Create a TokenResponse class
        return tokenResponse.getToken();
    }



    protected String extractSlugFromResponseViaRes(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TestUserResponse testUserResponse = objectMapper.readValue(responseBody, TestUserResponse.class); // Create a TokenResponse class
        return (String) testUserResponse.getRes().get("slug");
    }


    protected String extractSlugFromResponseViaUser(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TestUser testUser = objectMapper.readValue(responseBody, TestUser.class); // Create a TokenResponse class
        return (String) testUser.getUser().get("slug");
    }

    @Getter
    @Setter
    private static class TokenResponse {
        private String token;
    }



    @Getter
    @Setter
    private static class TestUserResponse {
        private Map<String,Object> res;
    }

    @Getter
    @Setter
    private static class TestUser {
        private Map<String,Object> user;
    }


    public Map<String,String> getLoginBeaverSlugAndToken(String email, String password) throws Exception {
        String json = """
                    {
                        "email" : "{email}",
                        "password": "{password}"
                    }
                """
                .replace("{email}",email).replace("{password}",password);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        Map<String,String> response = new HashMap<>();
        response.put("slug", extractSlugFromResponseViaUser(result));
        response.put("token", extractTokenFromResponse(result));
        return response;
    }



    public String getRandomMobileNumber(){
        Random random  = new Random();
        String randomMobileNumber = "9";
        for (int i = 0; i < 9; i++) {
            int randomNumber = random.nextInt(9); // Generates any integer (positive or negative)
            randomMobileNumber += randomNumber;
        }
        return randomMobileNumber;
    }






}
