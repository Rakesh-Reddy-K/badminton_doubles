package com.example.badminton.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_players", uniqueConstraints = {
    @UniqueConstraint(name = "uk_match_players_match_player", columnNames = {"match_id", "player_id"})
}, indexes = {
    @Index(name = "idx_match_players_player", columnList = "player_id"),
    @Index(name = "idx_match_players_match", columnList = "match_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private Match.Side side;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Position {
        PLAYER_1(1), PLAYER_2(2);

        private final int value;

        Position(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
