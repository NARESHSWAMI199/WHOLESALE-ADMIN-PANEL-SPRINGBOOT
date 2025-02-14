package sales.application.sales.admin.controller;


import com.sales.SalesApplication;
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
public class ItemCommentControllerTest extends TestUtil {

    @Autowired
    MockMvc mockMvc;



    @Test
    public void testGetAllCommentsWithoutLogin() throws Exception {
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/item/comments/all")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        ).andDo(print());
    }


    @Test
    public void testGetAllComments() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/item/comments/all")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());
    }



}
