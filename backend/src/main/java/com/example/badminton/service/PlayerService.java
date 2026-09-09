package com.example.badminton.service;

import com.example.badminton.dto.CreatePlayerRequest;
import com.example.badminton.dto.PlayerResponse;
import com.example.badminton.dto.UpdatePlayerRequest;
import com.example.badminton.entity.Player;
import com.example.badminton.exception.BusinessException;
import com.example.badminton.exception.ResourceNotFoundException;
import com.example.badminton.repository.MatchPlayerRepository;
import com.example.badminton.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;

    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers(String search) {
        List<Player> players;
        if (search != null && !search.isBlank()) {
            players = playerRepository.findByNameContainingIgnoreCaseOrderByNameAsc(search.trim());
        } else {
            players = playerRepository.findAllByOrderByNameAsc();
        }
        return players.stream().map(PlayerResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayer(Long id) {
        Player player = findPlayerOrThrow(id);
        return PlayerResponse.fromEntity(player);
    }

    @Transactional
    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        if (playerRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BusinessException("A player with name '" + request.getName() + "' already exists", HttpStatus.CONFLICT);
        }

        Player player = new Player();
        player.setName(request.getName().trim());
        player.setPhone(request.getPhone());
        player.setEmail(request.getEmail());
        player.setActive(true);

        Player saved = playerRepository.save(player);
        return PlayerResponse.fromEntity(saved);
    }

    @Transactional
    public PlayerResponse updatePlayer(Long id, UpdatePlayerRequest request) {
        Player player = findPlayerOrThrow(id);

        if (!player.getName().equalsIgnoreCase(request.getName().trim())
                && playerRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BusinessException("A player with name '" + request.getName() + "' already exists", HttpStatus.CONFLICT);
        }

        player.setName(request.getName().trim());
        player.setPhone(request.getPhone());
        player.setEmail(request.getEmail());

        Player updated = playerRepository.save(player);
        return PlayerResponse.fromEntity(updated);
    }

    @Transactional
    public PlayerResponse updatePlayerStatus(Long id, boolean active) {
        Player player = findPlayerOrThrow(id);

        // Don't allow deactivating a player that has match history
        // (we can deactivate but never physically delete)
        player.setActive(active);

        Player updated = playerRepository.save(player);
        return PlayerResponse.fromEntity(updated);
    }

    @Transactional
    public void deletePlayer(Long id) {
        Player player = findPlayerOrThrow(id);

        if (matchPlayerRepository.existsByPlayerId(id)) {
            throw new BusinessException(
                    "Player '" + player.getName() + "' has match history and cannot be deleted. Deactivate instead.",
                    HttpStatus.CONFLICT
            );
        }

        playerRepository.delete(player);
    }

    @Transactional(readOnly = true)
    public Player findPlayerOrThrow(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
    }

    @Transactional(readOnly = true)
    public Page<PlayerResponse> getAllPlayersPaged(Pageable pageable, String search) {
        Page<Player> page;
        if (search != null && !search.isBlank()) {
            page = playerRepository.findAll(
                    (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    pageable
            );
        } else {
            page = playerRepository.findAll(pageable);
        }
        return page.map(PlayerResponse::fromEntity);
    }
}
