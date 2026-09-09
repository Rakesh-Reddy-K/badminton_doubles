-- V1: Initial schema for Badminton Doubles application
-- Creates players, matches, and match_players tables
-- Uses a normalized design: matches + players + match_players junction table

-- Players table
CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_players_name UNIQUE (name)
);

-- Matches table
CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    played_at TIMESTAMP NOT NULL,
    side_a_score INTEGER NOT NULL,
    side_b_score INTEGER NOT NULL,
    winner_side VARCHAR(10) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_matches_scores_non_negative CHECK (side_a_score >= 0 AND side_b_score >= 0),
    CONSTRAINT chk_matches_winner_side CHECK (winner_side IN ('A', 'B'))
);

-- Match players junction table
-- Links each player to a specific match, side, and position
CREATE TABLE match_players (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    side CHAR(1) NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_match_players_side CHECK (side IN ('A', 'B')),
    CONSTRAINT chk_match_players_position CHECK (position IN (1, 2)),
    CONSTRAINT fk_match_players_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    CONSTRAINT fk_match_players_player FOREIGN KEY (player_id) REFERENCES players(id),
    CONSTRAINT uk_match_players_match_player UNIQUE (match_id, player_id)
);

-- Index for efficient queries
CREATE INDEX idx_matches_played_at ON matches (played_at DESC);
CREATE INDEX idx_match_players_player ON match_players (player_id);
CREATE INDEX idx_match_players_match ON match_players (match_id);
CREATE INDEX idx_players_name ON players (name);
CREATE INDEX idx_players_active ON players (active);
