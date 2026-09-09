-- V2: Seed sample data
-- Adds 7 players and 10 doubles matches so the dashboard is immediately useful
--
-- Players:
--   1 Sukesh    2 Rakesh    3 Kumar    4 Revanth
--   5 Suresh    6 Santhosh  7 Ramesh

INSERT INTO players (name, phone, email, active, created_at, updated_at) VALUES
('Sukesh',   '+91-98765-43210', 'sukesh@example.com',   TRUE, NOW(), NOW()),
('Rakesh',   '+91-98765-43211', 'rakesh@example.com',   TRUE, NOW(), NOW()),
('Kumar',    '+91-98765-43212', 'kumar@example.com',    TRUE, NOW(), NOW()),
('Revanth',  '+91-98765-43213', 'revanth@example.com',  TRUE, NOW(), NOW()),
('Suresh',   '+91-98765-43214', 'suresh@example.com',   TRUE, NOW(), NOW()),
('Santhosh', '+91-98765-43215', 'santhosh@example.com', TRUE, NOW(), NOW()),
('Ramesh',   '+91-98765-43216', 'ramesh@example.com',   TRUE, NOW(), NOW());

-- Match 1: Sukesh+Rakesh vs Kumar+Revanth -> 21-15 (A wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '5 days', 21, 15, 'A', 'Opening match - strong start', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (1, 'A', 1), (2, 'A', 2),
    (3, 'B', 1), (4, 'B', 2)
) AS p(player_id, side, position);

-- Match 2: Suresh+Santhosh vs Ramesh+Sukesh -> 18-21 (B wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '4 days', 18, 21, 'B', 'Sukesh pair came from behind', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (5, 'A', 1), (6, 'A', 2),
    (7, 'B', 1), (1, 'B', 2)
) AS p(player_id, side, position);

-- Match 3: Rakesh+Kumar vs Revanth+Suresh -> 21-19 (A wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '4 days', 21, 19, 'A', 'Nail-biter finish', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (2, 'A', 1), (3, 'A', 2),
    (4, 'B', 1), (5, 'B', 2)
) AS p(player_id, side, position);

-- Match 4: Santhosh+Ramesh vs Sukesh+Kumar -> 15-21 (B wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '3 days', 15, 21, 'B', 'Sukesh-Kumar combo dominates', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (6, 'A', 1), (7, 'A', 2),
    (1, 'B', 1), (3, 'B', 2)
) AS p(player_id, side, position);

-- Match 5: Revanth+Sukesh vs Suresh+Rakesh -> 21-17 (A wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '3 days', 21, 17, 'A', 'Morning session', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (4, 'A', 1), (1, 'A', 2),
    (5, 'B', 1), (2, 'B', 2)
) AS p(player_id, side, position);

-- Match 6: Kumar+Santhosh vs Ramesh+Revanth -> 22-20 (A wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '2 days', 22, 20, 'A', 'Extra points thriller', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (3, 'A', 1), (6, 'A', 2),
    (7, 'B', 1), (4, 'B', 2)
) AS p(player_id, side, position);

-- Match 7: Suresh+Sukesh vs Rakesh+Santhosh -> 16-21 (B wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '2 days', 16, 21, 'B', 'Rakesh-Santhosh on fire', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (5, 'A', 1), (1, 'A', 2),
    (2, 'B', 1), (6, 'B', 2)
) AS p(player_id, side, position);

-- Match 8: Revanth+Ramesh vs Kumar+Suresh -> 21-18 (A wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '1 day', 21, 18, 'A', 'Evening showdown', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (4, 'A', 1), (7, 'A', 2),
    (3, 'B', 1), (5, 'B', 2)
) AS p(player_id, side, position);

-- Match 9: Sukesh+Santhosh vs Rakesh+Revanth -> 19-21 (B wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW() - INTERVAL '1 day', 19, 21, 'B', 'Tight contest', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (1, 'A', 1), (6, 'A', 2),
    (2, 'B', 1), (4, 'B', 2)
) AS p(player_id, side, position);

-- Match 10: Kumar+Suresh vs Sukesh+Ramesh -> 21-14 (A wins)
WITH m AS (
    INSERT INTO matches (played_at, side_a_score, side_b_score, winner_side, notes, created_at, updated_at)
    VALUES (NOW(), 21, 14, 'A', 'Dominant display today', NOW(), NOW())
    RETURNING id
)
INSERT INTO match_players (match_id, player_id, side, position)
SELECT m.id, p.player_id, p.side, p.position
FROM m, (VALUES
    (3, 'A', 1), (5, 'A', 2),
    (1, 'B', 1), (7, 'B', 2)
) AS p(player_id, side, position);
