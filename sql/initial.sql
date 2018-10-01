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

INSERT INTO system_parameter (key, value) VALUES('checkOpponentProbabilities', 'true');
INSERT INTO system_parameter (key, value) VALUES('epsilonForProbabilities', '0.0001');
INSERT INTO system_parameter (key, value) VALUES('minMaxOnFirstTile', 'true');
INSERT INTO system_parameter (key, value) VALUES('minMaxTreeHeight', '7');
INSERT INTO system_parameter (key, value) VALUES('minMaxType', 'BFS');
INSERT INTO system_parameter (key, value) VALUES('useMinMaxPredictor', 'true');
INSERT INTO system_parameter (key, value) VALUES('opponentPlayHeuristicsDiffsFunctionName', 'opponentPlayHeuristicsDiffsFunction_M');
INSERT INTO system_parameter (key, value) VALUES('coefficientForComplexHeuristic', '12');
INSERT INTO system_parameter (key, value) VALUES('heuristicValueForStartNextRound', '15');
INSERT INTO system_parameter (key, value) VALUES('rateForFinishedGameHeuristic', '1.0');
INSERT INTO system_parameter (key, value) VALUES('distributedProbabilityMaxRate', '0.75');
INSERT INTO system_parameter (key, value) VALUES('logTilesAfterMethod', 'true');
INSERT INTO system_parameter (key, value) VALUES('logOnVirtualMode', 'false');
INSERT INTO system_parameter (key, value) VALUES('bestMoveAutoPlay', 'true');
INSERT INTO system_parameter (key, value) VALUES('detectAddedTiles', 'true');
INSERT INTO system_parameter (key, value) VALUES('possiblePoints', '75,155,175,255,355');
INSERT INTO system_parameter (key, value) VALUES('systemLanguageCode', 'ka');
INSERT INTO system_parameter (key, value) VALUES('minMaxIteration', '150000');
INSERT INTO system_parameter (key, value) VALUES('roundHeuristicType', 'POINT_DIFF_ROUND_HEURISTIC');
INSERT INTO system_parameter (key, value) VALUES('p2pServerPort', '8080');
INSERT INTO system_parameter (key, value) VALUES('logAboutRoundHeuristic', 'false');
INSERT INTO system_parameter (key, value) VALUES('mixedRoundHeuristicParam1', '2');
INSERT INTO system_parameter (key, value) VALUES('mixedRoundHeuristicParam2', '10');
INSERT INTO system_parameter (key, value) VALUES('mixedRoundHeuristicParam3', '0.4');
INSERT INTO system_parameter (key, value) VALUES('mixedRoundHeuristicParam4', '0.2');
INSERT INTO system_parameter (key, value) VALUES('mixedRoundHeuristicParam5', '0.1');
INSERT INTO system_parameter (key, value) VALUES('mixedRoundHeuristicParam6', '0.5');
INSERT INTO system_parameter (key, value) VALUES('mixedRoundHeuristicParam7', '3');
INSERT INTO system_parameter (key, value) VALUES('useMultithreadingMinMax', 'true');
INSERT INTO system_parameter (key, value) VALUES('multithreadingClientRankSysParam', 'minMaxIteration');
INSERT INTO system_parameter (key, value) VALUES('multithreadingServerPort', '8080');
INSERT INTO system_parameter (key, value) VALUES('executeRankTestForMultithreadingClient', 'true');
INSERT INTO system_parameter (key, value) VALUES('rankTestCount', '2');