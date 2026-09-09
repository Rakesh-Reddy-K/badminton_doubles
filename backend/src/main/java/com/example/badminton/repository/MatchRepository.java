package com.example.badminton.repository;

import com.example.badminton.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match> {

    List<Match> findAllByOrderByPlayedAtDesc();

    List<Match> findByPlayedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByPlayedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.playedAt >= :from AND m.playedAt < :to")
    long countMatchesInRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT DISTINCT m FROM Match m JOIN m.matchPlayers mp WHERE mp.player.id = :playerId ORDER BY m.playedAt DESC")
    List<Match> findMatchesByPlayerId(@Param("playerId") Long playerId);
}
