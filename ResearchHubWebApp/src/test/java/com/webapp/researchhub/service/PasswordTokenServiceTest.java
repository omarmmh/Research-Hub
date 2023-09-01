package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.PasswordResetToken;
import com.webapp.researchhub.repository.PasswordTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordTokenServiceTest {

    MyUser user;

    @BeforeEach
    public void createUser() {
        this.user = new MyUser("foo","foo", "bar", "foo@bar.com", "passwd");
    }

    @InjectMocks
    PasswordTokenService passwordTokenService;
    @Mock
    PasswordTokenRepository passwordTokenRepository;

    /**
     * Testing that a password reset token contains a valid UUID.
     * The UUID is used to uniquely identify a user so that they can reset their password.
     */
    @DisplayName("Test Password Reset Token Has Valid UUID.")
    @Test
    public void testPasswordResetTokenGeneratesUuidPayload() {
        Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        when(passwordTokenRepository.findById(1L)).thenReturn(Optional.of(new PasswordResetToken(user)));
        Optional<PasswordResetToken> token = passwordTokenService.getTokenById(1L);
        token.ifPresent(passwordResetToken ->
                assertTrue(UUID_REGEX.matcher(passwordResetToken.getPayload()).matches()));
    }

    /**
     * Test that we can create a password reset token for a given user.
     */
    @DisplayName("Test password reset token is created.")
    @Test
    public void testCanCreatePasswordResetToken() {
        when(passwordTokenRepository.save(Mockito.any(PasswordResetToken.class))).thenReturn(new PasswordResetToken(user));
        PasswordResetToken token = passwordTokenService.createPasswordResetTokenForUser(user);
        assertFalse(passwordTokenService.isTokenExpired(token));
    }

    /**
     * Test that a password reset token is linked to a user so that we can uniquely identify who the token
     * belongs to.
     */
    @DisplayName("Test password reset token has a relationship with a user.")
    @Test
    public void testPasswordResetTokenIsLinkedToUser()
    {
        when(passwordTokenRepository.save(Mockito.any(PasswordResetToken.class))).thenReturn(new PasswordResetToken(user));
        PasswordResetToken token = passwordTokenService.createPasswordResetTokenForUser(user);
        assertEquals(token.getUser().getId(), user.getId());
    }

    /**
     * Test that we can search the database by a token payload, (a UUID string).  This allows us to identify the token
     * that the uuid payload belongs to.  In turn, that allows us to identify who the token belongs to.
     */
    @DisplayName("Test password reset token can be found by payload")
    @Test
    public void testGetTokenByPayload()
    {
        String payload = UUID.randomUUID().toString();
        when(passwordTokenRepository.findByPayload(payload)).thenReturn(new PasswordResetToken(user));
        PasswordResetToken token = passwordTokenService.getTokenByPayload(payload);
        assertEquals(token.getClass(), PasswordResetToken.class);
    }

    /**
     * Test that a password token can be found in the database by its primary key (ID).
     */
    @DisplayName("Test password reset token can be found by token ID")
    @Test
    public void testGetTokenById()
    {
        Long id = 1L;
        when(passwordTokenRepository.findById(id)).thenReturn(Optional.of(new PasswordResetToken(user)));
        Optional<PasswordResetToken> token = passwordTokenService.getTokenById(id);
        assertTrue(token.isPresent());
    }

    /**
     * Test that all tokens belonging to a user can be deleted from the database.
     */
    @DisplayName("Test user assigned password reset tokens are removed")
    @Test
    public void testCanRevokePasswordResetToken()
    {
        passwordTokenService.createPasswordResetTokenForUser(user);
        passwordTokenService.revokeTokensByUserId(user.getId());
        verify(passwordTokenRepository, times(1)).deleteAllByUserId(user.getId());
    }

    /**
     * Test that a newly generated password reset token is valid.  A valid token must contain an expiry date in the
     * future.
     */
    @DisplayName("Test new password reset token is valid and has not expired")
    @Test
    public void testNewPasswordResetHasNotExpired()
    {
        when(passwordTokenRepository.save(Mockito.any(PasswordResetToken.class))).thenReturn(new PasswordResetToken(user));
        PasswordResetToken token = passwordTokenService.createPasswordResetTokenForUser(user);
        assertFalse(passwordTokenService.isTokenExpired(token));
    }

    /**
     * Test that when a token reaches its expiry date (end of life) it is classed as expired.
     */
    @DisplayName("Test password reset tokens is expired after the specified period of time")
    @Test void testPasswordResetTokenExpiresAfterTimePeriod()
    {
        when(passwordTokenRepository.save(Mockito.any(PasswordResetToken.class))).thenReturn(new PasswordResetToken(user));
        PasswordResetToken token = passwordTokenService.createPasswordResetTokenForUser(user);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, PasswordResetToken.EXPIRY_DURATION_HOURS);
        cal.add(Calendar.MINUTE, 1);
        assertFalse(token.getExpiryDate().after(cal.getTime()));
        assertTrue(token.getExpiryDate().before(cal.getTime()));
    }

}
