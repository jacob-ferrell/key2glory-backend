package com.jacobferrell.Key2Glory;

import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.model.TypingTestType;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Initializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    // The first three keep their original order so they keep ids 1-3 on a fresh database
    private static final List<TypingTest> BUILT_IN_TESTS = List.of(
            new TypingTest("The quick red fox jumps over the lazy dog with lightning speed, creating a spectacle of elegance and skill.", TypingTestType.GENERAL),
            new TypingTest("In the year 2024, the global population is estimated to reach 8.05 billion. The average human lifespan has increased to 73.4 years, while the world's gross domestic product (GDP) is projected to be around $105 trillion. By 2030, over 75% of the world's population is expected to have access to the internet. In a recent study, 64% of respondents said they spend more than 5 hours per day on digital devices. Additionally, the global smartphone market is expected to sell around 1.5 billion units annually, with an average price of $325 per device.", TypingTestType.NUMBERS),
            new TypingTest("@Home! The total cost of groceries is $50.75, and I found some great deals: 20% off on fresh fruits, and 10% off on cleaning supplies. Don't forget to check out the new arrivals in the #Sale section! To celebrate, we'll have pizza (with extra cheese!) and dessert: chocolate cake, cookies, & ice cream. Remember, life is too short to skip dessert! So, let's enjoy every delicious bite while we stay safe and healthy!", TypingTestType.SPECIAL_CHARACTERS),

            new TypingTest("Every morning the old lighthouse keeper climbed the spiral stairs, wiped the salt from the great lens, and watched the fishing boats drift out past the harbor wall. He knew each captain by the color of their sails and could tell the weather by the way the gulls circled overhead.", TypingTestType.GENERAL),
            new TypingTest("Learning to type quickly is less about moving your fingers fast and more about keeping them relaxed. Rest them lightly on the home row, look at the screen instead of the keys, and let accuracy come first. Speed follows naturally once the motions become automatic.", TypingTestType.GENERAL),
            new TypingTest("The library was quiet except for the soft rustle of pages and the distant hum of the heating pipes. A student in the corner had fallen asleep on a stack of history books, while the librarian sorted returned novels into neat piles beside the front desk.", TypingTestType.GENERAL),
            new TypingTest("Good software is written twice: once to make it work, and again to make it clear. The second version is the one your teammates will thank you for, because code is read far more often than it is written.", TypingTestType.GENERAL),

            new TypingTest("The marathon started at 7:30 a.m. with 4,218 runners. The winner crossed the line in 2 hours, 9 minutes, and 41 seconds, while the last finisher arrived 6 hours and 12 minutes later. Volunteers handed out 18,500 cups of water and 3,200 bananas along the 26.2 mile course.", TypingTestType.NUMBERS),
            new TypingTest("Order #48213 shipped on 03/14 with 12 boxes weighing 7.5 kg each. The invoice listed 36 units at $19.99, 8 units at $104.50, and a $25 delivery fee, for a total of $1,580.64 due within 30 days.", TypingTestType.NUMBERS),
            new TypingTest("Room 1207 is on the 12th floor, 3 doors past the elevator. Check-in begins at 15:00 and checkout is at 11:00. Breakfast is served from 6:30 to 10:00, and the pool is open 24 hours a day, 7 days a week.", TypingTestType.NUMBERS),

            new TypingTest("Email support@example.com or call (555) 013-2468 ext. 9. Use code SAVE-20% at checkout; offer valid 1/1 - 3/31 only! Questions? Visit https://example.com/help#faq & we'll reply within 24-48 hrs.", TypingTestType.SPECIAL_CHARACTERS),
            new TypingTest("if (score >= 90 && !isLate) { grade = 'A'; } else if (score > 80 || bonus) { grade = \"B+\"; } // TODO: handle [edge] cases & <empty> input", TypingTestType.SPECIAL_CHARACTERS),
            new TypingTest("Recipe: mix 2 cups flour + 1/2 tsp salt; add 3 eggs (beaten) & stir. Bake @ 350*F for ~25 min. Yields 12 muffins = 1 dozen! Tip: grease the pan first, or they'll stick... #baking", TypingTestType.SPECIAL_CHARACTERS)
    );

    @Autowired
    private TypingTestRepository typingTestRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        normalizeExistingTests();
        migrateLegacyFavorites();
        seedBuiltInTests();
    }

    /** Replaces untypeable characters (curly quotes, en dashes...) in tests saved before text was normalized. */
    private void normalizeExistingTests() {
        var changed = typingTestRepository.findAll().stream()
                .filter(TypingTest::normalizeStoredText)
                .toList();
        if (!changed.isEmpty()) {
            typingTestRepository.saveAll(changed);
            log.info("Normalized text of {} typing test(s)", changed.size());
        }
    }

    private void seedBuiltInTests() {
        for (TypingTest test : BUILT_IN_TESTS) {
            if (!typingTestRepository.existsByText(test.getText())) {
                typingTestRepository.save(new TypingTest(test.getText(), test.getType()));
            }
        }
    }

    /**
     * Favorites used to be stored as a typing_test.favorites_id column, which only allowed a test to be
     * in one user's favorites. Copy those rows into the join table once, then clear the old column.
     */
    private void migrateLegacyFavorites() {
        try {
            Integer legacyColumns = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns " +
                            "WHERE LOWER(table_name) = 'typing_test' AND LOWER(column_name) = 'favorites_id'",
                    Integer.class);
            if (legacyColumns == null || legacyColumns == 0) {
                return;
            }
            int migrated = jdbcTemplate.update(
                    "INSERT INTO favorites_typing_tests (favorites_id, typing_test_id) " +
                            "SELECT t.favorites_id, t.id FROM typing_test t WHERE t.favorites_id IS NOT NULL " +
                            "AND NOT EXISTS (SELECT 1 FROM favorites_typing_tests f " +
                            "WHERE f.favorites_id = t.favorites_id AND f.typing_test_id = t.id)");
            jdbcTemplate.update("UPDATE typing_test SET favorites_id = NULL WHERE favorites_id IS NOT NULL");
            if (migrated > 0) {
                log.info("Migrated {} legacy favorite(s)", migrated);
            }
        } catch (DataAccessException e) {
            log.warn("Skipping legacy favorites migration", e);
        }
    }
}
