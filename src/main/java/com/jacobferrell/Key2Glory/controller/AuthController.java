package com.jacobferrell.Key2Glory.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path= "api")
@CrossOrigin(origins = "http://localhost:5174", allowedHeaders = "*")
public class AuthController {
    @GetMapping("/public/hello")
    public String hello() {
        return "Hello!";
    }

    @GetMapping("/private/secured")
    public String secured() {
        return "Secured!";
    }
}
