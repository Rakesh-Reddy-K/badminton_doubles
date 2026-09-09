package com.example.badminton.controller;

import com.example.badminton.dto.*;
import com.example.badminton.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(statisticsService.getDashboard());
    }

    @GetMapping("/day")
    public ResponseEntity<DashboardResponse> getDayDashboard(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(statisticsService.getDayDashboard(date));
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerStatistics>> getPlayerStatistics() {
        return ResponseEntity.ok(statisticsService.getPlayerStatistics());
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<PlayerStatistics> getPlayerStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(statisticsService.getPlayerStatistics(id));
    }

    @GetMapping("/players/ranking")
    public ResponseEntity<List<PlayerStatistics>> getRankings(
            @RequestParam(defaultValue = "0") int minMatches) {
        return ResponseEntity.ok(statisticsService.getRankings(minMatches));
    }

    @GetMapping("/pairs")
    public ResponseEntity<List<PairStatistics>> getPairStatistics(
            @RequestParam(defaultValue = "0") int minMatches) {
        return ResponseEntity.ok(statisticsService.getPairStatistics(minMatches));
    }
}
