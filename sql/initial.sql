-- System Parameters
CREATE TABLE system_parameter (
  id SERIAL PRIMARY KEY,
  key VARCHAR(50) NOT NULL UNIQUE,
  value VARCHAR(500) NOT NULL
);
ALTER TABLE system_parameter ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'CONSOLE_PARAMETER';

-- Played Games
CREATE TABLE played_game (
  id SERIAL PRIMARY KEY,
  version VARCHAR(50) NOT NULL,
  result VARCHAR(50) NOT NULL,
  date DATE,
  time TIME,
  my_point INTEGER,
  opponent_point INTEGER,
  point_for_win INTEGER NOT NULL,
  opponent_name VARCHAR(50) NOT NULL,
  website VARCHAR(50) NOT NULL,
  game_history TEXT
);
ALTER TABLE played_game ADD COLUMN channel_id INT NOT NULL DEFAULT 0;
ALTER TABLE played_game DROP COLUMN website;

-- Opponent Plays
CREATE TABLE opponent_play (
  id SERIAL PRIMARY KEY,
  game_id int NOT NULL,
  version VARCHAR(50) NOT NULL,
  move_type VARCHAR(50) NOT NULL,
  tile VARCHAR(50) NOT NULL,
  opponent_tiles TEXT NOT NULL
);
ALTER TABLE opponent_play ADD COLUMN possible_play_numbers VARCHAR(200);

-- Args And Values
CREATE TABLE arg_and_value (
  id SERIAL PRIMARY KEY,
  arg REAL NOT NULL,
  value REAL NOT NULL,
  function_name VARCHAR(100) NOT NULL
);

-- Channel
CREATE TABLE channel (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  params TEXT
);

-------- GAME_MANAGER
INSERT INTO system_parameter (key, value, type) VALUES('checkOpponentProbabilities', 'true', 'GAME_MANAGER');
INSERT INTO system_parameter (key, value, type) VALUES('epsilonForProbabilities', '0.0001', 'GAME_MANAGER');
INSERT INTO system_parameter (key, value, type) VALUES('distributedProbabilityMaxRate', '0.75', 'GAME_MANAGER');

-------- CONTROL_PANEL
INSERT INTO system_parameter (key, value, type) VALUES('bestMoveAutoPlay', 'true', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('detectAddedTiles', 'true', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('possiblePoints', '75,155,175,255,355', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('systemLanguageCode', 'ka', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('p2pServerPort', '8080', 'CONTROL_PANEL');

-------- LOGGING
INSERT INTO system_parameter (key, value, type) VALUES('logTilesAfterMethod', 'true', 'LOGGING');
INSERT INTO system_parameter (key, value, type) VALUES('logOnVirtualMode', 'false', 'LOGGING');
INSERT INTO system_parameter (key, value, type) VALUES('logAboutRoundHeuristic', 'false', 'LOGGING');

-------- MIN_MAX
-- Global
INSERT INTO system_parameter (key, value, type) VALUES('minMaxOnFirstTile', 'true', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxTreeHeight', '7', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxType', 'BFS', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('useMinMaxPredictor', 'true', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('opponentPlayHeuristicsDiffsFunctionName', 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxIteration', '150000', 'MIN_MAX');
-- Multithreading
INSERT INTO system_parameter (key, value, type) VALUES('useMultithreadingMinMax', 'true', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('multithreadingClientRankSysParam', 'minMaxIteration', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('multithreadingServerPort', '8080', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('executeRankTestForMultithreadingClient', 'true', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('rankTestCount', '2', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxForCachedNodeRoundIterationRate', '5', 'MIN_MAX');

-------- HEURISTIC
-- Global
INSERT INTO system_parameter (key, value, type) VALUES('coefficientForComplexHeuristic', '12', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('heuristicValueForStartNextRound', '12', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('rateForFinishedGameHeuristic', '1.0', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('roundHeuristicType', 'MIXED_ROUND_HEURISTIC', 'HEURISTIC');
-- MixedRoundHeuristic
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicTilesDiffRate', '2', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicMovesDiffRate', '10', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicPointsBalancingRate', '0.3', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicOpenTilesSumBalancingRate', '0.15', 'HEURISTIC');
-- RoundStatisticsProcessor
INSERT INTO system_parameter (key, value, type) VALUES('roundStatisticProcessorParam1', '0.4', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('roundStatisticProcessorParam2', '0.2', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('roundStatisticProcessorParam3', '0.1', 'HEURISTIC');
