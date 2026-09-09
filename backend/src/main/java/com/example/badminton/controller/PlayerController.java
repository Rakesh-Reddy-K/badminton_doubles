package com.example.badminton.controller;

import com.example.badminton.dto.*;
import com.example.badminton.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(playerService.getAllPlayers(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayer(id));
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable Long id,
                                                        @Valid @RequestBody UpdatePlayerRequest request) {
        return ResponseEntity.ok(playerService.updatePlayer(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PlayerResponse> updatePlayerStatus(@PathVariable Long id,
                                                              @Valid @RequestBody PlayerStatusRequest request) {
        return ResponseEntity.ok(playerService.updatePlayerStatus(id, request.getActive()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
