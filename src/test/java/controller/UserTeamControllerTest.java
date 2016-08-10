package controller;

import com.bezman.model.User;
import com.bezman.model.UserTeam;
import com.bezman.service.AuthService;
import com.bezman.service.Security;
import com.bezman.service.UserService;
import com.bezman.service.UserTeamService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/spring/mvc-config.xml")
public class UserTeamControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    @Autowired
    UserTeamService userTeamService;

    @Autowired
    AuthService authService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Security security;

    private MockMvc mockMvc;

    private UserTeam userTeam;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        User user = userService.saveUser(User.builder()
            .username("The First User")
            .password(security.hash("somepass"))
            .role(User.Role.USER)
            .build());

        userTeam = userTeamService.createTeam(UserTeam.builder()
            .avatarURL("Avatar URL")
            .gamesLost(10L)
            .owner(user)
            .gamesWon(12L)
            .name("Team Name")
            .tag("tag")
            .xp(20)
            .build());
    }

    @Test
    public void test_admin_can_edit_team() throws Exception {
        User newUser = userService.saveUser(User.builder()
            .username("The New User")
            .password(security.hash("somepass"))
            .role(User.Role.USER)
            .build());

        UserTeam newUserTeam = userTeamService.createTeam(UserTeam.builder()
            .avatarURL("New Avatar URL")
            .gamesLost(20L)
            .owner(newUser)
            .gamesWon(30L)
            .name("New Team Name")
            .tag("new")
            .xp(30)
            .build());

        mockMvc.perform(post("/v1/teams/" + userTeam.getId() + "/edit")
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
            .param("userTeam", objectMapper.writeValueAsString(newUserTeam))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.avatarURL").value("New Avatar URL"))
            .andExpect(jsonPath("$.gamesLost").value(20))
            .andExpect(jsonPath("$.owner.id").value(newUser.getId()))
            .andExpect(jsonPath("$.gamesWon").value(30))
            .andExpect(jsonPath("$.name").value("New Team Name"))
            .andExpect(jsonPath("$.tag").value("new"))
            .andExpect(jsonPath("$.xp").value(30));
    }
}
