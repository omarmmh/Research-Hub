package com.webapp.researchhub.service;

import com.webapp.researchhub.repository.PasswordTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@Transactional
public class ScheduledTaskService {
    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    /**
     * Deletes all expired password reset tokens from the database at the specified time interval.
     */
    @Scheduled(cron = "${purge.cron.expired-password-reset-tokens}")
    public void purgeExpiredPasswordResetTokens() {
        final Calendar cal = Calendar.getInstance();
        Date dateNow = cal.getTime();
        passwordTokenRepository.deleteAllByExpiryDateBefore(dateNow);
    }
}
