package com.webapp.researchhub.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = "purge.cron.expired-password-reset-tokens=*/1 * * * * *")
public class ScheduledTaskServiceTest {

    @SpyBean
    private ScheduledTaskService scheduledTaskService;

    /**
     * Test the task scheduler is triggered to delete all expired password reset tokens.
     */
    @DisplayName("Test task scheduler triggers delete all expired password reset tokens.")
    @Test
    public void testPurgeExpiredPasswordResetTokensIsTriggered()
    {
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(
                () -> verify(scheduledTaskService, atLeastOnce()).purgeExpiredPasswordResetTokens());
    }
}
