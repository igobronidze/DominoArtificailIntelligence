DELETE FROM system_parameter WHERE 1 = 1;

-------- GAME_MANAGER
INSERT INTO system_parameter (key, value, type) VALUES('checkOpponentProbabilities', 'true', 'GAME_MANAGER');
INSERT INTO system_parameter (key, value, type) VALUES('epsilonForProbabilities', '0.0001', 'GAME_MANAGER');
INSERT INTO system_parameter (key, value, type) VALUES('distributedProbabilityMaxRate', '0.75', 'GAME_MANAGER');
INSERT INTO system_parameter (key, value, type) VALUES('analyzeFirstTwinTileRate', '0.10', 'GAME_MANAGER');
INSERT INTO system_parameter (key, value, type) VALUES('analyzeFirstNotTwinTileRate', '0.10', 'GAME_MANAGER');
INSERT INTO system_parameter (key, value, type) VALUES('analyzeFirstNotTwinTileTwinsSubtractionRate', '0.07', 'GAME_MANAGER');

-------- CONTROL_PANEL
INSERT INTO system_parameter (key, value, type) VALUES('bestMoveAutoPlay', 'true', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('detectAddedTiles', 'true', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('possiblePoints', '75,155,175,255,355', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('systemLanguageCode', 'ka', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('p2pServerPort', '8080', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('levelDefaultValue', '5', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('defaultWinPoint', '255', 'CONTROL_PANEL');
INSERT INTO system_parameter (key, value, type) VALUES('defaultChannelName', 'Real', 'CONTROL_PANEL');

-------- LOGGING
INSERT INTO system_parameter (key, value, type) VALUES('logTilesAfterMethod', 'true', 'LOGGING');
INSERT INTO system_parameter (key, value, type) VALUES('logOnVirtualMode', 'false', 'LOGGING');

-------- MIN_MAX
-- Global
INSERT INTO system_parameter (key, value, type) VALUES('minMaxOnFirstTile', 'true', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxTreeHeight', '7', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxType', 'BFS', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('opponentTilesPredictor', 'MIN_MAX', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('opponentPlayHeuristicsDiffsFunctionName', 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxIteration', '150000', 'MIN_MAX');
-- MultiProcessor
INSERT INTO system_parameter (key, value, type) VALUES('useMultiProcessorMinMax', 'true', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('multiProcessorClientRankSysParam', 'minMaxIteration', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('multiProcessorServerPort', '8080', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('executeRankTestForMultiProcessorClient', 'true', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('rankTestCount', '2', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('minMaxForCachedNodeRoundIterationRate', '5', 'MIN_MAX');
INSERT INTO system_parameter (key, value, type) VALUES('multiProcessorMinMaxThreadsCount', '3', 'MIN_MAX');

-------- HEURISTIC
-- Global
INSERT INTO system_parameter (key, value, type) VALUES('coefficientForComplexHeuristic', '12', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('heuristicValueForStartNextRound', '7', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('rateForFinishedGameHeuristic', '2.0', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('roundHeuristicType', 'MIXED_ROUND_HEURISTIC', 'HEURISTIC');
-- MixedRoundHeuristic
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicTilesDiffRate', '2', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicMovesDiffRate', '10', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicPointsBalancingRate', '0.3', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicOpenTilesSumBalancingRate', '0.15', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicPointsDiffCoefficientRate', '0.5', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('mixedRoundHeuristicPLayTurnRate', '0.3', 'HEURISTIC');
-- RoundStatisticsProcessor
INSERT INTO system_parameter (key, value, type) VALUES('roundStatisticProcessorParam1', '0.4', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('roundStatisticProcessorParam2', '0.2', 'HEURISTIC');
INSERT INTO system_parameter (key, value, type) VALUES('roundStatisticProcessorParam3', '0.1', 'HEURISTIC');
