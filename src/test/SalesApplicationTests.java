package src.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import src.test.test.com.controllers.UserControllerTest;

@SpringBootTest(classes = UserControllerTest.class)
class SalesApplicationTests {

	@Test
	void contextLoads() {
	}

}
