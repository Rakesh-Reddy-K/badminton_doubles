package com.example.badminton.service;

import com.example.badminton.dto.*;
import com.example.badminton.entity.Match;
import com.example.badminton.entity.MatchPlayer;
import com.example.badminton.entity.Player;
import com.example.badminton.exception.ResourceNotFoundException;
import com.example.badminton.repository.MatchPlayerRepository;
import com.example.badminton.repository.MatchRepository;
import com.example.badminton.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        DashboardResponse d = new DashboardResponse();
        d.setTotalMatches(matchRepository.count());
        d.setTotalPlayers(playerRepository.countByActiveTrue());

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        d.setMatchesToday(matchRepository.countMatchesInRange(todayStart, todayEnd));

        List<Match> all = matchRepository.findAll();
        List<Match> recent = all.stream()
                .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
                .limit(5).collect(Collectors.toList());
        d.setRecentMatches(recent.stream().map(this::toResponse).collect(Collectors.toList()));

        List<Player> active = playerRepository.findByActiveTrueOrderByNameAsc();
        List<PlayerStatistics> stats = active.stream()
                .map(p -> calcPlayerStats(p, all))
                .filter(s -> s.getMatchesPlayed() > 0)
                .collect(Collectors.toList());

        d.setMostActivePlayers(stats.stream()
                .sorted(Comparator.comparing(PlayerStatistics::getMatchesPlayed).reversed())
                .limit(5).collect(Collectors.toList()));
        d.setTopPlayers(stats.stream()
                .sorted(Comparator.comparing(PlayerStatistics::getWinPercentage).reversed())
                .limit(5).collect(Collectors.toList()));
        return d;
    }

    /**
     * Builds a per-day dashboard scoped to a single calendar date, mirroring the
     * structure of the global dashboard (matches played that day, players active
     * that day, most active players, top players, and the day's matches).
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDayDashboard(LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        List<Match> all = matchRepository.findByPlayedAtBetween(dayStart, dayEnd);

        DashboardResponse d = new DashboardResponse();
        d.setTotalMatches(all.size());
        d.setMatchesToday(all.size());

        // Unique players involved in any match that day
        Set<Long> playerIds = new HashSet<>();
        for (Match m : all) {
            for (MatchPlayer mp : m.getMatchPlayers()) playerIds.add(mp.getPlayer().getId());
        }
        d.setTotalPlayers(playerIds.size());

        d.setRecentMatches(all.stream()
                .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
                .map(this::toResponse)
                .collect(Collectors.toList()));

        // Player stats limited to players who played that day, computed only from that day's matches
        List<PlayerStatistics> stats = playerRepository.findByActiveTrueOrderByNameAsc().stream()
                .filter(p -> playerIds.contains(p.getId()))
                .map(p -> calcPlayerStats(p, all))
                .filter(s -> s.getMatchesPlayed() > 0)
                .collect(Collectors.toList());

        d.setMostActivePlayers(stats.stream()
                .sorted(Comparator.comparing(PlayerStatistics::getMatchesPlayed).reversed())
                .limit(5).collect(Collectors.toList()));
        d.setTopPlayers(stats.stream()
                .sorted(Comparator.comparing(PlayerStatistics::getWinPercentage).reversed())
                .limit(5).collect(Collectors.toList()));
        return d;
    }

    @Transactional(readOnly = true)
    public List<PlayerStatistics> getPlayerStatistics() {
        List<Match> all = matchRepository.findAll();
        return playerRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(p -> calcPlayerStats(p, all))
                .filter(s -> s.getMatchesPlayed() > 0)
                .sorted(Comparator.comparing(PlayerStatistics::getWinPercentage).reversed()
                        .thenComparing(PlayerStatistics::getMatchesPlayed).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlayerStatistics getPlayerStatistics(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", playerId));
        return calcPlayerStats(player, matchRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<PlayerStatistics> getRankings(int minMatches) {
        return getPlayerStatistics().stream()
                .filter(s -> s.getMatchesPlayed() >= minMatches)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PairStatistics> getPairStatistics(int minMatches) {
        List<Match> all = matchRepository.findAll();
        Map<String, PairAccumulator> map = new HashMap<>();

        for (Match m : all) {
            List<MatchPlayer> sA = m.getMatchPlayers().stream()
                    .filter(mp -> mp.getSide() == Match.Side.A)
                    .sorted(Comparator.comparing(MatchPlayer::getPosition))
                    .collect(Collectors.toList());
            List<MatchPlayer> sB = m.getMatchPlayers().stream()
                    .filter(mp -> mp.getSide() == Match.Side.B)
                    .sorted(Comparator.comparing(MatchPlayer::getPosition))
                    .collect(Collectors.toList());

            if (sA.size() == 2) processPair(map, sA.get(0), sA.get(1), m.getWinnerSide() == Match.Side.A);
            if (sB.size() == 2) processPair(map, sB.get(0), sB.get(1), m.getWinnerSide() == Match.Side.B);
        }

        return map.values().stream()
                .filter(a -> a.matches >= minMatches)
                .map(a -> a.toDto())
                .sorted(Comparator.comparing(PairStatistics::getWinPercentage).reversed())
                .collect(Collectors.toList());
    }

    private PlayerStatistics calcPlayerStats(Player player, List<Match> allMatches) {
        long wins = 0, played = 0;
        for (Match m : allMatches) {
            Optional<MatchPlayer> mp = m.getMatchPlayers().stream()
                    .filter(x -> x.getPlayer().getId().equals(player.getId()))
                    .findFirst();
            if (mp.isPresent()) {
                played++;
                if (m.getWinnerSide() == mp.get().getSide()) wins++;
            }
        }
        PlayerStatistics s = new PlayerStatistics();
        s.setPlayerId(player.getId());
        s.setPlayerName(player.getName());
        s.setMatchesPlayed(played);
        s.setWins(wins);
        s.setLosses(played - wins);
        return s;
    }

    private void processPair(Map<String, PairAccumulator> map, MatchPlayer p1, MatchPlayer p2, boolean won) {
        Long id1 = Math.min(p1.getPlayer().getId(), p2.getPlayer().getId());
        Long id2 = Math.max(p1.getPlayer().getId(), p2.getPlayer().getId());
        String key = id1 + "-" + id2;
        PairAccumulator a = map.computeIfAbsent(key, k -> new PairAccumulator());
        if (a.p1Id == null) {
            a.p1Id = id1;
            a.p1Name = p1.getPlayer().getId().equals(id1) ? p1.getPlayer().getName() : p2.getPlayer().getName();
            a.p2Id = id2;
            a.p2Name = p2.getPlayer().getId().equals(id2) ? p2.getPlayer().getName() : p1.getPlayer().getName();
        }
        a.matches++;
        if (won) a.wins++; else a.losses++;
    }

    private MatchResponse toResponse(Match match) {
        MatchResponse d = new MatchResponse();
        d.setId(match.getId());
        d.setPlayedAt(match.getPlayedAt());
        d.setSideAScore(match.getSideAScore());
        d.setSideBScore(match.getSideBScore());
        d.setWinnerSide(match.getWinnerSide().name());
        d.setNotes(match.getNotes());
        d.setCreatedAt(match.getCreatedAt());
        d.setUpdatedAt(match.getUpdatedAt());
        List<MatchResponse.PlayerRef> sA = new ArrayList<>(), sB = new ArrayList<>();
        for (MatchPlayer mp : match.getMatchPlayers()) {
            MatchResponse.PlayerRef r = new MatchResponse.PlayerRef(
                    mp.getPlayer().getId(), mp.getPlayer().getName(), mp.getPosition());
            if (mp.getSide() == Match.Side.A) sA.add(r); else sB.add(r);
        }
        sA.sort(Comparator.comparing(MatchResponse.PlayerRef::getPosition));
        sB.sort(Comparator.comparing(MatchResponse.PlayerRef::getPosition));
        d.setSideAPlayers(sA);
        d.setSideBPlayers(sB);
        return d;
    }

    private static class PairAccumulator {
        Long p1Id; String p1Name; Long p2Id; String p2Name;
        long matches = 0, wins = 0, losses = 0;
        PairStatistics toDto() {
            PairStatistics s = new PairStatistics();
            s.setPlayer1Id(p1Id); s.setPlayer1Name(p1Name);
            s.setPlayer2Id(p2Id); s.setPlayer2Name(p2Name);
            s.setMatchesPlayed(matches); s.setWins(wins); s.setLosses(losses);
            return s;
        }
    }
}
