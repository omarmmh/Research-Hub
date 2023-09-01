package com.webapp.researchhub.Forum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.researchhub.API.ForumAPI;
import com.webapp.researchhub.config.SecurityConfig;
import com.webapp.researchhub.domain.Forum.DTO.ThreadCommentDto;
import com.webapp.researchhub.domain.Forum.ForumComment;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.repository.AccountRepository;
import com.webapp.researchhub.repository.Calendar.EventInterestRepository;
import com.webapp.researchhub.repository.Calendar.EventRepository;
import com.webapp.researchhub.repository.Forum.ForumCategoryRepository;
import com.webapp.researchhub.repository.Forum.ForumCommentRepository;
import com.webapp.researchhub.repository.Forum.ForumThreadRepository;
import com.webapp.researchhub.repository.ProfilePicRepository;
import com.webapp.researchhub.repository.RoleRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.service.FileService;
import com.webapp.researchhub.service.MyUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ForumAPI.class)
@Import(SecurityConfig.class)
public class ForumApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ForumAPI forumAPI;

    @MockBean
    ForumThreadRepository forumThreadRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    RoleRepository roleRepository;
    @MockBean
    ForumCategoryRepository forumCategoryRepository;
    @MockBean
    ForumCommentRepository forumCommentRepository;
    @MockBean
    MyUserDetailsService myUserDetailsService;
    @MockBean
    EventRepository eventRepository;
    @MockBean
    FileService fileService;
    @MockBean
    EventInterestRepository eventInterestRepository;
    @MockBean
    ProfilePicRepository profilePicRepository;
    @MockBean
    AccountRepository accountRepository;


    ForumThread forumThread;
    ForumComment forumComment;

    private final String baseUrl = "/api/forum";
    final String threadTitle = "A random title.";
    final String threadBody = "Some text for the thread body.";
    final String commentBody = "Some text for a comment body";

    @BeforeEach
    void setUp() {
        forumThread = new ForumThread();
        forumComment = new ForumComment();
        forumThread.setId(1L);
        forumThread.setTitle(threadTitle);
        forumThread.setBody(threadBody);

        forumComment.setId(1L);
        forumComment.setBody(commentBody);
        forumComment.setThread(forumThread);
    }

    @DisplayName("Test the GET thread endpoint is secure and cannot be accessed by anonymous users.")
    @Test
    public void get_testGetThreadIsSecure() throws Exception {
        Mockito.when(forumThreadRepository.findById(anyLong())).thenReturn(Optional.ofNullable(forumThread));
        mockMvc.perform(get(baseUrl + "/get-comment").secure(true).param("id", "1"))
                .andExpect(status().is(302));
    }


    @DisplayName("Test the GET comment endpoint is secure and cannot be accessed by anonymous users.")
    @Test
    public void get_testGetCommentIsSecure() throws Exception {
        Mockito.when(forumCommentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(forumComment));
        mockMvc.perform(get(baseUrl + "/get-comment").secure(true).param("id", "1"))
                .andExpect(status().is(302));
    }

    @DisplayName("Test POST comment end point is up and secure")
    @Test
    @WithMockUser(roles = "USER")
    public void post_testPostCommentEndpoint() throws Exception {
        ThreadCommentDto data = new ThreadCommentDto();
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(forumThreadRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(forumThread));
        Mockito.when(mockPrincipal.getName()).thenReturn("defaultUser");

        data.setUsersName(mockPrincipal.getName());

        mockMvc.perform(MockMvcRequestBuilders
                .post(baseUrl + "/add-comment")
                .secure(true)
                .with(csrf())
                .principal(Mockito.mock(Principal.class))
                .param("threadId", "")
                .param("body", "some text body")
                .param("UserName", "")
                .content(objectMapper.writeValueAsString(data))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    // Edit thread
    @DisplayName("Test PUT thread end point is up and secure")
    @Test
    @WithMockUser(roles = "USER")
    public void put_testEditThreadIsSecure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseUrl + "/edit-thread")
                        .secure(true)
                        .with(csrf())
                        .principal(Mockito.mock(Principal.class))
                        .param("id", "1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Edit comment
    @DisplayName("Test PUT comment end point is up and secure")
    @Test
    @WithMockUser(roles = "USER")
    public void put_testEditCommentIsSecure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseUrl + "/edit-comment")
                        .secure(true)
                        .with(csrf())
                        .principal(Mockito.mock(Principal.class))
                        .param("id", "1")
                        .param("body", "body of text")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Delete thread
    @DisplayName("Test DELETE thread end point is up and secure")
    @Test
    @WithMockUser(roles = "USER")
    public void del_testDeleteThreadIsSecure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseUrl + "/delete-thread")
                        .secure(true)
                        .with(csrf())
                        .principal(Mockito.mock(Principal.class))
                        .param("id", "1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Delete comment
    @DisplayName("Test DELETE comment end point is up and secure")
    @Test
    @WithMockUser(roles = "USER")
    public void put_testDeleteCommentIsSecure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseUrl + "/delete-comment")
                        .secure(true)
                        .with(csrf())
                        .principal(Mockito.mock(Principal.class))
                        .param("id", "1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
