package sales.application.sales.wholesaler.controller;

import com.sales.SalesApplication;
import com.sales.global.GlobalConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SalesApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RemoveBgControllerTest extends TestUtil {

    private String token;

    @BeforeEach
    public void loginUserTest() throws Exception {
        token = loginUser(GlobalConstantTest.WHOLESALER);
    }

    @Test
    public void testGetFileWithoutLogin() throws Exception {
        mockMvc.perform(get("/removebg/test.png"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertTrue(status == 401 || status == 403);
                });
    }

    @Test
    public void testGetFileWithLogin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstant.AUTHORIZATION, token);

        // prepare file under public/removebg/{selfSlug}/test.png
        File dir = new File("public/removebg/" + selfSlug);
        dir.mkdirs();
        File file = new File(dir, "test.png");
        if (!file.exists()) {
            try (InputStream in = getImageMultipartFileToUpload("image").getInputStream();
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
            }
        }

        mockMvc.perform(get("/removebg/test.png").headers(headers))
                .andExpect(status().isOk());

        // cleanup
        file.delete();
    }
}
