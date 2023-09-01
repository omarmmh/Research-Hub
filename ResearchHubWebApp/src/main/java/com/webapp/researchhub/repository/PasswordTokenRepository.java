package com.webapp.researchhub.repository;

import com.webapp.researchhub.domain.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    PasswordResetToken findByPayload(String payload);
    List<PasswordResetToken> deleteAllByExpiryDateBefore(Date date);
    List<PasswordResetToken> findAllByUserId(Long id);
    void deleteAllByUserId(Long id);
}
