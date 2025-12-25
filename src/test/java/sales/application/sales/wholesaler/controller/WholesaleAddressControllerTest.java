package sales.application.sales.wholesaler.controller;


import com.sales.SalesApplication;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SalesApplication.class})
@AutoConfigureMockMvc
public class WholesaleAddressControllerTest extends  TestUtil{

    @Autowired
    MockMvc mockMvc;


    @Test
    public void getStates() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/address/state")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());
    }


    @Test
    public void getCities() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/address/city/1")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());
    }

}
