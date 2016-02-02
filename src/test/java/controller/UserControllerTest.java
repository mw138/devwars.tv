package controller;

import com.bezman.model.User;
import com.bezman.service.AuthService;
import com.bezman.service.Security;
import com.bezman.service.UserService;
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

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userService.saveUser(User.builder()
            .username("The New User")
            .password(security.hash("somepass"))
            .role(User.Role.USER)
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
            .andDo(print())
            .andExpect(jsonPath("$..username", Matchers.hasItems("The New User")));
    }
}
