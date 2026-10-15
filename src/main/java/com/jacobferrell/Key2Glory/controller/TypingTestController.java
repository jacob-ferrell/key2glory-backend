package com.jacobferrell.Key2Glory.controller;

import com.jacobferrell.Key2Glory.dto.TypingTestDTO;
import com.jacobferrell.Key2Glory.dto.TypingTestRequest;
import com.jacobferrell.Key2Glory.dto.TypingTestSortAndFilterDTO;
import com.jacobferrell.Key2Glory.model.MineFilterTypes;
import com.jacobferrell.Key2Glory.model.SortTypes;
import com.jacobferrell.Key2Glory.model.OrderTypes;
import com.jacobferrell.Key2Glory.model.TypingTestType;
import com.jacobferrell.Key2Glory.service.TypingTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path= "api")
public class TypingTestController {
    @Autowired
    private TypingTestService service;
    @GetMapping("/public/typing-test/{id}")
    public ResponseEntity<TypingTestDTO> getTypingTest(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(service.getTest(id, jwt));
    }
    @GetMapping("/public/typing-test/random")
    public ResponseEntity<TypingTestDTO> getRandomTypingTest(
            @RequestParam(name = "type", defaultValue = "GENERAL") TypingTestType type,
            @RequestParam(name = "exclude", required = false) Long exclude,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok().body(service.getRandomTest(type, exclude, jwt));
    }
    @GetMapping("/public/typing-test/{id}/text")
    public ResponseEntity<String> getTypingTestText(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getText(id));
    }
    @GetMapping("/public/typing-test")
    public ResponseEntity<Page<TypingTestDTO>> getTypingTests(
        @RequestParam(name = "creators", required = false) String creators,
        @RequestParam(name = "types", required = false) String types,
        @RequestParam(name = "sort", required = false) SortTypes sort,
        @RequestParam(name = "order", required = false) OrderTypes order,
        @RequestParam(name = "mine", required = false) MineFilterTypes mine,
        @RequestParam(name = "q", required = false) String query,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @AuthenticationPrincipal Jwt jwt
    ) {
        String username = jwt == null ? null : jwt.getClaim("username");
        var params = new TypingTestSortAndFilterDTO(creators, types, sort, order, mine, query, username, page, size);
        return ResponseEntity.ok().body(service.getTests(params, username));
    }
    @PostMapping("/private/typing-test")
    public ResponseEntity<?> createTypingTest(
            @RequestBody TypingTestRequest typingTest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.createTypingTest(jwt, typingTest);
    }
    @PutMapping("/private/typing-test/{id}")
    public ResponseEntity<?> updateTypingTest(
            @PathVariable Long id,
            @RequestBody TypingTestRequest typingTest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.updateTypingTest(id, typingTest, jwt);
    }
}
