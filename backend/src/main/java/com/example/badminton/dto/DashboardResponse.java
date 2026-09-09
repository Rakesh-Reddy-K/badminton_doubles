package com.example.badminton.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private long totalMatches;
    private long totalPlayers;
    private long matchesToday;
    private List<MatchResponse> recentMatches;
    private List<PlayerStatistics> mostActivePlayers;
    private List<PlayerStatistics> topPlayers;
}
