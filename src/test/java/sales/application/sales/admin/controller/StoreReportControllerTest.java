package sales.application.sales.admin.controller;

import com.sales.SalesApplication;
import com.sales.entities.Store;
import com.sales.entities.StoreReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import sales.application.sales.util.TestUtil;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StoreReportControllerTest extends TestUtil {

    @Autowired
    private com.sales.admin.repositories.StoreReportRepository storeReportRepository;

    private String token;

    @BeforeEach
    public void loginUserTest() throws Exception {
        token = loginUser("SA");
    }

    @Test
    public void findAllStoreReports() throws Exception {
        String email = createRandomEmail();
        String slug = UUID.randomUUID().toString();
        var user = createUser(slug, email, "pw", "W");
        Store store = createStore();
        store.setUser(user);
        storeRepository.save(store);

        StoreReport sr = new StoreReport();
        sr.setStoreId(store.getId());
        sr.setUserId(user.getId());
        sr.setMessage("problem");
        sr.setCreatedAt(System.currentTimeMillis());
        storeReportRepository.save(sr);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);

        String json = "{}";

        mockMvc.perform(post("/admin/store/report/all").contentType(MediaType.APPLICATION_JSON).content(json).headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.numberOfElements", notNullValue()))
                .andDo(print());
    }

}
