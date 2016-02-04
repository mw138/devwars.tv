package controller;

import com.bezman.controller.LotteryController;
import com.bezman.service.AuthService;
import com.bezman.service.UserService;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.Cookie;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {UserControllerTest.class, LotteryControllerTest.class})
@ContextConfiguration(locations = {"classpath:/spring/mvc-config.xml"})
public class ControllerTestSuite {

    @BeforeClass
    public static void before() {
        System.setProperty("testing", "true");
    }

}
