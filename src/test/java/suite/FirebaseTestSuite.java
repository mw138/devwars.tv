package suite;

import com.bezman.Reference.Reference;
import com.bezman.controller.LotteryController;
import com.bezman.service.AuthService;
import com.bezman.service.UserService;
import controller.*;
import firebase.FirebaseConnectionTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.Cookie;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {FirebaseConnectionTest.class})
public class FirebaseTestSuite {

    @BeforeClass
    public static void before() {
        Reference.testing = true;
        System.setProperty("testing", "true");

        Reference.loadDevWarsProperties();
    }

}
