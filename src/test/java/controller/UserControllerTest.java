package controller;

import com.bezman.jackson.DevWarsObjectMapper;
import com.bezman.model.User;
import com.bezman.service.AuthService;
import com.bezman.service.Security;
import com.bezman.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
public class UserControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    Security security;

    @Autowired
    ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static User user;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        user = userService.saveUser(User.builder()
            .username("The New User")
            .password(security.hash("somepass"))
            .role(User.Role.USER)
            .build());

        userService.saveUser(User.builder()
            .username("The Admin User")
            .password(security.hash("somepass"))
            .role(User.Role.ADMIN)
            .build());
    }

    @Test
    public void test_login() throws Exception {
        mockMvc.perform(post("/v1/user/login")
            .param("username", "The New user")
            .param("password", "somepass"))
            .andExpect(cookie().exists("token"))
            .andExpect(status().isOk());
    }

    @Test
    public void test_logged_in_user() throws Exception {
        mockMvc.perform(get("/v1/user/")
            .cookie(authService.loginUser(userService.userForUsername("The New User"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("The New User"));
    }

    @Test
    public void test_search_users() throws Exception {
        mockMvc.perform(get("/v1/user/search")
            .cookie(authService.loginUser(userService.userForUsername("The New User")))
            .param("username", "The"))
            .andExpect(jsonPath("$..username", Matchers.hasItems("The New User")));
    }

    @Test
    public void test_add_points_and_xp() throws Exception {
        mockMvc.perform(post("/v1/user/" + user.getId() + "/addpoints")
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
            .param("points", "50")
            .param("xp", "100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ranking.points").value(50D))
            .andExpect(jsonPath("$.ranking.xp").value(100D));

    }

    @Test
    public void test_admin_can_edit_user() throws Exception {
        user.setUsername("New Username");
        user.getRanking().setPoints(200D);
        user.getRanking().setXp(300D);

        mockMvc.perform(post("/v1/user/" + user.getId() + "/edit")
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
            .param("user", objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("New Username"))
            .andExpect(jsonPath("$.ranking.points").value(200D))
            .andExpect(jsonPath("$.ranking.xp").value(300D));

    }
}
