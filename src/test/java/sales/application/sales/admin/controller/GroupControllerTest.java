package sales.application.sales.admin.controller;


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

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
public class GroupControllerTest extends TestUtil {


    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateGroupWithoutParams () throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/group/create")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
            status().is(406)
        ).andDo(print());
    }


    @Test
    public void testCreateGroupWithStaffAccount () throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        String json = """
                {
                    "name" : "Mock test group"
                }
                
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/group/create")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is(403)
        ).andDo(print());
    }


    @Test
    public void testCreateGroupWithSuperAdminAccount () throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        String json= """
                {
                    "name" : "Mock test group {random}"
                }
                
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6))
                ;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/group/create")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(201)
                ).andDo(print())
                .andReturn();

        String slug = extractSlugFromResponseViaRes(result);
        // update this group
        testUpdateGroupWithSuperAdminAccountWithSlug(slug);

        // find detail by slug
        testFindGroupBySlug(slug);

        // delete group by staff account
        testDeleteGroupByStaffAccount(slug);

        // delete group by super admin account
        testDeleteGroupBySuperAdminAccount(slug);

    }


    @Test
    public void testUpdateGroupWithSuperAdminAccountWithoutSlug () throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        String json= """
                {
                    "name" : "Mock test group {random}"
                }
                
                """
                .replace("{random}", UUID.randomUUID().toString().substring(0,6))
                ;
        mockMvc.perform(MockMvcRequestBuilders.post("/group/update")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers)
            )
            .andExpectAll(
                    status().is(406)
            ).andDo(print())
            .andReturn();
    }



    @Test
    public void testUpdateGroupWithSuperAdminAccountWithWrongSlug () throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json= """
                {
                    "slug" : "{slug}",
                    "name" : "Mock test updated group {random}"
                }
                
                """
                .replace("{slug}","sdfsd")
                .replace("{random}", UUID.randomUUID().toString().substring(0,6))
                ;
        mockMvc.perform(MockMvcRequestBuilders.post("/group/update")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(404)
                ).andDo(print());
    }



    public void testUpdateGroupWithSuperAdminAccountWithSlug (String slug) throws Exception {
            Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
            String token = loggedUserResponse.get("token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization" , token);
            String json= """
                {
                    "slug" : "{slug}",
                    "name" : "Mock test updated group {random}"
                }
                
                """
                    .replace("{slug}",slug)
                    .replace("{random}", UUID.randomUUID().toString().substring(0,6))
                    ;
            mockMvc.perform(MockMvcRequestBuilders.post("/group/update")
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .headers(headers)
                    )
                    .andExpectAll(
                            status().is(200)
                    ).andDo(print());
        }

    @Test
    public void testGetAllGroups () throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json= """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/group/all")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200),
                        jsonPath("$.content",notNullValue())
                ).andDo(print());
    }


    public void testFindGroupBySlug (String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        mockMvc.perform(MockMvcRequestBuilders.get("/group/detail/"+slug)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                )
                .andExpectAll(
                        status().is(200),
                        jsonPath("$.res.group",notNullValue()),
                        jsonPath("$.res.permissions",notNullValue())
                ).andDo(print());
    }



    public void testDeleteGroupByStaffAccount (String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.STAFF_TEST_EMAIL, GlobalConstantTest.STAFF_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);

        String json = """
                    {
                        "slug" : "{slug}"
                    }
                """
                .replace("{slug}",slug);

        mockMvc.perform(MockMvcRequestBuilders.post("/group/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(json)
                )
                .andExpectAll(
                        status().is(403)
                ).andDo(print());
    }
    public void testDeleteGroupBySuperAdminAccount (String slug) throws Exception {
        Map<String,String> loggedUserResponse = getLoginBeaverSlugAndToken(GlobalConstantTest.SUPER_ADMIN_TEST_EMAIL, GlobalConstantTest.SUPER_ADMIN_TEST_PASSWORD);
        String token = loggedUserResponse.get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , token);
        String json = """
                    {
                        "slug" : "{slug}"
                    }
                """
                .replace("{slug}",slug);

        mockMvc.perform(MockMvcRequestBuilders.post("/group/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(json)
                )
                .andExpectAll(
                        status().is(200)
                ).andDo(print());
    }

}
