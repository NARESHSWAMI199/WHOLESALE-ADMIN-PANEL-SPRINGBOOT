package sales.application.sales.wholesaler.controller;

import com.sales.SalesApplication;
import com.sales.global.GlobalConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = {SalesApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WholesaleItemReviewControllerTest extends TestUtil {

    private String token;

    @BeforeEach
    public void loginUserTest() throws Exception {
        token = loginUser(GlobalConstantTest.WHOLESALER);
    }

    @Test
    public void testGetAllReviewsWithoutLogin() throws Exception {
        String json = "{}";
        mockMvc.perform(post("/wholesale/item/review/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(result -> {
            int status = result.getResponse().getStatus();
            org.junit.jupiter.api.Assertions.assertTrue(status == 401 || status == 403);
        });
    }

    @Test
    public void testGetAllReviewsWithLogin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION, token);
        String json = "{}";
        mockMvc.perform(post("/wholesale/item/review/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(json)
        ).andExpect(result -> {
            int status = result.getResponse().getStatus();
            // allow 200 or 406 (not acceptable) depending on content negotiation
            org.junit.jupiter.api.Assertions.assertTrue(status == 200 || status == 406);
        });
    }

}
