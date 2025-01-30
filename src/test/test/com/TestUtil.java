package src.test.test.com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;


public class TestUtil {
    @Autowired
    protected MockMvc mockMvc;
}
