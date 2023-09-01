package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.PasswordResetToken;
import com.webapp.researchhub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    // Constructor.
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    PasswordTokenService passwordTokenService;
    @Autowired
    EmailService emailService;

    /**
     * Sets a users password based on their user id.
     * The plain text password will be securely encoded
     * before saving to the database.  Only a secure HASH will be saved.
     *
     * @param userId : ID value of the user
     * @param password : Plain text password to be securely hashed.
     * @return : The saved MyUser entity or null
     */
    public MyUser setSecurePasswordForUserWithId(Long userId, String password)
    {
        Optional<MyUser> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent())
        {
            MyUser user = optionalUser.get();
            return setSecurePasswordForUser(user, password);
        }
        return null;
    }

    /**
     * Sets a password for a user.  The plain text password will be securely encoded
     * before saving to the database.  Only a HASH will be saved.
     * @param user : MyUser object
     * @param password : Plain text password to be securely hashed.
     */
    public MyUser setSecurePasswordForUser(MyUser user, String password)
    {
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void sendPasswordResetEmail(HttpServletRequest request, MyUser user)
    {
        PasswordResetToken token = passwordTokenService.createPasswordResetTokenForUser(user);
        String message = emailService.createPasswordResetTokenEmail(request, token);
        emailService.sendEmail(user.getEmail(), "Your Password Reset Link", message);
    }

}
