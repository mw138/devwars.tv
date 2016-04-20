package controller;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.jackson.DevWarsObjectMapper;
import com.bezman.model.PodcastEpisode;
import com.bezman.service.AuthService;
import com.bezman.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/spring/mvc-config.xml")
public class PodcastControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthService authService;

    @Autowired
    UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test_get_podcast() throws Exception {
         PodcastEpisode podcastEpisode = PodcastEpisode.builder()
            .episodeName("Hello World")
            .episodeNumber(5)
            .episodeDescription("Lorem Ipsum")
            .showNotes("Long list here")
            .build();

        MvcResult result = mockMvc.perform(fileUpload("/v1/podcast/create")
            .param("episode", Reference.objectMapper.writeValueAsString(podcastEpisode))
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
        )
            .andExpect(status().isOk())
            .andReturn();

        PodcastEpisode returnEpisode = Reference.objectMapper.readValue(result.getResponse().getContentAsString(), PodcastEpisode.class);

        mockMvc.perform(get("/v1/podcast/" + returnEpisode.getId()))
            .andExpect(status().isOk());
    }

    @Test
    public void test_create_podcast_episode() throws Exception {
        PodcastEpisode podcastEpisode = PodcastEpisode.builder()
            .episodeName("Hello World")
            .episodeNumber(5)
            .episodeDescription("Lorem Ipsum")
            .showNotes("Long list here")
            .build();

        mockMvc.perform(fileUpload("/v1/podcast/create")
            .file("file", Util.byteArrayFromResources("mpthreetest.mp3"))
            .param("episode", Reference.objectMapper.writeValueAsString(podcastEpisode))
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
        )
            .andExpect(jsonPath("$.episodeName", is(podcastEpisode.getEpisodeName())))
            .andExpect(jsonPath("$.episodeNumber", is(podcastEpisode.getEpisodeNumber())))
            .andExpect(jsonPath("$.episodeDescription", is(podcastEpisode.getEpisodeDescription())))
            .andExpect(jsonPath("$.podcastAudioURL").exists())
            .andExpect(jsonPath("$.showNotes", is(podcastEpisode.getShowNotes())));
    }

    @Test
    public void test_edit_episode() throws Exception {
        PodcastEpisode podcastEpisode = PodcastEpisode.builder()
            .episodeName("Hello World")
            .episodeNumber(5)
            .episodeDescription("Lorem Ipsum")
            .showNotes("Long list here")
            .build();

        MvcResult mvcResult = mockMvc.perform(fileUpload("/v1/podcast/create")
            .file("file", Util.byteArrayFromResources("mpthreetest.mp3"))
            .param("episode", Reference.objectMapper.writeValueAsString(podcastEpisode))
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
        ).andReturn();

        PodcastEpisode returnEpisode = Reference.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PodcastEpisode.class);
        returnEpisode.setEpisodeName("A different one");
        returnEpisode.setEpisodeDescription("New  Lorem Ipsum");
        returnEpisode.setEpisodeNumber(4);
        returnEpisode.setShowNotes("NEWER SHOW NOTES");

        mockMvc.perform(fileUpload("/v1/podcast/edit")
            .file("file", Util.byteArrayFromResources("mpthreetest.mp3"))
            .param("episode", Reference.objectMapper.writeValueAsString(returnEpisode))
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
        )
            .andExpect(jsonPath("$.episodeName", is(returnEpisode.getEpisodeName())))
            .andExpect(jsonPath("$.episodeNumber", is(returnEpisode.getEpisodeNumber())))
            .andExpect(jsonPath("$.episodeDescription", is(returnEpisode.getEpisodeDescription())))
            .andExpect(jsonPath("$.podcastAudioURL").exists())
            .andExpect(jsonPath("$.showNotes", is(returnEpisode.getShowNotes())));

    }

    @Test
    public void test_delete_podcast_episode() throws Exception {
        PodcastEpisode podcastEpisode = PodcastEpisode.builder()
            .episodeName("Hello World")
            .episodeNumber(5)
            .episodeDescription("Lorem Ipsum")
            .showNotes("Long list here")
            .build();

        MvcResult result = mockMvc.perform(fileUpload("/v1/podcast/create")
            .file("file", Util.byteArrayFromResources("mpthreetest.mp3"))
            .param("episode", Reference.objectMapper.writeValueAsString(podcastEpisode))
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
        )
            .andReturn();

        PodcastEpisode returnEpisode = Reference.objectMapper.readValue(result.getResponse().getContentAsString(), PodcastEpisode.class);

        mockMvc.perform(post("/v1/podcast/" + returnEpisode.getId() + "/delete")
            .cookie(authService.loginUser(userService.userForUsername("The Admin User")))
        )
            .andReturn();

        mockMvc.perform(get("/v1/podcast/" + returnEpisode.getId()))
            .andExpect(status().is(404));
    }
}
