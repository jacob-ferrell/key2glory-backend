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
        var test1 = new TypingTest("The quick red fox jumps over the lazy dog with lightning speed, creating a spectacle of elegance and skill.", TypingTestType.GENERAL);
        var test2 = new TypingTest("asdf", TypingTestType.NUMBERS);
        var test3 = new TypingTest("Typing is a skill that many people use every day. Being able to type quickly and accurately can make a big difference in productivity. Whether you are writing a paper, sending an email, or working on a project, typing skills can save you time. Practicing your typing regularly is one of the best ways to improve your speed. It is also important to focus on accuracy as much as speed. Typing quickly is useful, but it is even better when you can avoid making mistakes. Keeping your hands in the right position and using all of your fingers is key to developing good typing habits. " + 
                        "With consistent practice, anyone can improve their typing skills. Start by typing slowly and focusing on hitting the right keys. Gradually, as you become more comfortable, you can increase your speed. Remember to keep your posture correct and take breaks to avoid strain. Typing tests like this one are a great way to measure progress and see how fast you can go. The more you practice, the better you will become at typing smoothly and efficiently. Keep challenging yourself to get faster while maintaining accuracy, and soon you will see a big improvement.", TypingTestType.GENERAL);
        typingTestRepository.save(test1);
        typingTestRepository.save(test2);
        typingTestRepository.save(test3);
        for (int i = 0; i < 20; i++) {
           var test = new TypingTest("asdf", TypingTestType.NUMBERS);
           typingTestRepository.save(test);
        }
    }
}
