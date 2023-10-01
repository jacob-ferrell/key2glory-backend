package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.model.TestSession;
import com.jacobferrell.Key2Glory.service.TestSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="api/private/typing-test")
public class TestSessionController {
    @Autowired
    private TestSessionService service;
    @GetMapping("/{id}/session")
    public ResponseEntity<?> getTestSession(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.getTestSession(id, jwt);
    }
    @PostMapping("/{id}/session")
    public ResponseEntity<?> createTestSession(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @RequestBody TestSession session) {
        return service.createTestSession(id, jwt, session);
    }

    @PutMapping("/{id}/session")
    public ResponseEntity<?> completeTestSession(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @RequestBody TestSession session) {
        return service.completeSession(id, jwt, session);
    }
}
