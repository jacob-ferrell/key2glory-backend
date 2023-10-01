package com.jacobferrell.Key2Glory;

import com.jacobferrell.Key2Glory.model.TypingTest;
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
        var test1 = new TypingTest("The quick red fox jumps over the lazy dog with lightning speed, creating a spectacle of elegance and skill.");
        typingTestRepository.save(test1);
    }
}
