package sales.application.sales.wholesaler.controller;

import com.sales.SalesApplication;
import com.sales.admin.repositories.ServicePlanRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.WholesalerPlans;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import com.sales.wholesaler.repository.WholesalePlansRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sales.application.sales.util.TestUtil;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class WholesaleDashboardControllerTest extends TestUtil {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServicePlanRepository servicePlanRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WholesalePlansRepository wholesalePlansRepository;

    private String token;

    @BeforeEach
    public void loginUser() throws Exception {

        String email = UUID.randomUUID()+"@mocktest.in";
        String password = UUID.randomUUID().toString();
        String slug = UUID.randomUUID().toString();
        Date currentTime = new Date();
        Date futureDate = new Date();
        futureDate.setMonth(currentTime.getMonth() + 12);

        ServicePlan servicePlan = ServicePlan.builder()
                .name("Test Service plan")
                .slug(UUID.randomUUID().toString())
                .createdAt(currentTime.getTime())
                .price(101L)
                .discount(0L)
                .months(6)
                .updatedAt(currentTime.getTime())
                .createdBy(1)
                .updatedBy(1)
                .build();
        servicePlan = servicePlanRepository.save(servicePlan);


        User user = User.builder()
                .slug(slug)
                .userType("W")
                .email(email)
                .password(password)
                .status("A")
                .isDeleted("N")
                .build();
        user = userRepository.save(user);

        WholesalerPlans wholesalerPlans = WholesalerPlans.builder()
                .userId(user.getId())
                .createdAt(currentTime.getTime())
                .expiryDate(futureDate.getTime())
                .isExpired(false)
                .slug(slug)
                .servicePlan(servicePlan)
                .build();
        wholesalePlansRepository.save(wholesalerPlans);
        user.setActivePlan(wholesalerPlans.getId());
        userRepository.save(user);


        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(email, password);
        token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
    }


    @Test
    public void testGraphWithoutLogin() throws Exception {
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/dashboard/graph/months/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().is(401)
        ).andDo(print());
    }


    @Test
    public void testGraphData() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/wholesale/dashboard/graph/months/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());
    }


    @Test
    public void testCountWithoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/dashboard/counts")
        ).andExpectAll(
                status().is(401)
        ).andDo(print());
    }


    @Test
    public void testCounts() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/dashboard/counts")
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());
    }

}
