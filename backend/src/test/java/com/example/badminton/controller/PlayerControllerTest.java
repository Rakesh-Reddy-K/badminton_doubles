package com.example.badminton.controller;

import com.example.badminton.dto.CreatePlayerRequest;
import com.example.badminton.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerService playerService;

    @Test
    void testCreatePlayer() throws Exception {
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ControllerTest\",\"phone\":\"+91-11111-11111\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ControllerTest"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testGetPlayers() throws Exception {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("ListTest");
        playerService.createPlayer(req);

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetPlayerById() throws Exception {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("ByIdTest");
        var created = playerService.createPlayer(req);

        mockMvc.perform(get("/api/players/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ByIdTest"));
    }

    @Test
    void testCreatePlayerWithBlankName() throws Exception {
        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPlayerNotFound() throws Exception {
        mockMvc.perform(get("/api/players/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePlayerStatus() throws Exception {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("StatusTest");
        var created = playerService.createPlayer(req);

        mockMvc.perform(patch("/api/players/" + created.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
