package ge.ai.domino.manager.game.ai.minmax.bfs;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicFactory;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicHelper;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.ai.minmax.MinMax;
import ge.ai.domino.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.game.helper.filter.OpponentTilesFilter;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.MoveHelper;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.move.AddForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.AddForOpponentProcessorVirtual;
import ge.ai.domino.manager.game.move.MoveProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessorVirtual;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.CloneUtil;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class MinMaxBFS extends MinMax {

    private final Logger logger = Logger.getLogger(MinMaxBFS.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final SysParam minMaxIteration = new SysParam("minMaxIteration", "100000");

    private final MoveProcessor playForMeProcessorVirtual = new PlayForMeProcessorVirtual();

    private final MoveProcessor playForOpponentProcessorVirtual = new PlayForOpponentProcessorVirtual();

    private final MoveProcessor addForMeProcessorVirtual = new AddForMeProcessorVirtual();

    private final MoveProcessor addForOpponentProcessorVirtual = new AddForOpponentProcessorVirtual();

    private final RoundHeuristic roundHeuristic = RoundHeuristicFactory.getRoundHeuristic(systemParameterManager.getStringParameterValue(roundHeuristicType));

    private int iteration;

    private int gameId;

    private Map<Integer, List<NodeRound>> nodeRoundsByHeight = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));

    private Queue<NodeRound> nodeRoundsQueue = new LinkedList<>();

    @Override
    public AiPredictionsWrapper solve(Round round) throws DAIException {
        this.gameId = round.getGameInfo().getGameId();
        logger.info("Executing MinMaxBFS gameId[" + gameId + "]");

        NodeRound nodeRound = new NodeRound();
        nodeRound.setRound(round);
        return minMax(nodeRound);
    }

    @Override
    public void minMaxForCachedNodeRound(Round round) throws DAIException {
        this.gameId = round.getGameInfo().getGameId();
        logger.info("Executing MinMaxBFSForCachedNodeRound gameId[" + gameId + "]");

        long ms = System.currentTimeMillis();
        NodeRound nodeRound = new NodeRound();
        nodeRound.setRound(round);
        nodeRound.setTreeHeight(1);
        addInQueue(nodeRound);

        while (!nodeRoundsQueue.isEmpty()) {
            NodeRound nr = nodeRoundsQueue.remove();
            processRoundNode(nr);
        }
        nodeRoundsQueue = null;

        applyBottomUpMinMax();
        CachedMinMax.setCachedPrediction(round.getGameInfo().getGameId(), GameOperations.fillCachedPrediction(round, CachedPrediction.getCachedPrediction(nodeRound, 1)), false);
        logger.info("MinMaxBFSForCachedNodeRound took " + (System.currentTimeMillis() - ms) + " ms");
    }

    @Override
    public String getType() {
        return "BFS";
    }

    @SuppressWarnings("Duplicates")
    private AiPredictionsWrapper minMax(NodeRound nodeRound) throws DAIException {
        long ms = System.currentTimeMillis();
        List<Move> moves = GameOperations.getPossibleMoves(nodeRound.getRound(), false);
        logger.info("Ai predictions:");
        if (moves.isEmpty()) {
            logger.info("No AIPrediction");
            return null;
        }
        if (moves.size() == 1 && systemParameterManager.getBooleanParameterValue(bestMoveAutoPlay) && !multithreadingMinMax) {
            if (systemParameterManager.getBooleanParameterValue(useMinMaxPredictor)) {
                new Thread(() -> {
                    try {
                        CachedMinMax.changeMinMaxInProgress(gameId, true);
                        minMaxForMoves(moves, nodeRound, ms);
                        if (new MinMaxPredictor().usePredictor()) {
                            int gameId = nodeRound.getRound().getGameInfo().getGameId();
                            if (CachedMinMax.isUseFirstChild(gameId)) {
                                CachedMinMax.changeUseFirstChild(gameId, false);
                                CachedMinMax.setCachedPrediction(gameId, GameOperations.fillCachedPrediction(nodeRound.getRound(), CachedPrediction.getCachedPrediction(nodeRound.getChildren().get(0), 1)), false);
                            } else {
                                CachedMinMax.setCachedPrediction(gameId, CachedPrediction.getCachedPrediction(nodeRound, 2), true);
                            }
                        }
                        CachedMinMax.changeMinMaxInProgress(gameId, false);
                    } catch (DAIException ex) {
                        logger.error(ex);
                    }
                }).start();
            }
            AiPredictionsWrapper aiPredictionsWrapper = new AiPredictionsWrapper();
            AiPrediction aiPrediction = new AiPrediction();
            aiPrediction.setMove(moves.get(0));
            aiPrediction.setBestMove(true);
            aiPrediction.setHeuristicValue(Integer.MIN_VALUE);
            aiPredictionsWrapper.getAiPredictions().add(aiPrediction);
            return aiPredictionsWrapper;
        } else {
            AiPredictionsWrapper aiPredictionsWrapper = minMaxForMoves(moves, nodeRound, ms);
            if (new MinMaxPredictor().usePredictor() && !multithreadingMinMax) {
                CachedMinMax.setCachedPrediction(nodeRound.getRound().getGameInfo().getGameId(), CachedPrediction.getCachedPrediction(nodeRound, 2), true);
            }
            return aiPredictionsWrapper;
        }
    }

    private AiPredictionsWrapper minMaxForMoves(List<Move> moves, NodeRound nodeRound, long ms) throws DAIException {
        long inlineMs = System.currentTimeMillis();
        for (Move move : moves) {
            Round nextRound = playForMeProcessorVirtual.move(CloneUtil.getClone(nodeRound.getRound()), move);
            NodeRound nextNodeRound = new NodeRound();
            nextNodeRound.setRound(nextRound);
            nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
            nextNodeRound.setParent(nodeRound);
            nextNodeRound.setTreeHeight(1);
            nodeRound.getChildren().add(nextNodeRound);
            validateOpponentTiles(nextNodeRound, "playForMe");
            addInQueue(nextNodeRound);
        }
        logger.info("Initial node round processing took " + (System.currentTimeMillis() - inlineMs) + " ms");
        inlineMs = System.currentTimeMillis();

        while (!nodeRoundsQueue.isEmpty()) {
            NodeRound nr = nodeRoundsQueue.remove();
            processRoundNode(nr);
        }
        nodeRoundsQueue = null;
        logger.info("Processing node round took " + (System.currentTimeMillis() - inlineMs) + " ms");
        inlineMs = System.currentTimeMillis();

        applyBottomUpMinMax();
        logger.info("Button up MinMax took " + (System.currentTimeMillis() - inlineMs) + " ms");
        inlineMs = System.currentTimeMillis();

        AiPrediction bestAiPrediction = null;
        NodeRound bestNodeRound = null;
        List<AiPrediction> aiPredictions = new ArrayList<>();

        for (NodeRound nr : nodeRoundsByHeight.get(1)) {
            Move move = TileAndMoveHelper.getMove(nr.getLastPlayedMove());
            AiPrediction aiPrediction = new AiPrediction();
            aiPrediction.setMove(move);
            aiPrediction.setHeuristicValue(nr.getHeuristic());
            aiPredictions.add(aiPrediction);

            boolean better = false;
            if (nodeRound.getRound().getTableInfo().isMyMove()) {
                if (bestAiPrediction == null || bestAiPrediction.getHeuristicValue() < aiPrediction.getHeuristicValue()) {
                    better = true;
                }
            } else {
                if (bestAiPrediction == null || bestAiPrediction.getHeuristicValue() > aiPrediction.getHeuristicValue()) {
                    better = true;
                }
            }
            if (better) {
                if (bestAiPrediction != null) {
                    bestAiPrediction.setBestMove(false);
                }
                bestAiPrediction = aiPrediction;
                bestAiPrediction.setBestMove(true);
                if (bestNodeRound != null) {
                    bestNodeRound.setLastPlayedProbability(0.0);
                }
                bestNodeRound = nr;
                bestNodeRound.setLastPlayedProbability(1.0);
            }
            logger.info("PlayedMove: " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + nr.getHeuristic());
        }
        logger.info("Best move recognition took " + (System.currentTimeMillis() - inlineMs) + " ms");

        double tookMs = System.currentTimeMillis() - ms;
        logger.info("MinMaxBFS took " + tookMs + " ms, iteration " + iteration + ", average " + (tookMs / iteration));
        iteration = 0;
        nodeRound.setHeuristic(bestAiPrediction.getHeuristicValue());
        logger.info("AIPrediction is [" + bestAiPrediction.getMove().getLeft() + "-" + bestAiPrediction.getMove().getRight() + " " +
                bestAiPrediction.getMove().getDirection().name() + "], " + "heuristic: " + bestAiPrediction.getHeuristicValue());

        AiPredictionsWrapper aiPredictionsWrapper = new AiPredictionsWrapper();
        aiPredictionsWrapper.setAiPredictions(aiPredictions);
        aiPredictionsWrapper.setWarnMsgKey(applyValidation());
        return aiPredictionsWrapper;
    }

    private void processRoundNode(NodeRound nodeRound) throws DAIException {
        nodeRoundsByHeight.computeIfAbsent(nodeRound.getTreeHeight(), k -> new ArrayList<>());
        nodeRoundsByHeight.get(nodeRound.getTreeHeight()).add(nodeRound);

        if (iteration > systemParameterManager.getIntegerParameterValue(minMaxIteration) / threadCount) {
            return;
        }
        Round round = nodeRound.getRound();

        if (round.getGameInfo().isFinished() || isNewRound(round)) {
            return;
        }

        List<Move> moves = GameOperations.getPossibleMoves(round, false);
        if (round.getTableInfo().isMyMove()) {
            if (!moves.isEmpty()) {
                for (Move move : moves) {
                    Round nextRound = playForMeProcessorVirtual.move(CloneUtil.getClone(round), move);
                    NodeRound nextNodeRound = new NodeRound();
                    nextNodeRound.setRound(nextRound);
                    nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
                    nextNodeRound.setParent(nodeRound);
                    nextNodeRound.setTreeHeight(nodeRound.getTreeHeight() + 1);
                    nodeRound.getChildren().add(nextNodeRound);
                    validateOpponentTiles(nextNodeRound, "playForMe");
                    addInQueue(nextNodeRound);
                }
            } else {
                double bazaarProbSum = round.getTableInfo().getBazaarTilesCount();
                for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
                    Tile tile = entry.getKey();
                    double prob = entry.getValue();
                    if (prob != 1.0) {
                        double probForPickTile = (1 - prob) / bazaarProbSum; // Probability fot choose this tile
                        Move move = TileAndMoveHelper.getMove(tile, MoveDirection.LEFT);
                        Round nextRound = addForMeProcessorVirtual.move(CloneUtil.getClone(round), move);
                        NodeRound nextNodeRound = new NodeRound();
                        nextNodeRound.setRound(nextRound);
                        nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForMeMove(move));
                        nextNodeRound.setParent(nodeRound);
                        nextNodeRound.setTreeHeight(nodeRound.getTreeHeight() + 1);
                        nextNodeRound.setLastPlayedProbability(probForPickTile);
                        nodeRound.setBazaarNodeRound(nextNodeRound);
                        validateOpponentTiles(nextNodeRound, "addForMe");
                        addInQueue(nextNodeRound);
                    }
                }
            }
        } else {
            nodeRound.setOpponentTilesClone(CloneUtil.getClone(round.getOpponentTiles()));
            for (Move move : moves) {
                Round nextRound = playForOpponentProcessorVirtual.move(CloneUtil.getClone(round), move);
                NodeRound nextNodeRound = new NodeRound();
                nextNodeRound.setRound(nextRound);
                nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForOpponentMove(move));
                nextNodeRound.setParent(nodeRound);
                nextNodeRound.setTreeHeight(nodeRound.getTreeHeight() + 1);
                nodeRound.getChildren().add(nextNodeRound);
                validateOpponentTiles(nextNodeRound, "playForOpponent");
                addInQueue(nextNodeRound);
            }

            OpponentTilesFilter opponentTilesFilter = new OpponentTilesFilter().bazaar(true);
            long bazaarTilesCount = round.getOpponentTiles().entrySet().stream().filter(opponentTilesFilter :: filter).count();
            if (countTilesInMoves(moves) + bazaarTilesCount <= round.getTableInfo().getBazaarTilesCount()) {
                Round nextRound = addForOpponentProcessorVirtual.move(CloneUtil.getClone(round), null);
                NodeRound nextNodeRound = new NodeRound();
                nextNodeRound.setRound(nextRound);
                nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForOpponentMove());
                nextNodeRound.setParent(nodeRound);
                nextNodeRound.setTreeHeight(nodeRound.getTreeHeight() + 1);
                nodeRound.setBazaarNodeRound(nextNodeRound);
                validateOpponentTiles(nextNodeRound, "addForOpponent");
                addInQueue(nextNodeRound);
            }
        }
    }

    private void applyBottomUpMinMax() {
        for (Map.Entry<Integer, List<NodeRound>> entry : nodeRoundsByHeight.entrySet()) {
            long ms = System.currentTimeMillis();
            List<NodeRound> nodeRounds = entry.getValue();

            for (NodeRound nodeRound : nodeRounds) {
                if (!isLeafNodeRound(nodeRound)) {
                    if (nodeRound.getRound().getTableInfo().isMyMove()) {
                        NodeRound bestNodeRound = null;
                        if (nodeRound.getChildren().isEmpty()) {
                            bestNodeRound = nodeRound.getBazaarNodeRound();
                        } else {
                            for (NodeRound child : nodeRound.getChildren()) {
                                if (bestNodeRound == null || child.getHeuristic() > bestNodeRound.getHeuristic()) {
                                    if (bestNodeRound != null) {
                                        bestNodeRound.setLastPlayedProbability(0.0);
                                    }
                                    bestNodeRound = child;
                                    bestNodeRound.setLastPlayedProbability(1.0);
                                }
                            }
                        }
                        nodeRound.setHeuristic(bestNodeRound.getHeuristic());
                    } else {
                        // Possible node rounds sorted ASC by heuristic
                        Queue<NodeRound> possibleRounds = new PriorityQueue<>(Comparator.comparingDouble(NodeRound::getHeuristic));
                        possibleRounds.addAll(nodeRound.getChildren());
                        double heuristic = 0.0;
                        double remainingProbability = 1.0;
                        for (NodeRound child : possibleRounds) {
                            if (ComparisonHelper.equal(remainingProbability, 0.0)) {
                                break;
                            }
                            Map<Tile, Double> opponentTilesClone = nodeRound.getOpponentTilesClone();
                            double prob = opponentTilesClone.get(new Tile(child.getLastPlayedMove().getLeft(), child.getLastPlayedMove().getRight()));
                            heuristic += child.getHeuristic() * prob * remainingProbability;
                            child.setLastPlayedProbability(prob * remainingProbability);
                            opponentTilesClone.put(new Tile(child.getLastPlayedMove().getLeft(), child.getLastPlayedMove().getRight()), 0.0);
                            ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTilesClone, prob);
                            remainingProbability -= prob * remainingProbability;
                        }
                        if (nodeRound.getBazaarNodeRound() != null && !ComparisonHelper.equal(remainingProbability, 0.0)) {
                            nodeRound.getBazaarNodeRound().setLastPlayedProbability(remainingProbability);
                            heuristic += nodeRound.getBazaarNodeRound().getHeuristic() * remainingProbability;
                        }
                        nodeRound.setHeuristic(heuristic);
                    }
                } else {
                    nodeRound.setHeuristic(getHeuristic(nodeRound.getRound()));
                }
            }
            logger.info("Button up MinMax for height " + entry.getKey() + " took " + (System.currentTimeMillis() - ms) + " ms");
        }
    }

    private int countTilesInMoves(List<Move> moves) {
        Set<Tile> tiles = new HashSet<>();
        for (Move move : moves) {
            tiles.add(new Tile(move.getLeft(), move.getRight()));
        }
        return tiles.size();
    }

    private boolean isLeafNodeRound(NodeRound nodeRound) {
        List<NodeRound> children = nodeRound.getChildren();
        return children == null || children.isEmpty() || children.get(children.size() - 1).getHeuristic() == null ||
                (nodeRound.getBazaarNodeRound() != null && nodeRound.getBazaarNodeRound().getHeuristic() == null);
    }

    private double getHeuristic(Round round) {
        if (round.getGameInfo().isFinished()) {
            return RoundHeuristicHelper.getFinishedGameHeuristic(round.getGameInfo(), CachedGames.getGameProperties(gameId).getPointsForWin());
        }
        if (isNewRound(round)) {
            return RoundHeuristicHelper.getFinishedRoundHeuristic(round.getGameInfo(), round.getTableInfo().isMyMove());
        }

        return getHeuristic(round, roundHeuristic);
    }

    private void addInQueue(NodeRound nodeRound) {
        iteration++;
        nodeRound.setId(iteration);
        nodeRoundsQueue.add(nodeRound);
    }
}
