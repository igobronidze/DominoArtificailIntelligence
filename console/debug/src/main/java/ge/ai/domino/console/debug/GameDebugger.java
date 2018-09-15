package ge.ai.domino.console.debug;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.console.debug.util.RoundParser;
import ge.ai.domino.dao.function.FunctionDAO;
import ge.ai.domino.dao.function.FunctionDAOImpl;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.function.FunctionArgsAndValues;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.game.move.AddForMeProcessor;
import ge.ai.domino.manager.game.move.AddForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.manager.game.move.AddForOpponentProcessorVirtual;
import ge.ai.domino.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessorVirtual;
import ge.ai.domino.manager.heuristic.HeuristicManager;
import ge.ai.domino.manager.opponentplay.OpponentPlaysManager;
import ge.ai.domino.manager.opponentplay.guess.NegativeBalancedGuessRateCounter;
import ge.ai.domino.manager.replaygame.ReplayGameManager;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.math.optimization.OptimizationDirection;
import ge.ai.domino.math.optimization.unimodal.multipleparams.ParamInterval;
import ge.ai.domino.math.optimization.unimodal.multipleparams.UnimodalOptimizationWithMultipleParams;
import ge.ai.domino.math.optimization.unimodal.oneparam.UnimodalOptimizationType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameDebugger {

    private static final Logger logger = Logger.getLogger(GameDebugger.class);

    private static final String LOG_END = "END";

    private static final int GAME_ID = -1;

    private static final SystemParameterManager sysParamManager = new SystemParameterManager();

    private static final HeuristicManager heuristicManager = new HeuristicManager();

    private static final ReplayGameManager replayGameManager = new ReplayGameManager();

    private static final OpponentPlaysManager opponentPlaysManager = new OpponentPlaysManager();

    private static final FunctionManager functionManager = new FunctionManager();

    private static Round round;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("0. exit");
            System.out.println("1. Cache game");
            System.out.println("2. Parse round(please type " + LOG_END + " for end)");
            System.out.println("3. Add tile for me");
            System.out.println("4. Add tile fot opponent");
            System.out.println("5. Play for me");
            System.out.println("6. Play for opponent");
            System.out.println("7. Change sys param(only in this session)");
            System.out.println("8. Get heuristics");
            System.out.println("9. Replay games");
            System.out.println("10. MinMaxPredictor Optimization");
            String line = scanner.nextLine();

            try {
                switch (line) {
                    case "0":
                        return;
                    case "1": {
                        cachGame(scanner);
                        break;
                    }
                    case "2": {
                        parseRound(scanner);
                        break;
                    }
                    case "3": {
                        addTileForMe(scanner);
                        break;
                    }
                    case "4": {
                        addTileForOpponent(scanner);
                        break;
                    }
                    case "5": {
                        playForMe(scanner);
                        break;
                    }
                    case "6": {
                        playForOpponent(scanner);
                        break;
                    }
                    case "7": {
                        changeSystemParameter(scanner);
                        break;
                    }
                    case "8": {
                        countHeuristics();
                        break;
                    }
                    case "9": {
                        replayGames(scanner);
                        break;
                    }
                    case "10": {
                        executeMinMaxPredictorOptimization(scanner);
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void executeMinMaxPredictorOptimization(Scanner scanner) {
        List<Integer> idsForProcess = getIdsForProcess(scanner);

        String functionName = "opponentPlayHeuristicsDiffsFunction_initialForOptimization";
        sysParamManager.changeParameterOnlyInCache("opponentPlayHeuristicsDiffsFunctionName", functionName);

        FunctionDAO functionDAO = new FunctionDAOImpl();
        FunctionManager functionManager = new FunctionManager();
        Map<String, FunctionArgsAndValues> functionArgsAndValuesMap = functionDAO.getFunctionArgsAndValues(functionName);
        FunctionArgsAndValues functionArgsAndValues = functionArgsAndValuesMap.get(functionName);
        Collections.reverse(functionArgsAndValues.getArgs());
        Collections.reverse(functionArgsAndValues.getValues());

        System.out.println("Games amount:");
        Integer gamesAmount = Integer.parseInt(scanner.nextLine());
        System.out.println("Optimization iteration:");
        Integer optimizationIteration = Integer.parseInt(scanner.nextLine());
        System.out.println("Optimization inner iteration:");
        Integer optimizationInnerIteration = Integer.parseInt(scanner.nextLine());

        UnimodalOptimizationWithMultipleParams unimodalOptimizationWithMultipleParams =
                new UnimodalOptimizationWithMultipleParams(UnimodalOptimizationType.INTERVAL_DIVISION, OptimizationDirection.MAX) {
                    @Override
                    public double getValue(List<Double> params) {
                        functionArgsAndValues.setValues(params);
                        functionArgsAndValuesMap.put(functionName, functionArgsAndValues);
                        functionManager.setFunctions(functionArgsAndValuesMap, false);

                        return getAverageGuess(idsForProcess.subList(0, gamesAmount), NegativeBalancedGuessRateCounter.class.getSimpleName(), params);
                    }
                };

        for (int i = 1; i <= optimizationIteration; i++) {
            logger.info("Starting MinMaxPredictor optimization iteration[" + i + "]");

            Collections.shuffle(idsForProcess);

            List<Double> newValues = unimodalOptimizationWithMultipleParams.getExtremaVector(functionArgsAndValues.getValues(),
                    getParamIntervals(functionArgsAndValues.getValues()), optimizationInnerIteration);
            functionArgsAndValues.setValues(newValues);
            logger.info("Finished MinMaxPredictor optimization iteration[" + i + "]");
            logger.info("New values: " + newValues);
            try {
                Thread.sleep(5 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<ParamInterval> getParamIntervals(List<Double> params) {
        List<ParamInterval> paramIntervals = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            ParamInterval paramInterval = new ParamInterval(i == 0 ? 0 : params.get(i - 1) + 0.000001,
                    i == params.size() - 1 ? 1 : params.get(i + 1) - 0.000001);
            paramIntervals.add(paramInterval);
        }
        return paramIntervals;
    }

    private static double getAverageGuess(List<Integer> idsForProcess, String guessRateCounterClassName, List<Double> params) {
        List<OpponentPlay> fullOpponentPlays = new ArrayList<>();
        for (int id : idsForProcess) {
            logger.info("Starting game for replay id[" + id + "]");

            int gameId = 0;
            try {
                ReplayMoveInfo replayMoveInfo = replayGameManager.startReplayGame(id);
                gameId = replayMoveInfo.getGameId();

                while (replayMoveInfo.getNextMove() != null) {
                    replayMoveInfo = replayGameManager.replayMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
                }

                fullOpponentPlays.addAll(removeExtraPlays(CachedGames.getOpponentPlays(replayMoveInfo.getGameId())));
            } catch (Exception ex) {
                logger.error("Error occurred while replay game id[" + id + "]", ex);
            } finally {
                CachedGames.removeGame(gameId);
                CachedGames.removeCreatedGameHistory(gameId);
                CachedMinMax.cleanUp(gameId);
            }

            logger.info("Finished game for replay id[" + id + "]");
        }

        GroupedOpponentPlay groupedOpponentPlay = opponentPlaysManager.getGroupedOpponentPlays(fullOpponentPlays, false, false, true).get(0);
        logger.info("Opponent plays grouped in one result for params: " + params);
        logger.info(groupedOpponentPlay.getAverageGuess());
        return groupedOpponentPlay.getAverageGuess().get(guessRateCounterClassName);
    }

    private static void replayGames(Scanner scanner) {
        List<Integer> idsForProcess = getIdsForProcess(scanner);

        functionManager.initFunctions();

        Map<Integer, List<GroupedOpponentPlay>> groupOpponentPlaysMap = new TreeMap<>();
        List<OpponentPlay> fullOpponentPlays = new ArrayList<>();

        for (int id : idsForProcess) {
            logger.info("Starting game for replay id[" + id + "]");

            int gameId = 0;
            try {
                ReplayMoveInfo replayMoveInfo = replayGameManager.startReplayGame(id);
                gameId = replayMoveInfo.getGameId();
                while (replayMoveInfo.getNextMove() != null) {
                    replayMoveInfo = replayGameManager.replayMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
                }

                List<OpponentPlay> opponentPlays = removeExtraPlays(CachedGames.getOpponentPlays(replayMoveInfo.getGameId()));
                List<GroupedOpponentPlay> groupedOpponentPlays = opponentPlaysManager.getGroupedOpponentPlays(opponentPlays, true, false, false);
                groupOpponentPlaysMap.put(id, groupedOpponentPlays);

                fullOpponentPlays.addAll(opponentPlays);

                logger.info("GroupedOpponentPlay info for replayed game, id[" + replayMoveInfo.getGameId() + "], replayedGameId[" + id + "]");
                for (GroupedOpponentPlay groupedOpponentPlay : groupedOpponentPlays) {
                    logger.info(groupedOpponentPlay.getAverageGuess());
                }
            } catch (Exception ex) {
                logger.error("Error occurred while replay game id[" + id + "]", ex);
            } finally {
                CachedGames.removeGame(gameId);
                CachedGames.removeCreatedGameHistory(gameId);
                CachedMinMax.cleanUp(gameId);
            }

            logger.info("Finished game for replay id[" + id + "]");
        }

        for (Map.Entry<Integer, List<GroupedOpponentPlay>> entry : groupOpponentPlaysMap.entrySet()) {
            logger.info("GroupedOpponentPlay info for replayed game, id[" + entry.getValue().get(0).getGameId() + "], replayedGameId[" + entry.getKey() + "]");
            for (GroupedOpponentPlay groupedOpponentPlay : entry.getValue()) {
                logger.info(groupedOpponentPlay.getAverageGuess());
            }
        }
        GroupedOpponentPlay groupedOpponentPlay = opponentPlaysManager.getGroupedOpponentPlays(fullOpponentPlays, false, false, true).get(0);
        logger.info("Opponent plays grouped in one result");
        logger.info(groupedOpponentPlay.getAverageGuess());
    }

    private static List<Integer> getIdsForProcess(Scanner scanner) {
        System.out.println("Game ids( Format example - 1-20/3,15 ):");
        String inputIds = scanner.nextLine();

        int idFrom = Integer.parseInt(inputIds.split("/")[0].split("-")[0]);
        int idTo = Integer.parseInt(inputIds.split("/")[0].split("-")[1]);

        String[] notUsedIdsString = inputIds.split("/")[1].split(",");
        List<Integer> notUsedIds = new ArrayList<>();
        for (String id : notUsedIdsString) {
            notUsedIds.add(Integer.parseInt(id));
        }

        return IntStream.range(idFrom, idTo + 1).filter(i -> !notUsedIds.contains(i)).boxed().collect(Collectors.toList());
    }

    private static void countHeuristics() {
        Map<String, Double> result = heuristicManager.getHeuristics(round);
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        logger.info("Heuristics counted successfully");
    }

    private static void changeSystemParameter(Scanner scanner) {
        System.out.println("key:");
        String key = scanner.nextLine();
        System.out.println("value:");
        String value = scanner.nextLine();
        sysParamManager.changeParameterOnlyInCache(key, value);
        logger.info("Sys param changed successfully");
    }

    private static void playForOpponent(Scanner scanner) throws DAIException {
        System.out.println("Left:");
        int left = Integer.parseInt(scanner.nextLine());
        System.out.println("Right:");
        int right = Integer.parseInt(scanner.nextLine());
        System.out.println("Direction:");
        MoveDirection direction = MoveDirection.valueOf(scanner.nextLine());
        System.out.println("Virtual:");
        boolean virtual = Boolean.valueOf(scanner.nextLine());
        if (virtual) {
            PlayForOpponentProcessorVirtual playForOpponentProcessor = new PlayForOpponentProcessorVirtual();
            round = playForOpponentProcessor.move(round, new Move(left, right, direction));
        } else {
            PlayForOpponentProcessor playForOpponentProcessor = new PlayForOpponentProcessor();
            round = playForOpponentProcessor.move(round, new Move(left, right, direction));
        }
        logger.info("Played fot opponent successfully");
    }

    private static void playForMe(Scanner scanner) throws DAIException {
        System.out.println("Left:");
        int left = Integer.parseInt(scanner.nextLine());
        System.out.println("Right:");
        int right = Integer.parseInt(scanner.nextLine());
        System.out.println("Direction:");
        MoveDirection direction = MoveDirection.valueOf(scanner.nextLine());
        System.out.println("Virtual:");
        boolean virtual = Boolean.valueOf(scanner.nextLine());
        if (virtual) {
            PlayForMeProcessorVirtual playForMeProcessor = new PlayForMeProcessorVirtual();
            round = playForMeProcessor.move(round, new Move(left, right, direction));
        } else {
            PlayForMeProcessor playForMeProcessor = new PlayForMeProcessor();
            round = playForMeProcessor.move(round, new Move(left, right, direction));
        }
        logger.info("Played fot me successfully");
    }

    private static void addTileForOpponent(Scanner scanner) throws DAIException {
        System.out.println("Virtual:");
        boolean virtual = Boolean.valueOf(scanner.nextLine());
        if (virtual) {
            AddForOpponentProcessorVirtual addForOpponentProcessor = new AddForOpponentProcessorVirtual();
            round = addForOpponentProcessor.move(round, new Move(0, 0, null));
        } else {
            AddForOpponentProcessor addForOpponentProcessor = new AddForOpponentProcessor();
            round = addForOpponentProcessor.move(round, new Move(0, 0, null));
        }
        logger.info("Added for opponent successfully");
    }

    private static void addTileForMe(Scanner scanner) throws DAIException {
        System.out.println("Left:");
        int left = Integer.parseInt(scanner.nextLine());
        System.out.println("Right:");
        int right = Integer.parseInt(scanner.nextLine());
        System.out.println("Virtual:");
        boolean virtual = Boolean.valueOf(scanner.nextLine());
        if (virtual) {
            AddForMeProcessorVirtual addForMeProcessor = new AddForMeProcessorVirtual();
            round = addForMeProcessor.move(round, new Move(left, right, null));
        } else {
            AddForMeProcessor addForMeProcessor = new AddForMeProcessor();
            round = addForMeProcessor.move(round, new Move(left, right, null));
        }
        logger.info("Added for me successfully");
    }

    private static void parseRound(Scanner scanner) throws DAIException {
        String s;
        StringBuilder log = new StringBuilder();
        while (!(s = scanner.nextLine()).equals(LOG_END)) {
            log.append(s).append(RoundLogger.END_LINE);
        }
        round = RoundParser.parseRound(log.toString());
        round.getGameInfo().setGameId(GAME_ID);
        logger.info("Round parsed successfully");
    }

    private static void cachGame(Scanner scanner) {
        GameProperties gameProperties = new GameProperties();
        System.out.println("Opponent name:");
        gameProperties.setOpponentName(scanner.nextLine());
        System.out.println("Channel:");
        Channel channel = new Channel();
        channel.setName(scanner.nextLine());
        gameProperties.setChannel(channel);
        System.out.println("Point for win:");
        gameProperties.setPointsForWin(Integer.parseInt(scanner.nextLine()));
        Game game = new Game();
        game.setId(GAME_ID);
        game.setProperties(gameProperties);
        CachedGames.addGame(game);
        logger.info("Game cached successfully");
    }

    private static List<OpponentPlay> removeExtraPlays(List<OpponentPlay> opponentPlays) {
        List<OpponentPlay> result = new ArrayList<>();
        boolean lastAdd = false;
        for (OpponentPlay opponentPlay : opponentPlays) {
            if (!lastAdd) {
                result.add(opponentPlay);
            }
            lastAdd = opponentPlay.getMoveType() == MoveType.ADD_FOR_OPPONENT;
        }
        return result;
    }
}
