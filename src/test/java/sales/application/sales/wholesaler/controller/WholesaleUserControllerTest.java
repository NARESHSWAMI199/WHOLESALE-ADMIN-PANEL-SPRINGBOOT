package sales.application.sales.wholesaler.controller;


import com.sales.SalesApplication;
import com.sales.entities.User;
import com.sales.wholesaler.repository.WholesaleUserRepository;
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

    @Autowired
    WholesaleUserRepository wholesaleUserRepository;


//    @Test
    public void testWholesaleLogin(String email) throws Exception {
        String json = """
                {
                    "email" : "{email}",
                    "password" : "{password}"
                }
                """
                .replace("{email}",email)
                .replace("{password}","123456");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpectAll(
                        status().is(200)
//                        jsonPath("$.message", Matchers.is("User exist but not verified. You can login via otp."))
                ).andDo(print())
                .andReturn();
    }


//    @Test
    public void validateOtp(String slug) throws Exception {
        User user = wholesaleUserRepository.findUserBySlug(slug);
        String otp = user.getOtp();
        System.err.println("THE OTP ================== > "+otp);
        String json = """
                {
                    "slug" : "{slug}",
                    "password" : "{otp}"
                }
                """
                .replace("{slug}",slug)
                .replace("{otp}",otp)
                ;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/validate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
                status().isOk()
        );

    }


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
                status().is(201)
        ).andDo(print()).andReturn();

        String slug = extractSlugFromResponseViaUser(result);
        // update user with login as wholesaler
//        testUpdateUser(slug);

        // login user test
        Map<String,Object> user = extractUserFromResponseViaUser(result);
        String email = (String) user.get("email");

        // before login validate otp
        validateOtp(slug);

        // login after validate otp
        testWholesaleLogin(email);



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
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
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
                status().is(200)
        );
    }


    @Test
    public void testDetailUser() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/auth/detail")
                .headers(headers)
        ).andExpectAll(
                status().isOk()
        ).andDo(print())
        ;

    }


    @Test
    public void testDetailUserWithSlug() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/auth/detail/"+GlobalConstantTest.WHOLESALER_SLUG)
                .headers(headers)
        ).andExpectAll(
                status().isOk()
        ).andDo(print())
        ;

    }


    @Test
    public void testUpdateLastSeenUser() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/auth/last-seen")
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print())
        ;

    }


    @Test
    public void testChatUsers() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        String json = """
                {
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/chat/users")
                .headers(headers)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isOk()).andDo(print());

    }


    /** @Note : Make sure if you update password once you need to use update password */
    @Test
    public void testUpdatePassword() throws Exception {
        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(GlobalConstantTest.WHOLESALER_TEST_EMAIL, GlobalConstantTest.WHOLESALER_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        String json = """
                {
                    "password" : "123456"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/auth/password")
                .headers(headers)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().is(200)).andDo(print());

    }

    @Test
    public void testUpdateProfile() {
        // TODO : skipped for now.
    }



    @Test
    public void testValidateOtp() {

    }

}
