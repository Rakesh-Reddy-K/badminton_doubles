package com.example.badminton.dto;

import lombok.Data;

@Data
public class PlayerStatistics {
    private Long playerId;
    private String playerName;
    private long matchesPlayed;
    private long wins;
    private long losses;
    private double winPercentage;

    public double getWinPercentage() {
        if (matchesPlayed == 0) return 0.0;
        return Math.round(((double) wins / matchesPlayed) * 10000.0) / 100.0;
    }
}
