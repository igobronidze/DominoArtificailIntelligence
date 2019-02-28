DELETE FROM system_parameter;
INSERT INTO system_parameter (id, key, value, type) VALUES(1, 'checkOpponentProbabilities', 'true', 'GAME_MANAGER');
INSERT INTO system_parameter (id, key, value, type) VALUES(2, 'epsilonForProbabilities', '0.0001', 'GAME_MANAGER');
INSERT INTO system_parameter (id, key, value, type) VALUES(3, 'distributedProbabilityMaxRate', '0.75', 'GAME_MANAGER');
INSERT INTO system_parameter (id, key, value, type) VALUES(4, 'analyzeFirstTwinTileRate', '0.10', 'GAME_MANAGER');
INSERT INTO system_parameter (id, key, value, type) VALUES(5, 'analyzeFirstNotTwinTileRate', '0.10', 'GAME_MANAGER');
INSERT INTO system_parameter (id, key, value, type) VALUES(6, 'analyzeFirstNotTwinTileTwinsSubtractionRate', '0.07', 'GAME_MANAGER');
INSERT INTO system_parameter (id, key, value, type) VALUES(7, 'bestMoveAutoPlay', 'false', 'CONTROL_PANEL');
INSERT INTO system_parameter (id, key, value, type) VALUES(8, 'detectAddedTiles', 'true', 'CONTROL_PANEL');
INSERT INTO system_parameter (id, key, value, type) VALUES(9, 'possiblePoints', '75,155,175,255,355', 'CONTROL_PANEL');
INSERT INTO system_parameter (id, key, value, type) VALUES(10, 'systemLanguageCode', 'ka', 'CONTROL_PANEL');
INSERT INTO system_parameter (id, key, value, type) VALUES(11, 'p2pServerPort', '8080', 'CONTROL_PANEL');
INSERT INTO system_parameter (id, key, value, type) VALUES(12, 'logTilesAfterMethod', 'true', 'LOGGING');
INSERT INTO system_parameter (id, key, value, type) VALUES(13, 'logOnVirtualMode', 'false', 'LOGGING');
INSERT INTO system_parameter (id, key, value, type) VALUES(14, 'logAboutRoundHeuristic', 'false', 'LOGGING');
INSERT INTO system_parameter (id, key, value, type) VALUES(15, 'minMaxOnFirstTile', 'true', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(16, 'minMaxTreeHeight', '7', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(17, 'minMaxType', 'BFS', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(18, 'opponentTilesPredictor', 'MIN_MAX', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(19, 'opponentPlayHeuristicsDiffsFunctionName', 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(20, 'minMaxIteration', '600000', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(21, 'useMultiProcessorMinMax', 'false', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(22, 'multiProcessorClientRankSysParam', 'minMaxIteration', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(23, 'multiProcessorServerPort', '8080', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(24, 'executeRankTestForMultiProcessorClient', 'true', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(25, 'rankTestCount', '2', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(26, 'minMaxForCachedNodeRoundIterationRate', '5', 'MIN_MAX');
INSERT INTO system_parameter (id, key, value, type) VALUES(27, 'coefficientForComplexHeuristic', '12', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(28, 'heuristicValueForStartNextRound', '7', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(29, 'rateForFinishedGameHeuristic', '2.0', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(30, 'roundHeuristicType', 'MIXED_ROUND_HEURISTIC', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(31, 'mixedRoundHeuristicTilesDiffRate', '2', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(32, 'mixedRoundHeuristicMovesDiffRate', '10', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(33, 'mixedRoundHeuristicPointsBalancingRate', '0.3', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(34, 'mixedRoundHeuristicOpenTilesSumBalancingRate', '0.15', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(35, 'mixedRoundHeuristicPointsDiffCoefficientRate', '0.5', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(36, 'mixedRoundHeuristicPLayTurnRate', '0.3', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(37, 'roundStatisticProcessorParam1', '0.4', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(38, 'roundStatisticProcessorParam2', '0.2', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(39, 'roundStatisticProcessorParam3', '0.1', 'HEURISTIC');
INSERT INTO system_parameter (id, key, value, type) VALUES(40, 'multiProcessorMinMaxThreadsCount', '3', 'MIN_MAX');

DELETE FROM arg_and_value;
INSERT INTO arg_and_value (id, function_name, arg, value)
VALUES (1, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 800, 0.9920810815287171),
  (2, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 60, 0.38260144662499995),
  (3, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 30, 0.34196854875),
  (4, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 26, 0.306545769125),
  (5, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 22, 0.269290596),
  (6, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 15, 0.1695859525625),
  (7, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 10, 0.162938043),
  (8, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 6, 0.15981133375),
  (9, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 3, 0.112924695),
  (10, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 1, 0.03472576849824219),
  (11, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 0.5, 0.02072619881402588),
  (12, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', 0, 0.004664956079001617),
  (13, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', -5, 0.00359327323),
  (14, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', -25, 0.001030432924875),
  (15, 'opponentPlayHeuristicsDiffsFunction_initialForOptimization', -800, 1.754861565E-4);

DELETE FROM channel;
INSERT INTO public.channel (id, name, params) VALUES (1, 'BetLive', 'tilesDetectorMarginLeftPercentage=15##tilesDetectorContour=200##blurCoefficient=3##tilesDetectorMarginLeftPercentage_2=2##tilesDetectorWidthPercentage=70##tilesDetectorHeightPercentage=15##tilesDetectorHeightPercentage_2=15##tilesDetectorMarginBottomPercentage=5##tilesDetectorContour_2=200##tilesDetectorMarginBottomPercentage_2=5##tilesDetectorWidthPercentage_2=70##firstNotTwinTileDirection=left##movePriority=RIGHT,LEFT,BOTTOM,TOP');
INSERT INTO public.channel (id, name, params) VALUES (2, 'Real', 'firstNotTwinTileDirection=left##movePriority=RIGHT,LEFT,BOTTOM,TOP');
INSERT INTO public.channel (id, name, params) VALUES (3, 'LiderBet', 'tilesDetectorMarginLeftPercentage=15##tilesDetectorContour=200##blurCoefficient=1##tilesDetectorMarginLeftPercentage_2=15##tilesDetectorWidthPercentage=75##tilesDetectorHeightPercentage=12##tilesDetectorHeightPercentage_2=12##tilesDetectorMarginBottomPercentage=5##tilesDetectorContour_2=200##tilesDetectorMarginBottomPercentage_2=5##tilesDetectorWidthPercentage_2=75##firstNotTwinTileDirection=right##movePriority=BOTTOM,TOP,RIGHT,LEFT');
