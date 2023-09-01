package com.webapp.researchhub.Events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.researchhub.API.EventAPI;
import com.webapp.researchhub.config.SecurityConfig;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.calendar.DTO.EventDTO;
import com.webapp.researchhub.domain.calendar.Event;
import com.webapp.researchhub.domain.calendar.EventInterest;
import com.webapp.researchhub.repository.AccountRepository;
import com.webapp.researchhub.repository.Calendar.EventInterestRepository;
import com.webapp.researchhub.repository.Calendar.EventRepository;
import com.webapp.researchhub.repository.Forum.ForumCategoryRepository;
import com.webapp.researchhub.repository.Forum.ForumCommentRepository;
import com.webapp.researchhub.repository.Forum.ForumThreadRepository;
import com.webapp.researchhub.repository.Forum.ForumVoteRepository;
import com.webapp.researchhub.repository.ProfilePicRepository;
import com.webapp.researchhub.repository.RoleRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.service.FileService;
import com.webapp.researchhub.service.MyUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WebMvcTest(EventAPI.class)
public class EventsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventAPI eventAPI;

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
    FileService fileService;

    @MockBean
    ForumVoteRepository forumVoteRepository;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    EventInterestRepository eventInterestRepository;

    @MockBean
    ProfilePicRepository profilePicRepository;
    @MockBean
    AccountRepository accountRepository;

    Event event;

    MyUser user;

    @Mock
    EventInterest interest;

    private final String baseUrl = "/api/event";

    @BeforeEach
    public void createUser() {
        this.user = new MyUser("foo","foo", "bar", "foo@bar.com", "passwd");
    }

    @BeforeEach
    void setUp(){
        List<EventInterest> interestList = new ArrayList<>();
        event = new Event();
        event.setId(1L);
        event.setTitle("Title!");
        event.setVirtual(true);
        event.setStartDate(new Date());
        event.setEndDate(new Date());
        event.setDescription("Description");
        event.setOrganiser(user);
        event.setLocation("Asda");
        interest.setId(1L);
        interest.setEvent(event);
        interest.setUsers(user);
        interestList.add(interest);
        event.setEventInterestList(interestList);
    }

    @DisplayName("Test Get events Endpoint.")
    @Test
    public void get_testGetEvents() throws Exception {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(event));
        mockMvc.perform(get(baseUrl).secure(true)).andExpect(status().is(302));
    }

    @DisplayName("Test Get event by Id Endpoint.")
    @Test
    public void get_testGetEvent() throws Exception {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(event));
        mockMvc.perform(get(baseUrl).secure(true).param("id", "1")).andExpect(status().is(302));
    }

    @DisplayName("Test Endpoint Get events by year.")
    @Test
    public void get_testGetEventsByYear() throws Exception {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(event));
        mockMvc.perform(get(baseUrl +"/year").secure(true).param("year","2021")).andExpect(status().is(302));
    }

    @DisplayName("Test Endpoint Get events by month and year")
    @Test
    public void get_testGetEventsByMonth() throws Exception {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(event));
        mockMvc.perform(get(baseUrl+"year/{year}/month/{month}", "2021","2").secure(true)).andExpect(status().is(302));
    }

    @DisplayName("Test Delete Endpoint which deletes an event.")
    @Test
    @WithMockUser(roles = "USER")
    public void delete_testDeleteEventById() throws Exception {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(event));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(baseUrl+"/{id}", "1")
                        .secure(true)
                        .with(csrf())
                        .principal(Mockito.mock(Principal.class))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test Put Endpoint which edits an event.")
    @Test
    @WithMockUser(roles="USER")
    public void put_testPatchEvent() throws Exception {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(event));
        EventDTO dto = new EventDTO();
        dto.setId(1L);
        dto.setVirtual(false);
        mockMvc.perform(MockMvcRequestBuilders
                        .put(baseUrl+"/{id}","1")
                        .secure(true)
                        .with(csrf())
                        .principal(Mockito.mock(Principal.class))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test Post Endpoint which creates an event.")
    @Test
    @WithMockUser(roles = "USER")
    public void post_testPostEvent() throws Exception {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(event));
        EventDTO dto = new EventDTO();
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("defaultUser");

        dto.setId(1L);
        dto.setLocation("Minecraft");
        dto.setVirtual(true);
        dto.setDescription("Desc");
        dto.setOrganiser(mockPrincipal.getName());
        dto.setTitle("Title");
        dto.setStartDate(new Date());
        dto.setEndDate(new Date());
        dto.setOrganiserId(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseUrl)
                        .secure(true)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test that it adds the event to the interest list for the user.")
    @Test
    @WithMockUser(roles = "USER")
    public void post_testAddInterest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseUrl+"/user/"+1L)
                        .secure(true)
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test that it deletes the event from the user the interest list for the user.")
    @Test
    @WithMockUser(roles = "USER")
    public void delete_testDeleteInterest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(baseUrl+"/delete/interest/"+1L)
                        .secure(true)
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Test Get which checks if a user is already interested with an event or not.")
    @Test
    @WithMockUser(roles="USER")
    public void get_testGetIfUserIsAlreadyInterested() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(baseUrl+"/"+1L+"/interest")
                        .secure(true)
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
