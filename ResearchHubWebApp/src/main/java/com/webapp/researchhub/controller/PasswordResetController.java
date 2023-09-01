package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.PasswordResetToken;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.service.EmailService;
import com.webapp.researchhub.service.PasswordTokenService;
import com.webapp.researchhub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/password")
public class PasswordResetController {
    @Autowired
    UserRepository repo;
    @Autowired
    PasswordTokenService passwordTokenService;
    @Autowired
    EmailService emailService;
    @Autowired
    UserService userService;

    // If send-to-console is true, emails will be printed to terminal instead.
    // Handy for quick debugging during development.
    @Value("${spring.mail.send-to-console}")
    private Boolean emailToConsole;

    /**
     * Mapping for the password reset form
     *
     * @return : A password request form
     */
    @GetMapping("/reset-form")
    public String resetPassword() {
        return "password/reset-form";
    }

    /**
     * POST handler for the reset form.
     * Check that the email address the user has input actually exists in the database.  If a matching email address
     * is found a password reset token is generated.  An email is created and sent containing the token and reset link.
     *
     * @param request : The HTTP request.
     * @param email   : email address the user has input in the html form.
     * @return : password success view.
     */
    @PostMapping("/reset")
    public String handleResetForm(HttpServletRequest request, @RequestParam(value = "email") String email) {
        MyUser user = repo.findByEmail(email);
        if (user != null) {
            userService.sendPasswordResetEmail(request, user);
        }
        return "password/reset-success";
    }

    /**
     * Mapping for when the user clicks the Password Reset link in their email.
     * The payload (a UUID) is extracted from the request params http://example.com?payload=[our token]
     * The token containing our uuid payload is loaded from the database and checked to see if it is still in date.  Ie,
     * it has not gone past its specified expiry date.
     * If a token is valid, the user will be sent to the password change form to set a new password.
     *
     * @param payload : The uuid payload from the users link to reset their password.
     * @param model   : Model used to send the user id associated with the token to the view.
     * @return : password change form if token is valid, else user will be routed back to the sign in form.
     */
    @GetMapping("/validate-token")
    public String validateUserToken(@RequestParam("payload") String payload, Model model) {
        PasswordResetToken token = passwordTokenService.getTokenByPayload(payload);
        if (token == null || passwordTokenService.isTokenExpired(token)) {
            // Token is invalid or expired, go back to sign in page.
            return "security/login-form";
        } else {
            // Token is Valid
            model.addAttribute("payload", token.getPayload());
            return "password/change-password-form";
        }
    }

    /**
     * Handles the users request to set a new password that has been input into the password change form.
     *
     * @param password1 : The new user password.
     * @param password2 : Confirmation of the new user password
     * @param payload    : String payload contained in the Password Reset Token.
     * @return : Change Password Success View, or change password form if the passwords do not match.
     */
    @PostMapping("/change-password")
    public String changeUserPassword(
            @RequestParam("password1") String password1,
            @RequestParam("password2") String password2,
            @RequestParam("payload") String payload,
            Model model) {
        if (password1.equals(password2) & (password1.length() > 0)) {
            Long userId = passwordTokenService.getUserByPayload(payload).getId();
            userService.setSecurePasswordForUserWithId(userId, password1);
            passwordTokenService.revokeTokensByUserId(userId);
        } else {
            model.addAttribute("assert_failed", true);
            model.addAttribute("payload", payload);
            return "password/change-password-form";
        }
        return "password/change-password-success";
    }
}
