package sales.application.sales.wholesaler.controller;


import com.sales.SalesApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SalesApplication.class})
@AutoConfigureMockMvc
public class WholesaleUserControllerTest  extends TestUtil {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateOrRegisterUserWithoutLogin() throws Exception {

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();

        String json = """
                    {
                        "email" : "{email}",
                        "username" : "Mock test wholesaler",
                        "password" : "123456",
                        "contact" : "{contact}"
                    }
                """
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
                status().isOk()
        ).andDo(print()).andReturn();

        String slug = extractSlugFromResponseViaUser(result);
        // update user with login as wholesaler
        testUpdateUser(slug);
    }

    @Test
    public void testUpdateUserWithoutLogin() throws Exception {
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        String json = """
                    {
                        "slug" : "provide your slug (optional)"
                        "email" : "{email}",
                        "username" : "Mock test update wholesaler",
                        "password" : "123456",
                        "contact" : "{contact}"
                    }
                """
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone);

        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
                status().is(401)
        );

    }


    @Test
    public void testDetailUserWithoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/auth/detail")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        );
    }

    @Test
    public void testLastSeenUserWithoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/auth/last-seen")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        );
    }


    @Test
    public void testChatUsersWithoutLogin() throws Exception {
        String json = """
                {
                
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/chat/users")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        );
    }


    @Test
    public void testUpdatePasswordWithoutLogin() throws Exception {
            String json = """
                    {
                        "password" : "mocktest"
                    }
                    """;
            mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/password")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpectAll(
                    status().is(401)
            );
    }

    @Test
    public void testUpdateProfileWithoutLogin() {
        // TODO : we need to work on it.
    }



    // With login ==========================


//    @Test
    public void testUpdateUser(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        String json = """
                    {
                        "slug" : "{slug}",
                        "email" : "{email}",
                        "username" : "Mock test update wholesaler",
                        "password" : "123456",
                        "contact" : "{contact}"
                    }
                """
                .replace("{slug}",slug)
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone);

        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/update")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(json)
        ).andExpectAll(
                status().is(201)
        );
    }


    @Test
    public void testDetailUser() {

    }

    @Test
    public void testLastSeenUser() {

    }


    @Test
    public void testChatUsers() {

    }


    @Test
    public void testUpdatePassword() {

    }

    @Test
    public void testUpdateProfile() {

    }

}
