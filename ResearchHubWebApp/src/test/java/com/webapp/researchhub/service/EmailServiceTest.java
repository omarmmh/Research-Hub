package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.PasswordResetToken;
import com.webapp.researchhub.repository.PasswordTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    PasswordTokenRepository passwordTokenRepository;
    @InjectMocks
    PasswordTokenService passwordTokenService;
    @InjectMocks
    EmailService emailService;

    private MyUser user;

    @BeforeEach
    public void createUser() {
        this.user = new MyUser("foo","foo", "bar", "foo@bar.com", "passwd");
    }

    /**
     * Test we can construct a URL containing a unique token to identify a user.
     * This URL is then emailed to the user so they can reset their password.
     */
    @DisplayName("Test that the email created contains a valid url including token linking back to the server")
    @Test
    public void testCreateEmailsContainsTokenPayload()
    {
        String testDomain = "www.test.com";
        String testURI = "/password/validate-token";
        String testQuery = "payload=";
        when(passwordTokenRepository.save(Mockito.any(PasswordResetToken.class))).thenReturn(new PasswordResetToken(user));
        PasswordResetToken token = passwordTokenService.createPasswordResetTokenForUser(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(testDomain);
        request.setRequestURI(testURI);
        request.setQueryString(testQuery + token.getPayload());

        String testUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        String email = emailService.createPasswordResetTokenEmail(request, token);
        assertThat(email, containsString(testUrl));
        System.out.println(email);
    }
}
