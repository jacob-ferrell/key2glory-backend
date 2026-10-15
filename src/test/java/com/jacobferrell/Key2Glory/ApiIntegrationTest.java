package com.jacobferrell.Key2Glory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private JwtDecoder jwtDecoder;

    private static RequestPostProcessor user(String username) {
        return jwt().jwt(j -> j.claim("username", username));
    }

    private static String uniqueUser() {
        return "user-" + UUID.randomUUID();
    }

    private JsonNode perform(MockHttpServletRequestBuilder request, int expectedStatus) throws Exception {
        var body = mvc.perform(request).andExpect(status().is(expectedStatus)).andReturn().getResponse().getContentAsString();
        return body.isEmpty() ? null : mapper.readTree(body);
    }

    private long createTest(String username, String text, String type) throws Exception {
        var body = mapper.writeValueAsString(Map.of("text", text, "type", type));
        return perform(post("/api/private/typing-test").with(user(username))
                .contentType(MediaType.APPLICATION_JSON).content(body), 201).get("id").asLong();
    }

    private void startSession(String username, long testId, long startTime) throws Exception {
        mvc.perform(post("/api/private/typing-test/" + testId + "/session").with(user(username))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"startTime\":" + startTime + "}"))
                .andExpect(result -> assertTrue(result.getResponse().getStatus() == 200 || result.getResponse().getStatus() == 201));
    }

    private JsonNode endSession(String username, long testId, long endTime, String missedJson, int expectedStatus) throws Exception {
        return perform(put("/api/private/typing-test/" + testId + "/session").with(user(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"endTime\":" + endTime + ",\"missedCharacters\":" + missedJson + "}"), expectedStatus);
    }

    private JsonNode play(String username, long testId, long durationMs, String missedJson) throws Exception {
        long now = System.currentTimeMillis();
        startSession(username, testId, now - durationMs);
        return endSession(username, testId, now, missedJson, 200);
    }

    @Test
    void listReturnsPageDirectlyWithBuiltInTests() throws Exception {
        var page = perform(get("/api/public/typing-test?size=50"), 200);
        assertTrue(page.get("content").isArray());
        assertTrue(page.get("totalElements").asInt() >= 13);
        assertNull(page.get("body"), "page should not be wrapped in a ResponseEntity");
    }

    @Test
    void invalidParametersAreBadRequests() throws Exception {
        perform(get("/api/public/typing-test?types=BOGUS"), 400);
        perform(get("/api/public/typing-test?sort=BOGUS"), 400);
        perform(get("/api/public/typing-test/random?type=BOGUS"), 400);
    }

    @Test
    void missingResourcesAreNotFound() throws Exception {
        var error = perform(get("/api/public/typing-test/987654321"), 404);
        assertEquals("Typing test not found", error.get("message").asText());
    }

    @Test
    void privateEndpointsRequireAuthentication() throws Exception {
        perform(get("/api/private/general-stats"), 401);
    }

    @Test
    void clientsCannotPostArbitraryScores() throws Exception {
        perform(post("/api/private/typing-test/1/scores").with(user(uniqueUser()))
                .contentType(MediaType.APPLICATION_JSON).content("{\"wpm\":9999}"), 405);
    }

    @Test
    void userCreatedTestsCanBePlayedAndScored() throws Exception {
        String alice = uniqueUser();
        long id = createTest(alice, "one two three four five six seven eight nine ten", "GENERAL");

        var test = perform(get("/api/public/typing-test/" + id), 200);
        assertEquals(10, test.get("wordsCount").asInt());
        assertEquals(alice, test.get("createdBy").asText());

        var score = play(alice, id, 6_000, "[\"a\",\"b\",\"c\"]");
        assertEquals(100.0, score.get("wpm").asDouble(), 0.5);
        assertEquals(93.75, score.get("accuracy").asDouble());

        // The same run can't be submitted twice
        endSession(alice, id, System.currentTimeMillis(), "[]", 409);

        test = perform(get("/api/public/typing-test/" + id).with(user(alice)), 200);
        assertEquals(1, test.get("scoresCount").asInt());
        assertEquals(score.get("id").asLong(), test.get("currentUserHighScore").get("id").asLong());

        var stats = perform(get("/api/private/general-stats").with(user(alice)), 200);
        assertEquals(1, stats.get("testsCompleted").asInt());
        assertEquals(1, stats.get("testsContributed").asInt());

        var missed = perform(get("/api/private/missed-characters").with(user(alice)), 200);
        assertEquals(1, missed.get("characterCounts").get("a").asInt());

        var recent = perform(get("/api/private/scores/recent").with(user(alice)), 200);
        assertEquals(1, recent.size());
    }

    @Test
    void createRejectsInvalidLengthAndIgnoresClientControlledFields() throws Exception {
        String bob = uniqueUser();
        perform(post("/api/private/typing-test").with(user(bob))
                .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"short\",\"type\":\"GENERAL\"}"), 400);

        var created = perform(post("/api/private/typing-test").with(user(bob))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"a perfectly reasonable typing test text\",\"type\":\"NUMBERS\",\"rating\":5,\"createdBy\":\"someone-else\"}"), 201);
        assertEquals(bob, created.get("createdBy").asText());
        assertTrue(created.get("rating").isNull());
    }

    @Test
    void favoritesAreIndependentPerUser() throws Exception {
        String alice = uniqueUser(), bob = uniqueUser();
        long id = createTest(alice, "favorites should work for more than one user at a time", "GENERAL");

        perform(post("/api/private/favorites/" + id).with(user(alice)), 200);
        perform(post("/api/private/favorites/" + id).with(user(bob)), 200);
        perform(post("/api/private/favorites/" + id).with(user(bob)), 200); // idempotent

        assertEquals(1, perform(get("/api/private/favorites").with(user(alice)), 200).size());
        assertEquals(1, perform(get("/api/private/favorites").with(user(bob)), 200).size());
        assertTrue(perform(get("/api/public/typing-test/" + id).with(user(alice)), 200).get("favorited").asBoolean());

        var mine = perform(get("/api/public/typing-test?mine=FAVORITES").with(user(alice)), 200);
        assertEquals(1, mine.get("totalElements").asInt());
        assertEquals(id, mine.get("content").get(0).get("id").asLong());

        perform(delete("/api/private/favorites/" + id).with(user(alice)), 200);
        assertEquals(0, perform(get("/api/private/favorites").with(user(alice)), 200).size());
        assertEquals(1, perform(get("/api/private/favorites").with(user(bob)), 200).size());

        // Signed out, "mine" filters have nothing to match
        assertEquals(0, perform(get("/api/public/typing-test?mine=FAVORITES"), 200).get("totalElements").asInt());
    }

    @Test
    void updatingARatingRecomputesTheAverage() throws Exception {
        String alice = uniqueUser(), bob = uniqueUser();
        long id = createTest(alice, "a test that people will have opinions about", "GENERAL");

        perform(post("/api/private/typing-test/" + id + "/rating").with(user(alice))
                .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":5}"), 201);
        perform(post("/api/private/typing-test/" + id + "/rating").with(user(bob))
                .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":3}"), 201);
        perform(post("/api/private/typing-test/" + id + "/rating").with(user(bob))
                .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":1}"), 200);
        perform(post("/api/private/typing-test/" + id + "/rating").with(user(bob))
                .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":9}"), 400);

        var test = perform(get("/api/public/typing-test/" + id).with(user(bob)), 200);
        assertEquals(3.0, test.get("rating").asDouble());
        assertEquals(2, test.get("ratings").asInt());
        assertEquals(1.0, test.get("currentUserRating").asDouble());
    }

    @Test
    void sortsByLengthAndSearchesText() throws Exception {
        var page = perform(get("/api/public/typing-test?sort=LENGTH&order=ASC&size=50"), 200);
        long previous = -1;
        for (JsonNode test : page.get("content")) {
            long length = test.get("text").asText().length();
            assertTrue(length >= previous, "tests should be sorted by length");
            previous = length;
        }

        var search = perform(get("/api/public/typing-test?q=LIGHTHOUSE"), 200);
        assertEquals(1, search.get("totalElements").asInt());
        // LIKE wildcards in the query are treated literally
        assertEquals(0, perform(get("/api/public/typing-test?q=%25%25"), 200).get("totalElements").asInt());
    }

    @Test
    void randomTestRespectsTypeAndExclusion() throws Exception {
        for (int i = 0; i < 10; i++) {
            var test = perform(get("/api/public/typing-test/random?type=NUMBERS&exclude=2"), 200);
            assertEquals("NUMBERS", test.get("type").asText());
            assertNotEquals(2, test.get("id").asLong());
        }
    }

    @Test
    void leaderboardsRankBestRunPerUser() throws Exception {
        String fast = uniqueUser(), slow = uniqueUser();
        long id = createTest(fast, "one two three four five six seven eight nine ten", "GENERAL");
        for (int i = 0; i < 3; i++) {
            play(fast, id, 6_000, "[]");
            play(slow, id, 12_000, "[]");
        }

        mvc.perform(get("/api/public/typing-test/" + id + "/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value(fast))
                .andExpect(jsonPath("$[1].username").value(slow));

        var leaderboard = perform(get("/api/public/leaderboard?limit=100"), 200);
        int fastRank = -1, slowRank = -1;
        for (int i = 0; i < leaderboard.size(); i++) {
            String name = leaderboard.get(i).get("username").asText();
            if (name.equals(fast)) fastRank = i;
            if (name.equals(slow)) slowRank = i;
        }
        assertTrue(fastRank >= 0 && slowRank > fastRank);
        assertEquals(3, leaderboard.get(fastRank).get("testsCompleted").asInt());
    }
}
