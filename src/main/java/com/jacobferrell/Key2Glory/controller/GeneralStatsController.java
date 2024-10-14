package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.model.GeneralStats;
import com.jacobferrell.Key2Glory.service.GeneralStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;


@RestController
@RequestMapping(path= "api")
public class GeneralStatsController {
    @Autowired
    GeneralStatsService service;

    @GetMapping("/private/general-stats")
    public ResponseEntity<GeneralStats> getGeneralStats(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(service.getGeneralStats(jwt));
    }
}
