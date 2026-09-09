package com.example.badminton.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MatchResponse {
    private Long id;
    private LocalDateTime playedAt;
    private Integer sideAScore;
    private Integer sideBScore;
    private String winnerSide;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PlayerRef> sideAPlayers;
    private List<PlayerRef> sideBPlayers;

    @Data
    public static class PlayerRef {
        private Long id;
        private String name;
        private int position;

        public PlayerRef(Long id, String name, int position) {
            this.id = id;
            this.name = name;
            this.position = position;
        }
    }
}
