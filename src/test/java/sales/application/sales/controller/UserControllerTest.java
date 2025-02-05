package sales.application.sales.controller;


import com.sales.SalesApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.Random;
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

    String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
    String randomPhone = "9"+ new Random().nextInt(999999999);


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
                headers.set("Authorization", extractTokenFromResponse(result));

    }






    /** For admin login */
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
                    "email" : "retailer@gmail.com",
                    "username" : "Retailer swami",
                    "userType"  : "R",
                    "contact" : "7122808226"
                }
                """;

        mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpect(status().is(200))
                .andDo(print());
    }



    @Test
    public void addWholesalerAndUpdateTest() throws Exception{

        String beaverToken = getLoginBeaverToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        HttpHeaders staffHeader = new HttpHeaders();
        staffHeader.set("Authorization",beaverToken);


        String json = """
                {
                    "email" : "nareshswami@gmail.com",
                    "username" : "naresh swami",
                    "userType"  : "W",
                    "contact" : "7145808226",
                    "city" : "1",
                    "state" : "1",
                    "street" : "1 Moti dungri",
                    "storeName" : "abc",
                    "storeEmail" : "abc@gmail.com",
                    "description" : "test",
                    "categoryId" : 0,
                    "subCategoryId"  : 0,
                    "zipCode" : "302013",
                    "storePhone" : 7147580822
                }
                """;
            MvcResult result = mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpect(status().is(200))
                .andDo(print())
                .andReturn();

            String userSlug = extractSlugFromResponseViaRes(result);

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
            status().is(201)
        );


    }


    public String addStaff() throws Exception {

        String json = """
                {
                    "email" : "staff@gmail.com",
                    "username" : "Mock Test Staff",
                    "userType"  : "S",
                    "contact" : "7135808226",
                    "groupList" : [0,1]            
                }
                """;

        MvcResult result = mockMvc.perform(post("/admin/auth/add")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpect(status().is(200))
                .andDo(print())
                .andReturn();

            return extractSlugFromResponseViaRes(result);

        }


    /** add user updated  */

    @Test
    public void getRetailerWithoutLogin () throws Exception {
        String retailerSlug = "54621d58-555c-425c-a48b-f06ae80cea73";
        mockMvc.perform(get("/auth/admin/detail/"+retailerSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username",is("Retailer swami")))
                .andDo(print())
        ;
    }

    @Test
    public void getRetailer () throws Exception {
        String retailerSlug = "54621d58-555c-425c-a48b-f06ae80cea73";
        mockMvc.perform(get("/admin/auth/detail/"+retailerSlug)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res.username",is("Retailer swami")))
                .andDo(print())
        ;
    }

    @Test
    public void getDeletedWholesaler () throws Exception {
        String wholesalerSlug = "d94ee65f-07c7-415b-8a40-20f1b283db58";
        mockMvc.perform(get("/admin/auth/detail/"+wholesalerSlug)
                        .headers(headers)
                )
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message",is("User not found.")))
                .andDo(print())
        ;
    }

    @Test
    public void getWholesaler () throws Exception {
        String wholesalerSlug = "d94ee65f-07c7-415b-8a40-20f1b283db58";
        mockMvc.perform(get("/admin/auth/detail/"+wholesalerSlug)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res.username",is("naresh swami")))
                .andDo(print())
        ;
    }


    @Test
    public void getStaff () throws Exception {
        String staffSlug = "d03efcee-93f6-4e73-b952-a9ee4554c85e";
        mockMvc.perform(get("/admin/auth/detail/"+staffSlug)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andDo(print())
        ;
    }


    @Test
    public void getStaffGroups () throws Exception {
        String staffSlug = "d03efcee-93f6-4e73-b952-a9ee4554c85e";
        mockMvc.perform(get("/admin/auth/groups/"+staffSlug)
                        .headers(headers)
                )
                .andExpect(status().isOk())
                .andDo(print())
        ;
    }


    /** ======================= UPDATE USERS */
    @Test
    public void updateStaffViaStaffUserAccount() throws Exception {
        String beaverToken = getLoginBeaverToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        HttpHeaders staffHeader = new HttpHeaders();
        staffHeader.set("Authorization",beaverToken);

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

    }


    @Test
    public void updateStaffViaSuperAdmin() throws Exception {
        /** user before update */
        String userSlug = addStaff();

        String randomEmail = UUID.randomUUID().toString().substring(0,6) + "@mocktest.in";
        String randomPhone = "9"+ new Random().nextInt(999999999);

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
            .andExpect(status().is(201))
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
                "slug" : "bd7072f2-ed92-4ee9-8c91-7c8366d0abd2",
                "status" : "A"
                }
                """;
        mockMvc.perform(post("/admin/auth/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(201)
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
        String json = """
                {
                "slug" : "self_slug", 
                "status" : "A"
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



}
