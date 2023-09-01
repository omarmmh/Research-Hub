package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.PasswordResetToken;
import com.webapp.researchhub.repository.PasswordTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PasswordTokenService {

    @Autowired
    private PasswordTokenRepository tokenRepository;

    public List<PasswordResetToken> findAllByUserId(Long id) { return tokenRepository.findAllByUserId(id); }

    /**
     * Check if the current token has expired by checking that the current time is before the expiry date.
     * @return Boolean (true if expired, false if token is still valid).
     */
    public boolean isTokenExpired(PasswordResetToken token)
    {
        final Calendar cal = Calendar.getInstance();
        return token.getExpiryDate().before(cal.getTime());
    }

    /**
     * Create a password reset token that is unique to identify a user.
     * The token is saved to the database.
     *
     * @param user : MyUser instance.
     * @return The generated password reset token.
     */
    public PasswordResetToken createPasswordResetTokenForUser(MyUser user) {
        PasswordResetToken token = new PasswordResetToken(user);
        return tokenRepository.save(token);
    }

    /**
     * Find a token based on its payload string.
     * @param payload : The payload string contained in the token.
     * @return : Token
     */
    public PasswordResetToken getTokenByPayload(String payload)
    {
        return tokenRepository.findByPayload(payload);
    }

    /**
     * Get a token by its ID
     * @param id : ID of token
     * @return : Password Reset Token
     */
    public Optional<PasswordResetToken> getTokenById(Long id) { return tokenRepository.findById(id); }

    /**
     * Revoke a token using its ID and delete from database.
     * @param userId : Token ID
     * @return : True if successfully deleted from the database.  False in other cases such as a database problem or
     * the token was not found so could not be deleted.
     */
    public void revokeTokensByUserId(Long userId) { tokenRepository.deleteAllByUserId(userId); }

    /**
     * Get a User by Token Payload
     * @param payload : String payload contained in a Password Reset Token.
     * @return : MyUser
     */
    public MyUser getUserByPayload(String payload)
    {
        return tokenRepository.findByPayload(payload).getUser();
    }
}