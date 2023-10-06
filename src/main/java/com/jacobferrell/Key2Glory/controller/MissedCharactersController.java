package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.service.MissedCharactersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path= "api/private/missed-characters")
public class MissedCharactersController {
    @Autowired
    private MissedCharactersService service;
    @GetMapping
    public ResponseEntity<?> getMissedCharacters(@AuthenticationPrincipal Jwt jwt) {
        return service.getMissedCharacters(jwt);
    }
}
