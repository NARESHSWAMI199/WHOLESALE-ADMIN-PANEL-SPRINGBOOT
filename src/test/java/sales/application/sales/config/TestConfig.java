package sales.application.sales.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sales.application.sales.controller.UserControllerTest;


@Configuration
public class TestConfig {


    @Bean
   public UserControllerTest getUserController() {
       return new UserControllerTest();
   }

}
