package com.example.badminton.specification;

import com.example.badminton.entity.Match;
import com.example.badminton.entity.MatchPlayer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MatchSpecifications {

    private MatchSpecifications() {}

    public static Specification<Match> hasPlayer(Long playerId) {
        return (root, query, cb) -> {
            if (playerId == null) return cb.conjunction();
            Join<Match, MatchPlayer> matchPlayers = root.join("matchPlayers");
            return cb.equal(matchPlayers.get("player").get("id"), playerId);
        };
    }

    public static Specification<Match> playedBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("playedAt"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("playedAt"), to));
            }
            return predicate;
        };
    }
}
