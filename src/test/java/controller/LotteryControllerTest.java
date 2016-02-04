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
public class LotteryControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    Security security;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test_user_purchase_lottery_tickets() throws Exception {
        User user = User.builder()
            .username("The Lottery User")
            .password(security.hash("somepass"))
            .role(User.Role.USER)
            .build();

        user.setRanking(new Ranking());
        user.getRanking().setUser(user);
        user.getRanking().addPoints(10000);
        userService.saveUser(user);
        //more <code></code>
        mockMvc.perform(post("/v1/lottery/purchase")
            .cookie(authService.loginUser(userService.userForUsername("The Lottery User")))
            .param("count","10"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/v1/user/")
        .cookie(authService.loginUser(userService.userForUsername("The Lottery User"))))
        .andExpect(jsonPath("$.inventory.lotteryTickets").value(10)); //odd
    }

}
