package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.model.GeneralStats;
import com.jacobferrell.Key2Glory.repository.ScoreRepository;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class GeneralStatsService {
    @Autowired
    TypingTestRepository testRepository;

    @Autowired
    ScoreRepository scoreRepository;

    public GeneralStats getGeneralStats(Jwt jwt) {
        String username = jwt.getClaim("username");

        Double averageWPM = scoreRepository.findAverageWpmByUsername(username);
        Double highestWPM = scoreRepository.findMaxWpmByUsername(username);
        Double averageAccuracy = scoreRepository.findAverageAccuracyByUsername(username);

        // Aggregates are null if the user hasn't taken any tests
        return new GeneralStats(
                round(averageWPM),
                round(highestWPM),
                round(averageAccuracy),
                (int) scoreRepository.countByUsername(username),
                (int) testRepository.countByCreatedBy(username));
    }

    private static double round(Double value) {
        return value == null ? 0.0 : Math.round(value * 100) / 100.0;
    }
}
