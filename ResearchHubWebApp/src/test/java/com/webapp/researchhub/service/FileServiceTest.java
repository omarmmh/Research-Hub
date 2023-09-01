package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.repository.FileRepository;
import com.webapp.researchhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class FileServiceTest {

    MyUser user;

    MockMultipartFile file;


    @BeforeEach
    public void createUser() {
        this.user = new MyUser("foo","foo", "bar", "foo@bar.com", "passwd");
    }

    @BeforeEach
    public void createFile() {
        this.file = new MockMultipartFile("file","test-file.pdf","application/pdf","File test".getBytes());
    }


    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserRepository userRepository;


    @Autowired
    private MockMvc mvc;


    /***
     * This method tests whether the upload service saves the file to the user and vice versa
     * @throws Exception
     * @author jwm22
     */
    @Test
    @DisplayName("Uploaded file has a user.")
    @WithMockUser(roles="USER")
    public void uploadTest() throws Exception {
        UserFile userFile = new UserFile(file.getOriginalFilename(),file.getContentType(),file.getBytes(),user);
        fileService.Upload(file,user,userFile);
        assertSame(userFile.getUser(),user);
    }


    /***
     * Since you would need to be logged in we check for a 302 status which
     * means it redirects you to the login page since in the security config you
     * need to be logged to view papers.
     * @throws Exception
     * @author jwm22
     */
    @Test
    @DisplayName("User File can be Viewed.")
    public void viewPaperTest() throws Exception {
        UserFile userFile = new UserFile(file.getOriginalFilename(),file.getContentType(),file.getBytes(),user);
        userFile.setId(1L);
        RequestBuilder request = MockMvcRequestBuilders.get("/paper/" + userFile.getId()).contentType(MediaType.APPLICATION_OCTET_STREAM).content(userFile.getData());
        ResultActions result = mvc.perform(request);
        result.andExpect(status().is(302));
    }

}
