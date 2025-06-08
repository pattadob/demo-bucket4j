package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    int responseCounter = 0;

    @GetMapping("/api/data")
    public ResponseEntity<String> getData(HttpServletRequest request){
//        return ResponseEntity.ok("Data retrieved successfully");
        return ResponseEntity.ok(String.format("Data retrieved successfully {%d}", ++responseCounter));
    }

    @GetMapping("/public/news")
    public ResponseEntity<String> getNews(HttpServletRequest request){
//        return ResponseEntity.ok("Data retrieved successfully");
        return ResponseEntity.ok(String.format("Data retrieved successfully {%d}", ++responseCounter));
    }
}
