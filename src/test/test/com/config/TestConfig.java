package src.test.test.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import src.test.test.com.controllers.UserControllerTest;

@Configuration
public class TestConfig {


    @Bean
   public UserControllerTest getUserController() {
       return new UserControllerTest();
   }

}
