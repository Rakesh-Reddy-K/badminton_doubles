package com.example.badminton.controller;

import com.example.badminton.dto.CreateMatchRequest;
import com.example.badminton.dto.CreatePlayerRequest;
import com.example.badminton.service.MatchService;
import com.example.badminton.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    private Long p1, p2, p3, p4;

    @BeforeEach
    void setup() {
        p1 = createP("MCT1");
        p2 = createP("MCT2");
        p3 = createP("MCT3");
        p4 = createP("MCT4");
    }

    private Long createP(String name) {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName(name);
        return playerService.createPlayer(req).getId();
    }

    @Test
    void testCreateMatch() throws Exception {
        String json = String.format("""
                {
                    "playedAt": "%s",
                    "sideAPlayer1Id": %d,
                    "sideAPlayer2Id": %d,
                    "sideBPlayer1Id": %d,
                    "sideBPlayer2Id": %d,
                    "sideAScore": 21,
                    "sideBScore": 18,
                    "notes": "Test match"
                }
                """, LocalDateTime.now(), p1, p2, p3, p4);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sideAScore").value(21))
                .andExpect(jsonPath("$.winnerSide").value("A"));
    }

    @Test
    void testGetMatches() throws Exception {
        playMatch();
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetMatch() throws Exception {
        var m = playMatch();
        mockMvc.perform(get("/api/matches/" + m.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sideAScore").value(21));
    }

    @Test
    void testDeleteMatch() throws Exception {
        var m = playMatch();
        mockMvc.perform(delete("/api/matches/" + m.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMatchNotFound() throws Exception {
        mockMvc.perform(get("/api/matches/9999"))
                .andExpect(status().isNotFound());
    }

    private com.example.badminton.dto.MatchResponse playMatch() {
        CreateMatchRequest req = new CreateMatchRequest();
        req.setPlayedAt(LocalDateTime.now());
        req.setSideAPlayer1Id(p1);
        req.setSideAPlayer2Id(p2);
        req.setSideBPlayer1Id(p3);
        req.setSideBPlayer2Id(p4);
        req.setSideAScore(21);
        req.setSideBScore(18);
        return matchService.createMatch(req);
    }
}
