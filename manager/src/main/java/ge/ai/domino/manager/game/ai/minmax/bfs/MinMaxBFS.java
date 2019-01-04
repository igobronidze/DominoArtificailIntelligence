package ge.ai.domino.manager.game.ai.minmax.bfs;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.ai.minmax.MinMax;
import ge.ai.domino.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.game.helper.filter.OpponentTilesFilter;
import ge.ai.domino.manager.game.helper.play.GameOperations;
import ge.ai.domino.manager.game.helper.play.MoveHelper;
import ge.ai.domino.manager.game.helper.play.PossibleMovesManager;
import ge.ai.domino.manager.game.helper.play.ProbabilitiesDistributor;
import ge.ai.domino.serverutil.CloneUtil;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class MinMaxBFS extends MinMax {

    private final Logger logger = Logger.getLogger(MinMaxBFS.class);

    private final SysParam minMaxIteration = new SysParam("minMaxIteration", "100000");

    private int iteration;

    protected Map<Integer, List<NodeRound>> nodeRoundsByHeight = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));

    private Queue<NodeRound> nodeRoundsQueue = new LinkedList<>();

    @Override
    public AiPredictionsWrapper solve(Round round) throws DAIException {
        NodeRound nodeRound = new NodeRound();
        nodeRound.setRound(round);

        List<Move> moves = PossibleMovesManager.getPossibleMoves(nodeRound.getRound(), false);
        if (moves.isEmpty()) {
            logger.info("There are no AIPrediction");
            return null;
        }

        if (moves.size() == 1 && systemParameterManager.getBooleanParameterValue(bestMoveAutoPlay)) {
            if (systemParameterManager.getBooleanParameterValue(useMinMaxPredictor)) {
                new Thread(() -> {
                    try {
                        CachedMinMax.changeMinMaxInProgress(round.getGameInfo().getGameId(), true);
                        threadCount = systemParameterManager.getIntegerParameterValue(minMaxForCachedNodeRoundIterationRate); // For minmax performance time
                        minMaxForNodeRound(nodeRound);
                        if (new MinMaxPredictor().usePredictor()) {
                            int gameId = nodeRound.getRound().getGameInfo().getGameId();
                            if (CachedMinMax.isUseFirstChild(gameId)) {
                                CachedMinMax.changeUseFirstChild(gameId, false);
                                CachedMinMax.setCachedPrediction(gameId, GameOperations.fillCachedPrediction(nodeRound.getChildren().get(0).getRound(), CachedPrediction.getCachedPrediction(nodeRound.getChildren().get(0), 1)), false);
                            } else {
                                CachedMinMax.setCachedPrediction(gameId, CachedPrediction.getCachedPrediction(nodeRound, 2), true);
                            }
                        }
                        CachedMinMax.changeMinMaxInProgress(round.getGameInfo().getGameId(), false);
                    } catch (DAIException ex) {
                        logger.error(ex);
                    }
                }).start();
            }
            AiPredictionsWrapper aiPredictionsWrapper = new AiPredictionsWrapper();
            AiPrediction aiPrediction = new AiPrediction();
            aiPrediction.setMove(moves.get(0));
            aiPrediction.setHeuristicValue(Integer.MIN_VALUE);
            aiPrediction.setRealHeuristic(Integer.MIN_VALUE);
            aiPrediction.setMoveProbability(1.0);
            aiPredictionsWrapper.getAiPredictions().add(aiPrediction);
            return aiPredictionsWrapper;
        } else {
            AiPredictionsWrapper aiPredictionsWrapper = minMaxForNodeRound(nodeRound);
            if (new MinMaxPredictor().usePredictor()) {
                CachedMinMax.setCachedPrediction(nodeRound.getRound().getGameInfo().getGameId(), CachedPrediction.getCachedPrediction(nodeRound, 2), true);
            }
            return aiPredictionsWrapper;
        }
    }

    @Override
    public void minMaxForCachedNodeRound(Round round) throws DAIException {
        logger.info("Executing MinMaxBFSForCachedNodeRound gameId[" + round.getGameInfo().getGameId() + "]");

        threadCount = systemParameterManager.getIntegerParameterValue(minMaxForCachedNodeRoundIterationRate);  // For minmax performance time

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
    }

    @Override
    public String getType() {
        return "BFS";
    }

    @SuppressWarnings("Duplicates")
    @Override
    public AiPredictionsWrapper minMaxForNodeRound(NodeRound nodeRound) throws DAIException {
        logger.info("Executing MinMaxBFS minMaxForNodeRound method gameId[" +  nodeRound.getRound().getGameInfo().getGameId() + "]");
        nodeRound.setTreeHeight(1);
        addInQueue(nodeRound);

        if (nodeRound.getRound().getGameInfo().isFinished() || isNewRound(nodeRound.getRound())) {
            logger.info("Round is finished");
            return new AiPredictionsWrapper();
        }

        long ms = System.currentTimeMillis();
        long inlineMs = System.currentTimeMillis();

        while (!nodeRoundsQueue.isEmpty()) {
            NodeRound nr = nodeRoundsQueue.remove();
            processRoundNode(nr);
        }
        nodeRoundsQueue = null;
        logger.info("Processing node round took " + (System.currentTimeMillis() - inlineMs) + " ms");
        inlineMs = System.currentTimeMillis();

        applyBottomUpMinMax();
        logger.info("Button up MinMax took " + (System.currentTimeMillis() - inlineMs) + " ms");

        double tookMs = System.currentTimeMillis() - ms;
        logger.info("MinMaxBFS took " + tookMs + " ms, iteration " + iteration + ", average " + (tookMs / iteration));

        return getAiPredictionsWrapper();
    }

    AiPredictionsWrapper getAiPredictionsWrapper() {
        List<AiPrediction> aiPredictions = new ArrayList<>();

        for (NodeRound nr : nodeRoundsByHeight.get(2)) {
            Move move = TileAndMoveHelper.getMove(nr.getLastPlayedMove());
            AiPrediction aiPrediction = new AiPrediction();
            aiPrediction.setMove(move);
            aiPrediction.setHeuristicValue(nr.getHeuristic());
            aiPrediction.setRealHeuristic(roundHeuristic.getHeuristic(nr.getRound(), false));
            aiPrediction.setMoveProbability(nr.getLastPlayedProbability());
            aiPredictions.add(aiPrediction);
        }

        aiPredictions.sort((o1, o2) -> Double.compare(o2.getHeuristicValue(), o1.getHeuristicValue()));
        logger.info("AIPredictions: ");
        for (AiPrediction aiPrediction : aiPredictions) {
            Move move = aiPrediction.getMove();
            logger.info("PlayedMove: " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + aiPrediction.getHeuristicValue());
        }

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

        if (nodeRound.getRound().getTableInfo().isMyMove()) {
            addMyPlaysForNodeRound(nodeRound, true);
        } else {
            addOpponentPlaysForNodeRound(nodeRound);
        }
    }

    void addOpponentPlaysForNodeRound(NodeRound nodeRound) throws DAIException {
        Round round = nodeRound.getRound();
        if (round.getGameInfo().isFinished() || isNewRound(round)) {
            return;
        }

        List<Move> moves = PossibleMovesManager.getPossibleMoves(round, false);

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
            nodeRound.getBazaarNodeRounds().add(nextNodeRound);
            validateOpponentTiles(nextNodeRound, "addForOpponent");
            addInQueue(nextNodeRound);
        }
    }

    void addMyPlaysForNodeRound(NodeRound nodeRound, boolean addBazaarPlay) throws DAIException {
        Round round = nodeRound.getRound();
        if (round.getGameInfo().isFinished() || isNewRound(round)) {
            return;
        }

        List<Move> moves = PossibleMovesManager.getPossibleMoves(round, false);
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
        } else if (addBazaarPlay) {
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
                    nodeRound.getBazaarNodeRounds().add(nextNodeRound);
                    validateOpponentTiles(nextNodeRound, "addForMe");
                    addInQueue(nextNodeRound);
                }
            }
        }
    }

    void applyBottomUpMinMax() {
        for (Map.Entry<Integer, List<NodeRound>> entry : nodeRoundsByHeight.entrySet()) {
            long ms = System.currentTimeMillis();
            List<NodeRound> nodeRounds = entry.getValue();

            for (NodeRound nodeRound : nodeRounds) {
                if (nodeRound.getHeuristic() == null) {
                    if (!isLeafNodeRound(nodeRound)) {
                        if (nodeRound.getRound().getTableInfo().isMyMove()) {
                            if (nodeRound.getChildren().isEmpty()) {
                                double heuristic = 0;
                                for (NodeRound bazaarNodeRound : nodeRound.getBazaarNodeRounds()) {
                                    heuristic += bazaarNodeRound.getHeuristic() * bazaarNodeRound.getLastPlayedProbability();
                                    nodeRound.addDescendant(bazaarNodeRound.getDescendant());
                                }
                                nodeRound.setHeuristic(heuristic);
                            } else {
                                NodeRound bestNodeRound = null;
                                for (NodeRound child : nodeRound.getChildren()) {
                                    if (bestNodeRound == null || child.getHeuristic() > bestNodeRound.getHeuristic()) {
                                        if (bestNodeRound != null) {
                                            bestNodeRound.setLastPlayedProbability(0.0);
                                        }
                                        bestNodeRound = child;
                                        bestNodeRound.setLastPlayedProbability(1.0);
                                    }
                                    nodeRound.addDescendant(child.getDescendant());
                                }
                                nodeRound.setHeuristic(bestNodeRound.getHeuristic());
                            }
                        } else {
                            double heuristic = 0.0;
                            double remainingProbability = 1.0;

                            nodeRound.getChildren().sort(Comparator.comparing(NodeRound::getHeuristic));
                            if (nodeRound.getLastPlayedMove() != null && nodeRound.getLastPlayedMove().getType() == MoveType.ADD_FOR_OPPONENT) {
                                for (NodeRound child : nodeRound.getChildren()) {
                                    Tile childTile = new Tile(child.getLastPlayedMove().getLeft(), child.getLastPlayedMove().getRight());
                                    double prob = nodeRound.getOpponentTilesClone().get(childTile);
                                    nodeRound.getOpponentTilesClone().put(childTile, 0.0);
                                    heuristic += child.getHeuristic() * prob;
                                    child.setLastPlayedProbability(prob);
                                    remainingProbability -= prob;
                                    nodeRound.addDescendant(child.getDescendant());
                                }
                            } else {
                                for (NodeRound child : nodeRound.getChildren()) {
                                    if (!ComparisonHelper.equal(remainingProbability, 0.0)) {
                                        Map<Tile, Double> opponentTilesClone = nodeRound.getOpponentTilesClone();
                                        double prob = opponentTilesClone.get(new Tile(child.getLastPlayedMove().getLeft(), child.getLastPlayedMove().getRight()));
                                        heuristic += child.getHeuristic() * prob * remainingProbability;
                                        child.setLastPlayedProbability(prob * remainingProbability);
                                        opponentTilesClone.put(new Tile(child.getLastPlayedMove().getLeft(), child.getLastPlayedMove().getRight()), 0.0);
                                        ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTilesClone, prob);
                                        remainingProbability -= prob * remainingProbability;
                                    }
                                    nodeRound.addDescendant(child.getDescendant());
                                }
                            }
                            if (!nodeRound.getBazaarNodeRounds().isEmpty()) {
                                NodeRound bazaarNodeRound = nodeRound.getBazaarNodeRounds().get(0);
                                nodeRound.addDescendant(bazaarNodeRound.getDescendant());
                                if (!ComparisonHelper.equal(remainingProbability, 0.0)) {
                                    bazaarNodeRound.setLastPlayedProbability(remainingProbability);
                                    heuristic += bazaarNodeRound.getHeuristic() * remainingProbability;
                                }
                            }
                            nodeRound.setHeuristic(heuristic);
                        }
                    } else {
                        nodeRound.setHeuristic(roundHeuristic.getHeuristic(nodeRound.getRound(), false));
                    }
                    nodeRound.addDescendant(1);
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
        List<NodeRound> bazaarNodeRounds = nodeRound.getBazaarNodeRounds();

        if (children.isEmpty() && bazaarNodeRounds.isEmpty()) {
            return true;
        }
        if (!children.isEmpty()) {
            if (children.get(children.size() - 1).getHeuristic() == null) {
                return true;
            }
        }
        if (!bazaarNodeRounds.isEmpty()) {
            if (bazaarNodeRounds.get(bazaarNodeRounds.size() - 1).getHeuristic() == null) {
                return true;
            }
        }

        return false;
    }

    private void addInQueue(NodeRound nodeRound) {
        iteration++;
        nodeRound.setId(iteration);
        nodeRoundsQueue.add(nodeRound);
    }
}
