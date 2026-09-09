package com.example.badminton.service;

import com.example.badminton.dto.CreateMatchRequest;
import com.example.badminton.dto.MatchResponse;
import com.example.badminton.dto.PagedResponse;
import com.example.badminton.entity.Match;
import com.example.badminton.entity.MatchPlayer;
import com.example.badminton.entity.Player;
import com.example.badminton.exception.BusinessException;
import com.example.badminton.exception.ResourceNotFoundException;
import com.example.badminton.repository.MatchRepository;
import com.example.badminton.specification.MatchSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerService playerService;

    @Transactional
    public MatchResponse createMatch(CreateMatchRequest request) {
        validateMatchRequest(request);

        Match match = new Match();
        applyRequestToMatch(match, request);

        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideAPlayer1Id(), Match.Side.A, 1));
        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideAPlayer2Id(), Match.Side.A, 2));
        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideBPlayer1Id(), Match.Side.B, 1));
        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideBPlayer2Id(), Match.Side.B, 2));

        Match saved = matchRepository.save(match);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatch(Long id) {
        return toResponse(findMatchOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<MatchResponse> getMatches(int page, int size, Long playerId,
                                                   LocalDate from, LocalDate to, String sort) {
        Pageable pageable = PageRequest.of(page, size,
                "oldest".equalsIgnoreCase(sort) ? Sort.by("playedAt").ascending() : Sort.by("playedAt").descending());

        Specification<Match> spec = Specification.where(MatchSpecifications.hasPlayer(playerId))
                .and(MatchSpecifications.playedBetween(
                        from != null ? from.atStartOfDay() : null,
                        to != null ? to.atTime(LocalTime.MAX) : null
                ));

        Page<Match> result = matchRepository.findAll(spec, pageable);

        PagedResponse<MatchResponse> response = new PagedResponse<>();
        response.setContent(result.getContent().stream().map(this::toResponse).collect(Collectors.toList()));
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setLast(result.isLast());
        response.setFirst(result.isFirst());
        return response;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getRecentMatches(int limit) {
        List<Match> matches = matchRepository.findAll(Sort.by(Sort.Direction.DESC, "playedAt"));
        return matches.stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchResponse updateMatch(Long id, CreateMatchRequest request) {
        Match match = findMatchOrThrow(id);
        validateMatchRequest(request);
        applyRequestToMatch(match, request);

        match.getMatchPlayers().clear();
        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideAPlayer1Id(), Match.Side.A, 1));
        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideAPlayer2Id(), Match.Side.A, 2));
        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideBPlayer1Id(), Match.Side.B, 1));
        match.getMatchPlayers().add(buildMatchPlayer(match, request.getSideBPlayer2Id(), Match.Side.B, 2));

        Match updated = matchRepository.save(match);
        return toResponse(updated);
    }

    @Transactional
    public void deleteMatch(Long id) {
        Match match = findMatchOrThrow(id);
        matchRepository.delete(match);
    }

    public Match.Side determineWinner(int sideAScore, int sideBScore) {
        if (sideAScore == sideBScore) {
            throw new BusinessException("Scores cannot be equal. There must be a winner.");
        }
        return sideAScore > sideBScore ? Match.Side.A : Match.Side.B;
    }

    private void applyRequestToMatch(Match match, CreateMatchRequest request) {
        match.setPlayedAt(request.getPlayedAt());
        match.setSideAScore(request.getSideAScore());
        match.setSideBScore(request.getSideBScore());
        match.setWinnerSide(determineWinner(request.getSideAScore(), request.getSideBScore()));
        match.setNotes(request.getNotes());
    }

    private void validateMatchRequest(CreateMatchRequest request) {
        if (request.getSideAScore() < 0 || request.getSideBScore() < 0) {
            throw new BusinessException("Scores cannot be negative");
        }
        if (request.getSideAScore().equals(request.getSideBScore())) {
            throw new BusinessException("Scores cannot be equal. There must be a winner.");
        }

        Set<Long> playerIds = new HashSet<>();
        playerIds.add(request.getSideAPlayer1Id());
        playerIds.add(request.getSideAPlayer2Id());
        playerIds.add(request.getSideBPlayer1Id());
        playerIds.add(request.getSideBPlayer2Id());

        if (playerIds.size() != 4) {
            throw new BusinessException("A player cannot appear twice in the same match. All 4 players must be unique.");
        }

        for (Long pid : playerIds) {
            Player p = playerService.findPlayerOrThrow(pid);
            if (Boolean.FALSE.equals(p.getActive())) {
                throw new BusinessException("Player '" + p.getName() + "' is inactive and cannot be used in a match");
            }
        }
    }

    private MatchPlayer buildMatchPlayer(Match match, Long playerId, Match.Side side, int position) {
        Player player = playerService.findPlayerOrThrow(playerId);
        MatchPlayer mp = new MatchPlayer();
        mp.setMatch(match);
        mp.setPlayer(player);
        mp.setSide(side);
        mp.setPosition(position);
        return mp;
    }

    private Match findMatchOrThrow(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }

    private MatchResponse toResponse(Match match) {
        MatchResponse dto = new MatchResponse();
        dto.setId(match.getId());
        dto.setPlayedAt(match.getPlayedAt());
        dto.setSideAScore(match.getSideAScore());
        dto.setSideBScore(match.getSideBScore());
        dto.setWinnerSide(match.getWinnerSide().name());
        dto.setNotes(match.getNotes());
        dto.setCreatedAt(match.getCreatedAt());
        dto.setUpdatedAt(match.getUpdatedAt());

        List<MatchResponse.PlayerRef> sideA = new ArrayList<>();
        List<MatchResponse.PlayerRef> sideB = new ArrayList<>();

        for (MatchPlayer mp : match.getMatchPlayers()) {
            MatchResponse.PlayerRef ref = new MatchResponse.PlayerRef(
                    mp.getPlayer().getId(),
                    mp.getPlayer().getName(),
                    mp.getPosition()
            );
            if (mp.getSide() == Match.Side.A) {
                sideA.add(ref);
            } else {
                sideB.add(ref);
            }
        }

        sideA.sort((p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()));
        sideB.sort((p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()));

        dto.setSideAPlayers(sideA);
        dto.setSideBPlayers(sideB);
        return dto;
    }
}
