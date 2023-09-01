package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    MyUser user;

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userService = new UserService(userRepository, passwordEncoder);
        this.user = new MyUser("foo","foo", "bar", "foo@bar.com", "password");
    }

    /**
     * Test that a plain text password is encoded to a secure hash before the user service persists a password
     * to the database.
     */
    @DisplayName("Test user password is encoded to a secure hash")
    @Test
    public void testUserServiceSetsHashedPassword() {
        String plainTextPassword = "password";
        when(userRepository.save(user)).thenReturn(user);
        userService.setSecurePasswordForUser(user, plainTextPassword);
        assertNotSame(user.getPassword(), plainTextPassword);
    }
}
