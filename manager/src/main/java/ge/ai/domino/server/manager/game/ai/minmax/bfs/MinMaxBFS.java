package ge.ai.domino.server.manager.game.ai.minmax.bfs;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.ai.heuristic.ComplexRoundHeuristic;
import ge.ai.domino.server.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.server.manager.game.ai.heuristic.RoundHeuristicHelper;
import ge.ai.domino.server.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.server.manager.game.ai.minmax.MinMax;
import ge.ai.domino.server.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.server.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.server.manager.game.helper.ComparisonHelper;
import ge.ai.domino.server.manager.game.helper.filter.OpponentTilesFilter;
import ge.ai.domino.server.manager.game.helper.game.MoveHelper;
import ge.ai.domino.server.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.server.manager.game.move.AddForMeProcessor;
import ge.ai.domino.server.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.server.manager.game.move.MoveProcessor;
import ge.ai.domino.server.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.server.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.CloneUtil;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

public class MinMaxBFS extends MinMax {

    private final Logger logger = Logger.getLogger(MinMaxBFS.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final RoundHeuristic roundHeuristic = new ComplexRoundHeuristic();

    private final SysParam minMaxIteration = new SysParam("minMaxIteration", "100000");

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

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

        while (!nodeRoundsQueue.isEmpty() && iteration <= systemParameterManager.getIntegerParameterValue(minMaxIteration)) {
            NodeRound nr = nodeRoundsQueue.remove();
            processRoundNode(nr);
        }
        nodeRoundsQueue = null;

        applyBottomUpMinMax();
        CachedMinMax.setLastNodeRound(round.getGameInfo().getGameId(), nodeRound, false);
        logger.info("MinMaxBFSForCachedNodeRound took " + (System.currentTimeMillis() - ms) + "ms");
    }

    @Override
    public String getType() {
        return "BFS";
    }

    @SuppressWarnings("Duplicates")
    private AiPredictionsWrapper minMax(NodeRound nodeRound) throws DAIException {
        long ms = System.currentTimeMillis();
        List<Move> moves = getPossibleMoves(nodeRound.getRound());
        logger.info("Ai predictions:");
        if (moves.isEmpty()) {
            logger.info("No AIPrediction");
            return null;
        }
        if (moves.size() == 1) {
            if (systemParameterManager.getBooleanParameterValue(useMinMaxPredictor)) {
                new Thread(() -> {
                    try {
                        CachedMinMax.changeMinMaxInProgress(gameId, true);
                        minMaxForMoves(moves, nodeRound, ms);
                        if (new MinMaxPredictor().usePredictor()) {
                            int gameId = nodeRound.getRound().getGameInfo().getGameId();
                            if (CachedMinMax.isUseFirstChild(gameId)) {
                                CachedMinMax.changeUseFirstChild(gameId, false);
                                CachedMinMax.setLastNodeRound(gameId, nodeRound.getChildren().get(0), false);
                            } else {
                                CachedMinMax.setLastNodeRound(gameId, nodeRound, true);
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
            if (new MinMaxPredictor().usePredictor()) {
                CachedMinMax.setLastNodeRound(nodeRound.getRound().getGameInfo().getGameId(), nodeRound, true);
            }
            return aiPredictionsWrapper;
        }
    }

    private AiPredictionsWrapper minMaxForMoves(List<Move> moves, NodeRound nodeRound, long ms) throws DAIException {
        for (Move move : moves) {
            Round nextRound = playForMeProcessor.move(CloneUtil.getClone(nodeRound.getRound()), move, true);
            NodeRound nextNodeRound = new NodeRound();
            nextNodeRound.setRound(nextRound);
            nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
            nextNodeRound.setParent(nodeRound);
            nextNodeRound.setTreeHeight(1);
            nodeRound.getChildren().add(nextNodeRound);
            validateOpponentTiles(nextNodeRound, "playForMe");
            addInQueue(nextNodeRound);
        }

        while (!nodeRoundsQueue.isEmpty() && iteration <= systemParameterManager.getIntegerParameterValue(minMaxIteration)) {
            NodeRound nr = nodeRoundsQueue.remove();
            processRoundNode(nr);
        }
        nodeRoundsQueue = null;

        applyBottomUpMinMax();

        AiPrediction bestAiPrediction = null;
        NodeRound bestNodeRound = null;
        List<AiPrediction> aiPredictions = new ArrayList<>();

        for (NodeRound nr : nodeRoundsByHeight.get(1)) {
            Move move = TileAndMoveHelper.getMove(nr.getLastPlayedMove());
            AiPrediction aiPrediction = new AiPrediction();
            aiPrediction.setMove(move);
            aiPrediction.setHeuristicValue(nr.getHeuristic());
            aiPredictions.add(aiPrediction);
            if (bestAiPrediction == null || bestAiPrediction.getHeuristicValue() < aiPrediction.getHeuristicValue()) {
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

        double tookMs = System.currentTimeMillis() - ms;
        logger.info("MinMaxBFS took " + tookMs + "ms, iteration " + iteration + ", average " + (tookMs / iteration));
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

        if (iteration >= systemParameterManager.getIntegerParameterValue(minMaxIteration)) {
            return;
        }
        Round round = nodeRound.getRound();

        if (round.getGameInfo().isFinished() || isNewRound(round)) {
            return;
        }

        List<Move> moves = getPossibleMoves(round);
        if (round.getTableInfo().isMyMove()) {
            if (!moves.isEmpty()) {
                for (Move move : moves) {
                    Round nextRound = playForMeProcessor.move(CloneUtil.getClone(round), move, true);
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
                        Round nextRound = addForMeProcessor.move(CloneUtil.getClone(round), move, true);
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
                Round nextRound = playForOpponentProcessor.move(CloneUtil.getClone(round), move, true);
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
            if (moves.size() + bazaarTilesCount <= round.getTableInfo().getBazaarTilesCount()) {
                Round nextRound = addForOpponentProcessor.move(CloneUtil.getClone(round), null, true);
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
        for (List<NodeRound> nodeRounds : nodeRoundsByHeight.values()) {
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
                            heuristic += nodeRound.getBazaarNodeRound().getHeuristic() * remainingProbability;
                        }
                        nodeRound.setHeuristic(heuristic);
                    }
                } else {
                    nodeRound.setHeuristic(getHeuristic(nodeRound.getRound()));
                }
            }
        }
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
        return roundHeuristic.getHeuristic(round);
    }

    private void addInQueue(NodeRound nodeRound) {
        iteration++;
        nodeRound.setId(iteration);
        nodeRoundsQueue.add(nodeRound);
    }
}
