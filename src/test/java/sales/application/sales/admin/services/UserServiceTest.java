package sales.application.sales.admin.services;


import com.sales.SalesApplication;
import com.sales.admin.services.UserService;
import com.sales.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import sales.application.sales.testglobal.GlobalConstantTest;
import sales.application.sales.util.TestUtil;

import java.util.UUID;

@SpringBootTest(classes = SalesApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserServiceTest extends TestUtil {


    @Autowired
    private UserService userService;

    private final String email = "test.naresh@gmail.com";
    private final String password = "123456";

    @Test
    public void testFindUserUsingEmailAndPassword(){
        String email = createRandomEmail();
        String password = UUID.randomUUID().toString();
        String slug =UUID.randomUUID().toString();
        createUser(slug,email, password, GlobalConstantTest.ADMIN);
        User user = userService.findByEmailAndPassword(email,password);
        Assertions.assertEquals("naresh",user.getUsername());
        Assertions.assertEquals("SA",user.getUserType());

    }


    @Test
    public void testFindUserUsingEmailAndPasswordWithWrongDetails(){
        Assertions.assertThrows(UsernameNotFoundException.class,() -> userService.findByEmailAndPassword(email,UUID.randomUUID().toString()));
    }
    @Test
    public void testFindUserUsingEmailAndPasswordWithBlankPassword() {
        createUser(UUID.randomUUID().toString(),email,password,GlobalConstantTest.STAFF);
        Assertions.assertThrows(UsernameNotFoundException.class,() -> userService.findByEmailAndPassword(email,null));
    }
    @Test
    public void testFindUserUsingEmailAndPasswordWithBlank(){
        Assertions.assertThrows(UsernameNotFoundException.class,() -> userService.findByEmailAndPassword(null,null));
    }


}
