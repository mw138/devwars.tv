package controller;

import com.bezman.model.Ranking;
import com.bezman.model.User;
import com.bezman.service.AuthService;
import com.bezman.service.Security;
import com.bezman.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/spring/mvc-config.xml")
public class InfoControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    Security security;

    private MockMvc mockMvc;

    private static User userBits, userXP;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userBits = userService.saveUser(User.builder()
            .username("The Bits User")
            .password(security.hash("somepass"))
            .role(User.Role.USER)
            .build());

        userXP = userService.saveUser(User.builder()
            .username("The XP User")
            .password(security.hash("somepass"))
            .role(User.Role.USER)
            .build());

        userService.addXpAndPointsToUser(userBits, 0, 100000);
        userService.addXpAndPointsToUser(userXP, 1000000, 0);
    }

    @Test
    public void test_xp_leaderboard() throws Exception {
        mockMvc.perform(get("/v1/info/xpleaderboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("The XP User"));
    }

    @Test
    public void test_bits_leaderboard() throws Exception {
        mockMvc.perform(get("/v1/info/bitsleaderboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("The Bits User"));
    }
}
