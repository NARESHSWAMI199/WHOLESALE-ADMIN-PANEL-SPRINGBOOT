package src.test.test.com.controllers;


import com.sales.SalesApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import src.test.test.com.TestUtil;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest extends TestUtil {

    @Test
    void testLoginWithRightPassword() throws Exception {
        String userJson = """
            {
                "email" : "naresh@gmail.com",
                "password" : "123456"
            }
            """;

        mockMvc.perform(post("/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username", is("naresh")))
                .andExpect(jsonPath("$.user.email", is("naresh@gmail.com")))
                .andDo(print());
    }


    @Test
    void testLoginWithWrongPassword() throws Exception {
        String userJson = """
            {
                "email" : "test.naresh@gmail.com",
                "password" : "123456"
            }
            """;

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Invalid credentials.")))
                .andDo(print());
    }


}
