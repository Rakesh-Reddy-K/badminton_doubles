package com.example.badminton.service;

import com.example.badminton.dto.*;
import com.example.badminton.entity.Match;
import com.example.badminton.exception.BusinessException;
import com.example.badminton.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MatchServiceTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    private Long player1Id, player2Id, player3Id, player4Id;

    @BeforeEach
    void setupPlayers() {
        player1Id = createPlayer("MatchTestP1");
        player2Id = createPlayer("MatchTestP2");
        player3Id = createPlayer("MatchTestP3");
        player4Id = createPlayer("MatchTestP4");
    }

    private Long createPlayer(String name) {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName(name);
        return playerService.createPlayer(req).getId();
    }

    private CreateMatchRequest buildMatchRequest(Long a1, Long a2, Long b1, Long b2, int scoreA, int scoreB) {
        CreateMatchRequest req = new CreateMatchRequest();
        req.setPlayedAt(LocalDateTime.now());
        req.setSideAPlayer1Id(a1);
        req.setSideAPlayer2Id(a2);
        req.setSideBPlayer1Id(b1);
        req.setSideBPlayer2Id(b2);
        req.setSideAScore(scoreA);
        req.setSideBScore(scoreB);
        return req;
    }

    @Test
    void testCreateMatch() {
        CreateMatchRequest req = buildMatchRequest(player1Id, player2Id, player3Id, player4Id, 21, 18);
        MatchResponse resp = matchService.createMatch(req);

        assertNotNull(resp.getId());
        assertEquals(21, resp.getSideAScore());
        assertEquals(18, resp.getSideBScore());
        assertEquals("A", resp.getWinnerSide());
        assertEquals(2, resp.getSideAPlayers().size());
        assertEquals(2, resp.getSideBPlayers().size());
    }

    @Test
    void testCreateMatchEqualScores() {
        CreateMatchRequest req = buildMatchRequest(player1Id, player2Id, player3Id, player4Id, 21, 21);
        assertThrows(BusinessException.class, () -> matchService.createMatch(req));
    }

    @Test
    void testCreateMatchNegativeScores() {
        CreateMatchRequest req = buildMatchRequest(player1Id, player2Id, player3Id, player4Id, -1, 21);
        assertThrows(BusinessException.class, () -> matchService.createMatch(req));
    }

    @Test
    void testCreateMatchDuplicatePlayer() {
        CreateMatchRequest req = buildMatchRequest(player1Id, player1Id, player3Id, player4Id, 21, 18);
        assertThrows(BusinessException.class, () -> matchService.createMatch(req));
    }

    @Test
    void testWinnerCalculation() {
        assertEquals(Match.Side.A, matchService.determineWinner(21, 15));
        assertEquals(Match.Side.B, matchService.determineWinner(15, 21));
        assertThrows(BusinessException.class, () -> matchService.determineWinner(21, 21));
    }

    @Test
    void testGetMatch() {
        CreateMatchRequest req = buildMatchRequest(player1Id, player2Id, player3Id, player4Id, 21, 15);
        MatchResponse created = matchService.createMatch(req);

        MatchResponse found = matchService.getMatch(created.getId());
        assertNotNull(found);
        assertEquals(21, found.getSideAScore());
        assertEquals(15, found.getSideBScore());
    }

    @Test
    void testGetMatchNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> matchService.getMatch(9999L));
    }

    @Test
    void testDeleteMatch() {
        CreateMatchRequest req = buildMatchRequest(player1Id, player2Id, player3Id, player4Id, 21, 10);
        MatchResponse created = matchService.createMatch(req);

        assertDoesNotThrow(() -> matchService.deleteMatch(created.getId()));
        assertThrows(ResourceNotFoundException.class, () -> matchService.getMatch(created.getId()));
    }

    @Test
    void testGetRecentMatches() {
        CreateMatchRequest req = buildMatchRequest(player1Id, player2Id, player3Id, player4Id, 21, 14);
        matchService.createMatch(req);

        List<MatchResponse> recent = matchService.getRecentMatches(5);
        assertFalse(recent.isEmpty());
    }
}
