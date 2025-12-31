package sales.application.sales.admin.services;


import com.sales.SalesApplication;
import com.sales.admin.services.UserService;
import com.sales.dto.UserDto;
import com.sales.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.UUID;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserServiceTest extends TestUtil {


    @Autowired
    private UserService userService;


    @Test
    public void testFindUserUsingEmailAndPassword(){
        String email = createRandomEmail();
        String password = UUID.randomUUID().toString();
        String slug =UUID.randomUUID().toString();
        createUser(slug,email, password, GlobalConstantTest.ADMIN);

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword(password);
        User user = userService.findByEmailAndPassword(userDto);
        Assertions.assertEquals("naresh",user.getUsername());
        Assertions.assertEquals("SA",user.getUserType());

    }


    @Test
    public void testFindUserUsingEmailAndPasswordWithWrongDetails(){
        UserDto userDto = new UserDto();
        userDto.setEmail("test.naresh@gmail.com");
        userDto.setPassword("123456");
        User user = userService.findByEmailAndPassword(userDto);
        Assertions.assertEquals(null,user);
    }
    @Test
    public void testFindUserUsingEmailAndPasswordWithBlankPassword(){
        UserDto userDto = new UserDto();
        userDto.setEmail("test.naresh@gmail.com");

        User user = userService.findByEmailAndPassword(userDto);
        Assertions.assertEquals(null,user);
    }
    @Test
    public void testFindUserUsingEmailAndPasswordWithBlank(){
        UserDto userDto = new UserDto();
        User user = userService.findByEmailAndPassword(userDto);
        Assertions.assertEquals(null,user);
    }

}
