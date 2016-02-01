package controller;

import com.bezman.service.UserService;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.Cookie;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {UserControllerTest.class})
@ContextConfiguration(locations = {"classpath:/spring/application-config.xml"})
public class ControllerTestSuite {

    @Autowired
    UserService userService;

    @Autowired


    public Cookie loginForUsername(String username) {
//        userService.
        return null;
    }

}
