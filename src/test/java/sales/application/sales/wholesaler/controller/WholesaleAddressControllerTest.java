package sales.application.sales.wholesaler.controller;


import com.sales.SalesApplication;
import com.sales.admin.controllers.AddressController;
import com.sales.admin.repositories.CityRepository;
import com.sales.admin.repositories.ServicePlanRepository;
import com.sales.admin.repositories.StateRepository;
import com.sales.admin.repositories.UserRepository;
import com.sales.admin.services.StoreService;
import com.sales.entities.City;
import com.sales.entities.ServicePlan;
import com.sales.entities.State;
import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import com.sales.utils.WriteExcelUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class WholesaleAddressControllerTest extends  TestUtil{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressController addressController;

    @Autowired
    private StoreService storeService;

    @Autowired
    private WriteExcelUtil writeExcelUtil;

    @Value(value = "${excel.export.absolute}")
    private String excelPath;

    @Autowired
    private ServicePlanRepository servicePlanRepository;

    @Autowired
    CityRepository cityRepository;

    private String token;

    @BeforeEach
    public void loginUser() throws Exception {

        String email = UUID.randomUUID()+"@mocktest.in";
        String password = UUID.randomUUID().toString();

        ServicePlan servicePlan = ServicePlan.builder()
                .name("Test Service plan")
                .slug(UUID.randomUUID().toString())
                .createdAt(new Date().getTime())
                .price(101L)
                .discount(0L)
                .months(6)
                .updatedAt(new Date().getTime())
                .createdBy(1)
                .updatedBy(1)
                .build();
        servicePlan = servicePlanRepository.save(servicePlan);


        User user = User.builder()
                .slug(UUID.randomUUID().toString())
                .userType("W")
                .email(email)
                .password(password)
                .status("A")
                .isDeleted("N")
                .activePlan(servicePlan.getId())
                .build();
        userRepository.save(user);

        Map<String,String> loggedUserResponse = getWholesaleLoginBeaverSlugAndToken(email, password);
        token = loggedUserResponse.get(ConstantResponseKeys.TOKEN);
    }

    @Test
    public void getStates() throws Exception {
        State state = State.builder()
                .stateName("Rajasthan")
                .status("A")
                .build();
        stateRepository.save(state);
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/address/state")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());
    }



    @Test
    public void getCities() throws Exception {
        State state = State.builder()
                .stateName("Rajasthan")
                .status("A")
                .build();
        state = stateRepository.save(state);

        City city = City.builder()
                .cityName("Jaipur")
                .stateId(state.getId())
                .status("A")
                .build();
        cityRepository.save(city);

        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION , token);
        String json = """
                {
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.get("/wholesale/address/city/"+state.getId())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpectAll(
                status().is(200)
        ).andDo(print());
    }

}
