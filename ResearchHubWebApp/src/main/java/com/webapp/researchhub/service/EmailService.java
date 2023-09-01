package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.PasswordResetToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;

@Service
@EnableAutoConfiguration
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    // Fetch the senders' username from application properties and assign to variable
    @Value("${spring.mail.username}")
    private String emailSenderName;

    @Value("${spring.mail.send-to-console}")
    private boolean sendMailToConsole;

    /**
     * Send a plain text email to a user asynchronously, so we do not block the application and keep the user
     * waiting.
     *
     * @param to : The recipient.
     * @param subject : Subject of the email
     * @param body : email message.
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(emailSenderName);
        message.setSentDate(new Date());
        if(sendMailToConsole)
        {
            // Print the email to the console.  Useful for development and debugging.

            String emailMessage = "\n====== Email Begin ======\n"
                    + "\tTo: " + message.getTo()[0] + "\n"
                    + "\tFrom: " + message.getFrom() + "\n"
                    + "\tSubject: " + message.getSubject() + "\n"
                    + "\tDate: " + message.getSentDate() + "\n"
                    + "\tMessage: " + message.getText() + "\n"
                    + "====== Email End ======\n";
            System.out.println(emailMessage);
        }
        mailSender.send(message);
    }

    /**
     * Creates an email message and includes the link containing the password reset token.
     * @param request : HttpServerletRequest to get the base url for building the clickable
     *                link back to the server.
     * @param token : Password Reset Token to include in the email.
     * @return : Composed email message as a String.
     */
    public String createPasswordResetTokenEmail(HttpServletRequest request, PasswordResetToken token)
    {
        String baseUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        return "Hello, " + token.getUser().getFirstName() + ".\n\n"
                + "We have received a request to reset the password for your account associated with " + token.getUser().getEmail() + "\n"
                + "No changes have been made to your account and you can reset your password by click the link below: \n\n"
                + baseUrl + "/password/validate-token?payload=" + token.getPayload() + "\n\n"
                + "This link is valid until " + token.getExpiryDate() + "\n\n"
                + "If you did not request this action, you can ignore this email.\n\n"
                + "--- The reversible computation research team.\n";
    }
}
