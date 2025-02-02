package sales.application.sales.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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


    @Getter
    @Setter
    private static class TokenResponse {
        private String token;
    }

}
