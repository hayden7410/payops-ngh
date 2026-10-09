package com.payops.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/protected")
    public String protectedEndpoint() {
        return "Authenticated";
    }
    @GetMapping("/api/health")
    public String healthCheck() {
        return "Healthy";
    }
}