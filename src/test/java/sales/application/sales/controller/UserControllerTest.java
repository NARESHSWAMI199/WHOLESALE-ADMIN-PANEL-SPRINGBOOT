package sales.application.sales.controller;


import com.sales.SalesApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import sales.application.sales.util.TestUtil;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest extends TestUtil {


    String authToken;

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


    @Test
    void testLoginWithWrongPasswordWithoutParams() throws Exception {
        String userJson = """
            {
                "email" : "",
                "password" : ""
            }
            """;

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Invalid credentials.")))
                .andDo(print());
    }


    @Test
    void testLoginWithWrongPasswordWithNullParams() throws Exception {
        String userJson = """
            { }
            """;

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Invalid credentials.")))
                .andDo(print());
    }



    // =========================  ADD OR UPDATE USER ================================


    /**

     private String email;
     private String password;
     private String username;
     private String token;
     private String userType="R";
     private String status;
     private String contact="";
     private String slug;
     private MultipartFile profileImage;
     Integer userId;
     List<Integer> groupList;
     List<Integer> storePermissions;
     }

     */

    @Test
    public void addUserRetailerWithoutLogin() throws Exception {
        String json = """
                {
                    "email" : "nareshswami@gmail.com",
                    "username" : "naresh swami",
                    "userType"  : "R",
                    "contact" : "9145808226",
                }
                """;
        mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(401))
                .andDo(print());
    }



    @BeforeEach
    public void loginFirst () throws Exception {
            String userJson = """
            {
                "email" : "naresh@gmail.com",
                "password" : "123456"
            }
            """;

        MvcResult result = mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username", is("naresh")))
                .andExpect(jsonPath("$.user.email", is("naresh@gmail.com")))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andDo(print())
                .andReturn();
        authToken = extractTokenFromResponse(result);
    }




    @Test
    public void addRetailerMultipartRequestTest() throws Exception{
        String json = """
                {
                    "email" : "nareshswami@gmail.com",
                    "username" : "naresh swami",
                    "userType"  : "R",
                    "contact" : "9145808226",
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        mockMvc.perform(post("/admin/auth/add")
                .content(json)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(headers)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message",is("Content-Type 'multipart/form-data")));

    }


    @Test
    public void addRetailerTest() throws Exception{
        String json = """
                {
                    "email" : "nareshswami@gmail.com",
                    "username" : "naresh swami",
                    "userType"  : "R",
                    "contact" : "7145808226"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpect(status().is(200))
                .andDo(print());
    }


}
