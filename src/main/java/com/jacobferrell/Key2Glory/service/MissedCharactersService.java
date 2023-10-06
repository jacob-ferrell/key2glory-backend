package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.repository.MissedCharactersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class MissedCharactersService {
    @Autowired
    MissedCharactersRepository repository;
    public ResponseEntity<?> getMissedCharacters(Jwt jwt) {
        String username = jwt.getClaim("username");
        var missedCharacters = repository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok().body(missedCharacters);
    }
}
