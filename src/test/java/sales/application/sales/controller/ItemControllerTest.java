package sales.application.sales.controller;


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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes=SalesApplication.class)
@AutoConfigureMockMvc
public class ItemControllerTest extends TestUtil {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testAddItem() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        mockMvc.perform(
                multipart("/admin/item/add")
                    .param("name", "Mock test item")
                    .param("wholesaleSlug","20")
                    .param("price","20")
                    .param("discount","10")
                    .param("rating","0")
                    .param("description","Mock test.")
                    .param("capacity","1")
                    .param("categoryId","0")
                    .param("subCategoryId","0")
                    .param("price","20")
                    .param("inStock","P")
                    .param("label","N")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .headers(headers)
        )
                .andExpectAll(
                    status().is(406)
                )
                .andDo(print())
        ;

    }




}
