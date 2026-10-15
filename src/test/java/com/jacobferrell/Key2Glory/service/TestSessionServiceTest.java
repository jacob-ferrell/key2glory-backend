package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.model.TestSession;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestSessionServiceTest {

    private static TestSession session(long clientStart, long clientEnd, Long serverStart) {
        var session = new TestSession();
        session.restartSession(clientStart);
        session.setEndTime(clientEnd);
        ReflectionTestUtils.setField(session, "serverStartTime", serverStart);
        return session;
    }

    @Test
    void trustsClientDurationWithinTolerance() {
        // Server saw 10.5s (request latency), client measured 10s
        assertEquals(10_000, TestSessionService.getValidatedDuration(session(0, 10_000, 1_000L), 11_500));
    }

    @Test
    void rejectsImplausiblyShortClientDuration() {
        // Client claims 1s but the server saw 20s pass between start and completion
        long duration = TestSessionService.getValidatedDuration(session(0, 1_000, 1_000L), 21_000);
        assertEquals(20_000 - TestSessionService.DURATION_TOLERANCE_MS, duration);
    }

    @Test
    void legacySessionsWithoutServerTimeUseClientDuration() {
        assertEquals(5_000, TestSessionService.getValidatedDuration(session(0, 5_000, null), 999_999));
    }
}
