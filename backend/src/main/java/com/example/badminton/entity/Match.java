package com.example.badminton.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches", indexes = {
    @Index(name = "idx_matches_played_at", columnList = "played_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "side_a_score", nullable = false)
    private Integer sideAScore;

    @Column(name = "side_b_score", nullable = false)
    private Integer sideBScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "winner_side", nullable = false, length = 10)
    private Side winnerSide;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("side ASC, position ASC")
    private List<MatchPlayer> matchPlayers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Side {
        A, B
    }
}
