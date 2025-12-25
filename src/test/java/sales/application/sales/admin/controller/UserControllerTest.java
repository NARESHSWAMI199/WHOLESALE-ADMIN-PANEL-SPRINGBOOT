package sales.application.sales.admin.controller;


import com.sales.SalesApplication;
import com.sales.admin.services.UserService;
import com.sales.dto.UserDto;
import com.sales.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest extends TestUtil {



    HttpHeaders headers = new HttpHeaders();


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


    @Test
    public void addUserRetailerWithoutLogin() throws Exception {
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        String json = """
                {
                    "email" : "{email}",
                    "username" : "naresh swami",
                    "userType"  : "R",
                    "contact" : "{contact}",
                }
                """
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone)
                ;
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
//                .andDo(print())
                .andReturn();
                headers.set("Authorization", extractTokenFromResponse(result));

    }






    /** For admin login */
    @Test
    public void addRetailerMultipartRequestTest() throws Exception{
        String json = """
                {
                    "email" : "{email}",
                    "username" : "naresh swami",
                    "userType"  : "R",
                    "contact" : "{contact}",
                }
                """;

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
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        String json = """
                {
                    "email" : "{email}",
                    "username" : "Retailer swami",
                    "userType"  : "R",
                    "contact" : "{contact}"
                }
                """
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone)
                ;

        MvcResult result = mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpect(status().is(201))
                .andDo(print()).andReturn();

        String userSlug = extractSlugFromResponseViaRes(result);

        // delete add user
        testDeleteRetailerUserViaStaffAccount(userSlug);
    }



    @Test
    public void addWholesalerAndUpdateTest() throws Exception{
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();

        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");

        HttpHeaders staffHeader = new HttpHeaders();
        staffHeader.set("Authorization",token);
        // Creating wholesaler
        String json = """
                {
                    "email" : "{email}",
                    "username" : "naresh swami",
                    "userType"  : "W",
                    "contact" : "{contact}",
                    "city" : "1",
                    "state" : "1",
                    "street" : "1 Moti dungri",
                    "storeName" : "abc",
                    "storeEmail" : "{storeEmail}",
                    "description" : "test",
                    "categoryId" : 0,
                    "subCategoryId"  : 0,
                    "zipCode" : "302013",
                    "storePhone" : "{storePhone}"
                }
                """
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone)
                .replace("{storePhone}",randomPhone)
                .replace("{storeEmail}",randomEmail);
            MvcResult result = mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpect(status().is(201))
                .andDo(print())
                .andReturn();

        String userSlug = extractSlugFromResponseViaRes(result);

        // Updating wholesaler
        String updatedJson = """
                {
                    "slug" : "{slug}",
                    "email" : "{email}",
                    "username" : "naresh swami",
                    "contact" : "{contact}",
                    "storeName" : "abc",
                    "storeEmail" : "{storeEmail}",
                    "description" : "test",
                    "categoryId" : 0,
                    "subCategoryId"  : 0,
                    "storePhone" : "{storePhone}",
                    "zipCode" : "302013",
                    "city" : "1",
                    "state" : "1",
                    "street" : "1 Moti dungri"
                }
                """
                .replace("{slug}",userSlug)
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone)
                .replace("{storeEmail}",randomEmail)
                .replace("{storePhone}",randomPhone)
                ;
        // update created wholesaler
        mockMvc.perform(post("/admin/auth/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson)
                .headers(staffHeader)
        )
        .andExpectAll(
            status().is(200)
        );

        // delete wholesaler via staff account
        testDeleteWholesalerUserViaStaffAccount(userSlug);


    }


    public String addStaff() throws Exception {
        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        String json = """
                {
                    "email" : "{email}",
                    "username" : "Mock Test Staff",
                    "userType"  : "S",
                    "contact" : "{contact}",
                    "groupList" : [0,1]            
                }
                """
                .replace("{email}",randomEmail)
                .replace("{contact}",randomPhone)
                ;

            MvcResult result = mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpect(status().is(201))
                .andDo(print())
                .andReturn();

            return extractSlugFromResponseViaRes(result);
        }


    /** add user updated  */

    @Test
    public void getRetailerWithoutLogin () throws Exception {
        mockMvc.perform(get("/auth/admin/detail/"+GlobalConstantTest.RETAILER_SLUG))
                .andExpect(status().is(401))
                .andDo(print());
    }


    @Test
    public void getRetailer () throws Exception {
        mockMvc.perform(get("/admin/auth/detail/"+GlobalConstantTest.RETAILER_SLUG)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getDeletedWholesaler () throws Exception {
        String deletedWholesalerSlug = "bad58cac-61f9-4b97-805a-fbfad48ebf5f";
        mockMvc.perform(get("/admin/auth/detail/"+deletedWholesalerSlug)
                    .headers(headers)
                )
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message",is("User not found.")))
                .andDo(print())
        ;
    }

    @Test
    public void getWholesaler () throws Exception {
        mockMvc.perform(get("/admin/auth/detail/"+GlobalConstantTest.WHOLESALER_SLUG)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andDo(print())
        ;
    }


    @Test
    public void getStaff () throws Exception {
        mockMvc.perform(get("/admin/auth/detail/"+GlobalConstantTest.STAFF_SLUG)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andDo(print())
        ;
    }


    @Test
    public void getStaffGroups () throws Exception {
        mockMvc.perform(get("/admin/auth/groups/"+GlobalConstantTest.STAFF_SLUG)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andDo(print())
        ;
    }


    /** ======================= UPDATE USERS */
    @Test
    public void updateStaffViaStaffUserAccount() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");
        HttpHeaders staffHeader = new HttpHeaders();
        staffHeader.set("Authorization",token);

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();
        /** user before update */
        String userSlug = addStaff();

        String json = """
                   {
                    "slug" : "{slug}",
                    "email" : "{email}",
                    "username" : "Mock Test Staff",
                    "userType"  : "S",
                    "contact" : "{contact}",
                    "groupList" : [0,1]            
                }
                """.replace("{slug}",userSlug)
                .replace("{email}", randomEmail)
                .replace("{contact}", randomPhone)
                ;

        System.err.println(json);

        mockMvc.perform(post("/admin/auth/update")
                        .headers(staffHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().is(403)) // because only a super admin can update a staff
                .andDo(print())
        ;


        // delete user via staff account
        testDeleteStaffUserViaStaffAccount(userSlug);

        // delete user via super admin account
        testDeleteStaffUserViaSuperAdmin(userSlug);

    }


    @Test
    public void updateStaffViaSuperAdmin() throws Exception {
        /** user before update */
        String userSlug = addStaff();

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = getRandomMobileNumber();

        String json = """
                   {
                    "slug" : "{slug}",
                    "email" : "{email}",
                    "username" : "Mock Test Staff",
                    "userType"  : "S",
                    "contact" : "{contact}",
                    "groupList" : [0,1]            
                }
                """.replace("{slug}",userSlug)
                .replace("{email}", randomEmail)
                .replace("{contact}", randomPhone)
                ;
        mockMvc.perform(post("/admin/auth/update")
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().is(200))
            .andDo(print());

    }



    /** ===================== Get all users ===================== */

    @Test
    public void getAllWholesalerWithoutLogin() throws Exception {
        String json = """
                {}
                """;
        mockMvc.perform(post("/admin/auth/W/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().is(401))
                .andDo(print());
    }


    @Test
    public void getAllWholesaler() throws Exception {
        String json = """
                {}
                """;
        mockMvc.perform(post("/admin/auth/W/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content",notNullValue()),
                        jsonPath("$.numberOfElements",notNullValue())
                )
                .andDo(print());
    }



    @Test
    public void getAllRetailer() throws Exception {
        String json = """
                {}
                """;
        mockMvc.perform(post("/admin/auth/R/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content",notNullValue()),
                        jsonPath("$.numberOfElements",notNullValue())
                )
                .andDo(print());
    }


    @Test
    public void getAllStaff() throws Exception {
        String json = """
                {}
                """;
        mockMvc.perform(post("/admin/auth/O/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content",notNullValue()),
                        jsonPath("$.numberOfElements",notNullValue())
                )
                .andDo(print());
    }



/** =====================  Update Status ===================== */


@Test
public void updateUserWrongStatus() throws Exception {
    String json = """
                {
                "slug" : "bd7072f2-ed92-4ee9-8c91-7c8366d0abd2",
                "status" : "F"
                }
                """;
    mockMvc.perform(post("/admin/auth/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .headers(headers)
            )
            .andExpectAll(
                    status().is(406),
                    jsonPath("$.message",is("Status must be A or D."))
            )
            .andDo(print());
}


    @Test
    public void updateUserStatus() throws Exception {
        String json = """
                {
                "slug" : "{slug}",
                "status" : "A"
                }
                """
                .replace("{slug}",GlobalConstantTest.STAFF_SLUG)
                ;
        mockMvc.perform(post("/admin/auth/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }


    @Test
    public void updateWrongUserStatus() throws Exception {
        String json = """
                {
                "slug" : "wrong_slug",
                "status" : "A"
                }
                """;
        mockMvc.perform(post("/admin/auth/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(404)
                )
                .andDo(print());
    }



    @Test
    public void updateUserStatusWithoutParams() throws Exception {
        String json = """
                {
                }
                """;
        mockMvc.perform(post("/admin/auth/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                )
                .andExpectAll(
                    status().is(406)
                )
                .andDo(print());
    }


    @Test
    public void testCanUserUpdateSelfStatus() throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  slug = loggedUserResponse.get("slug");
        String  token = loggedUserResponse.get("token");
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.set("Authorization",token);
        String json = """
                {
                "slug" : "{self_slug}", 
                "status" : "A"
                }
                """
                .replace("{self_slug}",slug)
                ;
        mockMvc.perform(post("/admin/auth/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(requestHeader)
                )
                .andExpectAll(
                        status().is(403)
                )
                .andDo(print());
    }



    @Test
    public void testSendOtpWrongEmail() throws Exception {

        String json = """
                {
                  "email" : "swaminaressdfsd3@gmail.com" 
                }
                """;

        mockMvc.perform(post("/admin/auth/sendOtp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
            status().is(406)
        );
    }

    @Test
    public void testSendOtp() throws Exception {

        String json = """
                {
                  "email" : "swaminaresh993@gmail.com" 
                }
                """;

        mockMvc.perform(post("/admin/auth/sendOtp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
                status().is(200)
        );
    }



    @Test
    public void testLoginWithWrongOtp() throws Exception {
        // for one time otp
        String json = """
                {
                    "email" : "swaminaresh993@gmail.com",
                    "password" : "302978"
                }
                """;
        mockMvc.perform(post("/admin/auth/login/otp")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401),
                jsonPath("$.message",is("Wrong otp password."))
        )
                .andDo(print())
        ;
    }





/*
    @Test
    public void testLoginViaOtpButBlocked() throws Exception {
        // Make sure user must be blocked otherwise this test will fail
        String json = """
                {
                    "email" : "swaminaresh993@gmail.com",
                    "password" : "156384"
                }
                """;
        mockMvc.perform(post("/admin/auth/login/otp")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpectAll(
                status().is(401),
                jsonPath("$.message",is("You are blocked by admin."))
        );
    }
    */


    @Autowired
    UserService userService;

    @Test
    public void testOtpLoginWithRightCredential() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail(GlobalConstantTest.STAFF_TEST_EMAIL);
        userDto.setPassword(GlobalConstantTest.STAFF_TEST_PASSWORD);
        User user = userService.findByEmailAndPassword(userDto);
        // Make sure otp is right
        String json = """
                {
                    "email" : "{email}",
                    "password" : "{otp}"
                }
                """
                .replace("{email}",user.getEmail())
                .replace("{otp}",user.getOtp())
                ;
        mockMvc.perform(post("/admin/auth/login/otp")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(200),
                jsonPath("$.message",is("Successfully logged in."))
        );
    }



    public void testDeleteStaffUserViaStaffAccount(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/auth/delete")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(403)
                )
                .andDo(print());
    }



    public void testDeleteStaffUserViaSuperAdmin(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/auth/delete")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }



    public void testDeleteWholesalerUserViaStaffAccount(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/auth/delete")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }

    public void testDeleteRetailerUserViaStaffAccount(String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String  token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        String json = """
                {
                    "slug" : "{slug}"
                }
                """
                .replace("{slug}",slug);
        mockMvc.perform(post("/admin/auth/delete")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200)
                )
                .andDo(print());
    }






}
