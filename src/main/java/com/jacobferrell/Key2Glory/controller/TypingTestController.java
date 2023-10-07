package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.dto.TypingTestDTO;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.service.TypingTestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(path= "api")
@CrossOrigin(origins = "http://localhost:5174", allowedHeaders = "*")
public class TypingTestController {
    @Autowired
    private TypingTestService service;
    @GetMapping("/public/typing-test/{id}")
    public ResponseEntity<TypingTestDTO> getTypingTest(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(service.getTest(id, jwt));
    }
    @GetMapping("/public/typing-test/{id}/text")
    public ResponseEntity<String> getTypingTestText(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getText(id));
    }
    @PostMapping("/private/typing-test")
    public ResponseEntity<?> createTypingTest(
            @RequestBody TypingTest typingTest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.createTypingTest(jwt, typingTest);
    }
    @PutMapping("/private/typing-test/{id}")
    public ResponseEntity<?> updateTypingTest(
            @PathVariable Long id,
            @RequestBody TypingTest typingTest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.updateTypingTest(id, typingTest, jwt);
    }
}
