package com.jacobferrell.Key2Glory.service;

import com.jacobferrell.Key2Glory.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {
    private final RestTemplate restTemplate;
    @Value("${app.userinfo.endpoint.uri}")
    private String uri;

    @Autowired
    public UserService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }
    public User getUser(HttpServletRequest request) {
        String bearerToken = getTokenFromRequest(request);
        return fetchUserInfo(bearerToken);
    }
    private User fetchUserInfo(String bearerToken) {
        if (bearerToken == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).headers(headers).build();
        try {
            ResponseEntity<User> response = restTemplate.exchange(requestEntity, User.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader;
        }
        return null;
    }
}
