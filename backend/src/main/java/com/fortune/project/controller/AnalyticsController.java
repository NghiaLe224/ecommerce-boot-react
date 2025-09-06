package com.fortune.project.controller;

import com.fortune.project.dto.response.analytics.AnalyticsResponse;
import com.fortune.project.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics(){
        AnalyticsResponse res = analyticsService.getAnalytics();
        return ResponseEntity.ok(res);
    }
}
