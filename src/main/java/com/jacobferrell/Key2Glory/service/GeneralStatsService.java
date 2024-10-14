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

        // Handle potential null or empty cases with default values
        Double averageWPM = scoreRepository.findAverageWpmByUsername(username);
        Double highestWPM = scoreRepository.findMaxWpmByUsername(username);
        Double averageAccuracy = scoreRepository.findAverageAccuracyByUsername(username);

        // Use 0 if the user hasn't taken any tests
        averageWPM = (averageWPM != null) ? averageWPM : 0.0;
        highestWPM = (highestWPM != null) ? highestWPM : 0.0;
        averageAccuracy = (averageAccuracy != null) ? averageAccuracy : 0.0;

        // Check the number of tests completed and contributed, default to 0 if none
        int testsCompleted = scoreRepository.findByUser(username).size();
        int testsContributed = testRepository.findByCreator(username).size();

        // Return the GeneralStats object with the calculated or default values
        return new GeneralStats(averageWPM, highestWPM, averageAccuracy, testsCompleted, testsContributed);
    }
}
