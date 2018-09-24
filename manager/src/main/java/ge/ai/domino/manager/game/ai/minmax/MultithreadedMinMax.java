package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.move.MoveProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessorVirtual;
import ge.ai.domino.manager.multithreadingserver.Server;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultithreadedMinMax extends MinMax {

    private final Logger logger = Logger.getLogger(MultithreadedMinMax.class);

    private final MoveProcessor playForMeProcessorVirtual = new PlayForMeProcessorVirtual();

    private MinMax minMax;

    public MultithreadedMinMax(MinMax minMax) {
        this.minMax = minMax;
    }

    @Override
    public void minMaxForCachedNodeRound(Round round) throws DAIException {
        minMax.minMaxForCachedNodeRound(round);
    }

    @Override
    public String getType() {
        return "MULTITHREADED_" + minMax.getType();
    }

    @Override
    public AiPredictionsWrapper solve(Round round) throws DAIException {
        long ms = System.currentTimeMillis();
        List<Move> moves = GameOperations.getPossibleMoves(round, false);
        logger.info("Start MultithreadedMinMax solve method, movesCount[" + moves.size() + "]");
        if (moves.size() == 1) {
            return minMax.solve(round);
        }

        Server server = Server.getInstance();
        int clientsCount = server.getClientsCount() + 1;
        logger.info("Clients count[" + clientsCount + "]");

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Callable<Map.Entry<List<Move>, List<AiPredictionsWrapper>>>> callableList = new ArrayList<>();

        Map<Integer, List<Move>> roundMap = new HashMap<>();
        for (int i = 0; i < moves.size(); i++) {
            int index = i % clientsCount - 1;
            roundMap.putIfAbsent(index, new ArrayList<>());
            roundMap.get(index).add(moves.get(i));
        }

        for (Map.Entry<Integer, List<Move>> entry : roundMap.entrySet()) {
            final List<Round> nextRounds = new ArrayList<>();
            for (Move move : entry.getValue()) {
                nextRounds.add(playForMeProcessorVirtual.move(CloneUtil.getClone(round), move));
            }

            if (entry.getKey() == -1) {
                logger.info("Executing own predict, moveCount[" + entry.getValue().size() + "]");
                callableList.add(() -> getOwnPredictions(entry.getValue(), nextRounds));
            } else {
                logger.info("Executing predict for client with index " + entry.getKey() + ", moveCount[" + entry.getValue().size() + "]");
                callableList.add(() -> new AbstractMap.SimpleEntry<>(entry.getValue(), server.executeMinMax(entry.getKey(), nextRounds)));
            }
        }

        try {
            CachedPrediction cachedPrediction = new CachedPrediction();

            List<Future<Map.Entry<List<Move>, List<AiPredictionsWrapper>>>> aiPredictionWrappers = executorService.invokeAll(callableList);

            AiPredictionsWrapper result = new AiPredictionsWrapper();
            AiPrediction bestPrediction = null;
            for (Future<Map.Entry<List<Move>, List<AiPredictionsWrapper>>> future : aiPredictionWrappers) {
                Map.Entry<List<Move>, List<AiPredictionsWrapper>> aiPredictionsWrapperEntry = future.get();

                for (int i = 0; i < aiPredictionsWrapperEntry.getKey().size(); i++) {
                    Move move = aiPredictionsWrapperEntry.getKey().get(i);
                    AiPredictionsWrapper aiPredictionsWrapper = aiPredictionsWrapperEntry.getValue().get(i);

                    if (aiPredictionsWrapper.getWarnMsgKey() != null) {
                        result.setWarnMsgKey(aiPredictionsWrapper.getWarnMsgKey());
                    }

                    AiPrediction aiPrediction = new AiPrediction();
                    aiPrediction.setMove(move);
                    for (AiPrediction prediction : aiPredictionsWrapper.getAiPredictions()) {
                        if (prediction.isBestMove()) {
                            aiPrediction.setHeuristicValue(prediction.getHeuristicValue());
                            if (bestPrediction == null || bestPrediction.getHeuristicValue() < aiPrediction.getHeuristicValue()) {
                                if (bestPrediction != null) {
                                    bestPrediction.setBestMove(false);
                                }
                                bestPrediction = aiPrediction;
                                bestPrediction.setBestMove(true);
                            }
                        }
                    }

                    cachedPrediction.getChildren().put(move, getCachedPrediction(aiPredictionsWrapper, move));

                    logger.info("PlayedMove: " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + aiPrediction.getHeuristicValue());
                    result.getAiPredictions().add(aiPrediction);
                }

            }
            logger.info("MultithreadingMinMax took " + (System.currentTimeMillis() - ms) + "ms");

            logger.info("AIPrediction is [" + bestPrediction.getMove().getLeft() + "-" + bestPrediction.getMove().getRight() + " " +
                    bestPrediction.getMove().getDirection().name() + "], " + "heuristic: " + bestPrediction.getHeuristicValue());

            CachedMinMax.setCachedPrediction(round.getGameInfo().getGameId(), cachedPrediction, true);
            return result;
        } catch (Exception ex) {
            logger.error("Error occurred while execute multithreaded minmax", ex);
            throw new DAIException("unexpectedError");
        }
    }

    private CachedPrediction getCachedPrediction(AiPredictionsWrapper aiPredictionsWrapper, Move move) {
        CachedPrediction cachedPrediction = new CachedPrediction();
        cachedPrediction.setMove(move);
        for (AiPrediction aiPrediction : aiPredictionsWrapper.getAiPredictions()) {
            CachedPrediction prediction = new CachedPrediction();
            prediction.setMove(aiPrediction.getMove());
            prediction.setHeuristicValue(aiPrediction.getHeuristicValue());
            cachedPrediction.getChildren().put(aiPrediction.getMove(), prediction);
        }
        return cachedPrediction;
    }

    private Map.Entry<List<Move>, List<AiPredictionsWrapper>> getOwnPredictions(List<Move> moves, List<Round> rounds) throws Exception {
        List<AiPredictionsWrapper> aiPredictionsWrappers = new ArrayList<>();
        for (Round round : rounds) {
            MinMax ownMinMax = minMax.getClass().newInstance();
            ownMinMax.setMultithreadingMinMax(true);
            ownMinMax.setThreadCount(rounds.size());
            aiPredictionsWrappers.add(ownMinMax.solve(round));
        }
        return new AbstractMap.SimpleEntry<>(moves, aiPredictionsWrappers);
    }
}
