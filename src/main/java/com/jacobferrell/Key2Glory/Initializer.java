package com.jacobferrell.Key2Glory;

import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.model.TypingTestType;
import com.jacobferrell.Key2Glory.repository.TypingTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {
    @Autowired
    private TypingTestRepository typingTestRepository;
    @Override
    public void run(String... args) throws Exception {
        if (!typingTestRepository.findAll().isEmpty()) { return; }
        var test1 = new TypingTest("The quick red fox jumps over the lazy dog with lightning speed, creating a spectacle of elegance and skill.", TypingTestType.GENERAL);
        var test2 = new TypingTest("In the year 2024, the global population is estimated to reach 8.05 billion. The average human lifespan has increased to 73.4 years, while the world's gross domestic product (GDP) is projected to be around $105 trillion. By 2030, over 75% of the world's population is expected to have access to the internet. In a recent study, 64% of respondents said they spend more than 5 hours per day on digital devices. Additionally, the global smartphone market is expected to sell around 1.5 billion units annually, with an average price of $325 per device.", TypingTestType.NUMBERS);
        var test3 = new TypingTest("@Home! The total cost of groceries is $50.75, and I found some great deals: 20% off on fresh fruits, and 10% off on cleaning supplies. Don't forget to check out the new arrivals in the #Sale section! To celebrate, we’ll have pizza (with extra cheese!) and dessert: chocolate cake, cookies, & ice cream. Remember, life is too short to skip dessert! So, let’s enjoy every delicious bite while we stay safe and healthy!", TypingTestType.SPECIAL_CHARACTERS);
        typingTestRepository.save(test1);
        typingTestRepository.save(test2);
        typingTestRepository.save(test3);
    }
}
