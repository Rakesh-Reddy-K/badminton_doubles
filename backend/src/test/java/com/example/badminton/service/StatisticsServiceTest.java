package com.example.badminton.service;

import com.example.badminton.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StatisticsServiceTest {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    private Long p1, p2, p3, p4;

    @BeforeEach
    void setup() {
        p1 = createPlayer("StatP1");
        p2 = createPlayer("StatP2");
        p3 = createPlayer("StatP3");
        p4 = createPlayer("StatP4");
    }

    private Long createPlayer(String name) {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName(name);
        return playerService.createPlayer(req).getId();
    }

    private void playMatch(Long a1, Long a2, Long b1, Long b2, int sA, int sB) {
        CreateMatchRequest req = new CreateMatchRequest();
        req.setPlayedAt(LocalDateTime.now());
        req.setSideAPlayer1Id(a1);
        req.setSideAPlayer2Id(a2);
        req.setSideBPlayer1Id(b1);
        req.setSideBPlayer2Id(b2);
        req.setSideAScore(sA);
        req.setSideBScore(sB);
        matchService.createMatch(req);
    }

    @Test
    void testPlayerStatistics() {
        playMatch(p1, p2, p3, p4, 21, 18); // p1,p2 win
        playMatch(p1, p3, p2, p4, 21, 15); // p1,p3 win

        PlayerStatistics stats = statisticsService.getPlayerStatistics(p1);
        assertEquals("StatP1", stats.getPlayerName());
        assertEquals(2, stats.getMatchesPlayed());
        assertEquals(2, stats.getWins());
        assertEquals(0, stats.getLosses());
    }

    @Test
    void testPairStatistics() {
        playMatch(p1, p2, p3, p4, 21, 18);
        playMatch(p1, p2, p3, p4, 21, 15);

        List<PairStatistics> pairs = statisticsService.getPairStatistics(0);
        assertFalse(pairs.isEmpty());

        boolean found = pairs.stream()
                .anyMatch(ps -> ps.getMatchesPlayed() == 2 && ps.getWins() == 2);
        assertTrue(found, "Should find pair p1+p2 with 2 wins");
    }

    @Test
    void testDashboard() {
        playMatch(p1, p2, p3, p4, 21, 18);

        DashboardResponse d = statisticsService.getDashboard();
        assertTrue(d.getTotalMatches() > 0);
        assertTrue(d.getRecentMatches() != null);
        assertFalse(d.getRecentMatches().isEmpty());
    }

    @Test
    void testRankings() {
        playMatch(p1, p2, p3, p4, 21, 18);
        playMatch(p1, p2, p3, p4, 21, 15);

        List<PlayerStatistics> rankings = statisticsService.getRankings(1);
        assertFalse(rankings.isEmpty());
        // p1 should have 100% win rate
        PlayerStatistics top = rankings.get(0);
        assertEquals(100.0, top.getWinPercentage());
    }
}
