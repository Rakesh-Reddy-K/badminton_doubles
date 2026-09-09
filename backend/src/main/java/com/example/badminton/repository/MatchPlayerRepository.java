package com.example.badminton.repository;

import com.example.badminton.entity.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    boolean existsByPlayerId(Long playerId);

    @Query("SELECT mp FROM MatchPlayer mp JOIN FETCH mp.player WHERE mp.match.id = :matchId ORDER BY mp.side ASC, mp.position ASC")
    List<MatchPlayer> findByMatchId(@Param("matchId") Long matchId);
}
