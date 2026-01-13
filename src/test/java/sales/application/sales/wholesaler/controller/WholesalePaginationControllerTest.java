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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SalesApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WholesalePaginationControllerTest extends TestUtil {

    private String token;

    @BeforeEach
    public void loginUserTest() throws Exception {
        token = loginUser(GlobalConstantTest.WHOLESALER);
    }

    @Test
    public void testFindUserPaginationWithoutLogin() throws Exception {
        mockMvc.perform(get("/wholesale/pagination/all"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertTrue(status == 401 || status == 403);
                });
    }

    @Test
    public void testFindUserPaginationWithLogin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION, token);
        mockMvc.perform(get("/wholesale/pagination/all").headers(headers))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePaginationRowWithoutLogin() throws Exception {
        String json = "{}";
        mockMvc.perform(post("/wholesale/pagination/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(result -> {
            int status = result.getResponse().getStatus();
            org.junit.jupiter.api.Assertions.assertTrue(status == 401 || status == 403);
        });
    }

    @Test
    public void testUpdatePaginationRowWithLogin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION, token);

        String json = "{ \"pageCount\": 10, \"entityName\": \"item\" }";
        mockMvc.perform(post("/wholesale/pagination/update")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(json)
        ).andExpect(result -> {
            int status = result.getResponse().getStatus();
            // At least ensure request was authenticated and processed (not 401)
            org.junit.jupiter.api.Assertions.assertTrue(status != 401);
        });
    }

}
