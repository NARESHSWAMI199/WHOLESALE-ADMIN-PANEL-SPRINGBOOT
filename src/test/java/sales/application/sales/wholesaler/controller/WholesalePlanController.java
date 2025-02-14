package sales.application.sales.wholesaler.controller;


import com.sales.SalesApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SalesApplication.class})
@AutoConfigureMockMvc
public class WholesalePlanController extends TestUtil {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testGetAllServicePlans() throws Exception {
        Map<String, String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        mockMvc.perform(get("/wholesale/plan/all")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().isOk()
        );
    }



    @Test
    public void testIsMyPlanActive() throws Exception {
        Map<String, String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        mockMvc.perform(get("/wholesale/plan/is-active")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().isOk()
        ).andDo(print());
    }


    @Test
    public void testIsMyPlanActiveWithoutLogin() throws Exception {
        mockMvc.perform(get("/wholesale/plan/is-active")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        ).andDo(print());
    }


    @Test
    public void testGetAllServicePlansWithoutLogin() throws Exception {
        mockMvc.perform(get("/wholesale/plan/all")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(200)
        );
    }



    @Test
    public void testGetAllMyPlansWithoutLogin() throws Exception {
        String json = """
                    {}
                """;
        mockMvc.perform(post("/wholesale/plan/my-plans")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        ).andDo(print());
    }

    @Test
    public void testGetAllMyPlans() throws Exception {
        Map<String, String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        String json = """
                    {}
                """;

        mockMvc.perform(post("/wholesale/plan/my-plans")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.numberOfElements", notNullValue())
        ).andDo(print());
    }




}