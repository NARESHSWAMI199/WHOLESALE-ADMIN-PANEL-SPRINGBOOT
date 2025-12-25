package sales.application.sales.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sales.application.sales.testglobal.GlobalConstantTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestUtil {

    private final Logger logger = LoggerFactory.getLogger(TestUtil.class);

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
        TestResResponse resResponse = objectMapper.readValue(responseBody, TestResResponse.class); // Create a TokenResponse class
        return (String) resResponse.getRes().get("slug");
    }


    protected String extractSlugFromResponseViaUser(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TestUser testUser = objectMapper.readValue(responseBody, TestUser.class); // Create a TokenResponse class
        return (String) testUser.getUser().get("slug");
    }

    protected Map<String,Object> extractUserFromResponseViaUser(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        TestUser testUser = objectMapper.readValue(responseBody, TestUser.class); // Create a TokenResponse class
        return testUser.getUser();
    }


    protected List extractCategoryListFromResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();
        List categoryResponse = objectMapper.readValue(responseBody, List.class); // Create a TokenResponse class
        return categoryResponse;
    }

    @Getter
    @Setter
    private static class TokenResponse {
        private String token;
    }



    @Getter
    @Setter
    private static class TestResResponse {
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



    public Map<String,String> getWholesaleLoginBeaverSlugAndToken(String email, String password) throws Exception {
        String json = """
                    {
                        "email" : "{email}",
                        "password": "{password}"
                    }
                """
                .replace("{email}",email).replace("{password}",password);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/login")
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


    public MockMultipartFile getImageMultipartFileToUpload(String parameterName) throws IOException {
        String imageFolder = GlobalConstantTest.IMAGE_FOLDER_PATH_TEST;
        String imageName = GlobalConstantTest.IMAGE_NAME_TEST;
        Path path = Paths.get(imageFolder + imageName);
        logger.error("The image path ================= "+path);
        if (!Files.exists(path)) throw new FileNotFoundException(path + " not found ");
        byte[] imageBytes = Files.readAllBytes(path);
        MockMultipartFile file = new MockMultipartFile(
                parameterName, // Name of the request parameter
                imageName, // Original filename,
                MediaType.IMAGE_PNG_VALUE,
                imageBytes// File content as byte array
        );
        return file;

    }





}
