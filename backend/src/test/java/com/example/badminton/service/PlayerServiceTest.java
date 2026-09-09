package com.example.badminton.service;

import com.example.badminton.dto.CreatePlayerRequest;
import com.example.badminton.dto.PlayerResponse;
import com.example.badminton.dto.UpdatePlayerRequest;
import com.example.badminton.exception.BusinessException;
import com.example.badminton.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @Test
    void testCreatePlayer() {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("TestPlayer");
        req.setPhone("+91-99999-00000");
        req.setEmail("test@test.com");

        PlayerResponse response = playerService.createPlayer(req);
        assertNotNull(response.getId());
        assertEquals("TestPlayer", response.getName());
        assertTrue(response.getActive());
    }

    @Test
    void testCreateDuplicatePlayer() {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("DuplicatePlayer");

        playerService.createPlayer(req);

        CreatePlayerRequest req2 = new CreatePlayerRequest();
        req2.setName("DuplicatePlayer");

        assertThrows(BusinessException.class, () -> playerService.createPlayer(req2));
    }

    @Test
    void testGetPlayer() {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("GetPlayer");
        PlayerResponse created = playerService.createPlayer(req);

        PlayerResponse found = playerService.getPlayer(created.getId());
        assertEquals("GetPlayer", found.getName());
    }

    @Test
    void testGetPlayerNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> playerService.getPlayer(9999L));
    }

    @Test
    void testUpdatePlayer() {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("UpdatePlayer");
        PlayerResponse created = playerService.createPlayer(req);

        UpdatePlayerRequest upd = new UpdatePlayerRequest();
        upd.setName("UpdatedName");
        upd.setPhone("+91-11111-11111");

        PlayerResponse updated = playerService.updatePlayer(created.getId(), upd);
        assertEquals("UpdatedName", updated.getName());
        assertEquals("+91-11111-11111", updated.getPhone());
    }

    @Test
    void testDeactivatePlayer() {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("DeactivatePlayer");
        PlayerResponse created = playerService.createPlayer(req);

        PlayerResponse deactivated = playerService.updatePlayerStatus(created.getId(), false);
        assertFalse(deactivated.getActive());
    }

    @Test
    void testDeletePlayerWithoutMatchHistory() {
        CreatePlayerRequest req = new CreatePlayerRequest();
        req.setName("DeleteMe");
        PlayerResponse created = playerService.createPlayer(req);

        assertDoesNotThrow(() -> playerService.deletePlayer(created.getId()));
        assertThrows(ResourceNotFoundException.class, () -> playerService.getPlayer(created.getId()));
    }

    @Test
    void testSearchPlayers() {
        CreatePlayerRequest req1 = new CreatePlayerRequest();
        req1.setName("Alpha");
        playerService.createPlayer(req1);

        CreatePlayerRequest req2 = new CreatePlayerRequest();
        req2.setName("Beta");
        playerService.createPlayer(req2);

        var results = playerService.getAllPlayers("Alpha");
        assertEquals(1, results.size());
        assertEquals("Alpha", results.get(0).getName());
    }
}
