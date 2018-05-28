package ge.ai.domino.server.manager.game.ai.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.ai.AiSolver;
import ge.ai.domino.server.manager.game.ai.heuristic.ComplexRoundHeuristic;
import ge.ai.domino.server.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.server.manager.game.ai.heuristic.RoundHeuristicHelper;
import ge.ai.domino.server.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.server.manager.game.helper.ComparisonHelper;
import ge.ai.domino.server.manager.game.helper.MoveHelper;
import ge.ai.domino.server.manager.game.helper.ProbabilitiesDistributor;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.move.AddForMeProcessor;
import ge.ai.domino.server.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.server.manager.game.move.MoveProcessor;
import ge.ai.domino.server.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.server.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MinMaxBFS implements AiSolver {

    private final Logger logger = Logger.getLogger(MinMaxBFS.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final RoundHeuristic roundHeuristic = new ComplexRoundHeuristic();

    private final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

    private final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "8");

    private final SysParam minMaxIteration = new SysParam("minMaxIteration", "10000");

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

    private int treeHeight;

    private int iteration;

    private int gameId;

    private NodeRound notValidRound;

    private String errorMsg;

    private String errorMsgKey;

    private Map<Integer, List<NodeRound>> nodeRoundsByHeight = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));

    @Override
    public AiPredictionsWrapper solve(Round round) throws DAIException {
        this.gameId = round.getGameInfo().getGameId();
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);

        NodeRound nodeRound = new NodeRound();
        nodeRound.setRound(round);
        AiPredictionsWrapper aiPredictionsWrapper = minMax(nodeRound);
        if (new MinMaxPredictor().usePredictor()) {
            CachedMinMax.setLastNodeRound(round.getGameInfo().getGameId(), nodeRound);
        }
        return aiPredictionsWrapper;
    }

    public void minMaxForCachedNodeRound(Round round) throws DAIException {
        this.gameId = round.getGameInfo().getGameId();
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);

        NodeRound nodeRound = new NodeRound();
        nodeRound.setRound(round);
        nodeRoundsByHeight.put(1, new ArrayList<>());
        nodeRoundsByHeight.get(1).add(nodeRound);
        getHeuristicValue(nodeRound, 2);
        applyBottomUpMinMax();
        CachedMinMax.setLastNodeRound(round.getGameInfo().getGameId(), nodeRound);
    }

    private AiPredictionsWrapper minMax(NodeRound nodeRound) throws DAIException {
        long ms = System.currentTimeMillis();
        List<Move> moves = getPossibleMoves(nodeRound.getRound());
        logger.info("Ai predictions:");
        if (moves.isEmpty()) {
            logger.info("No AIPrediction");
            return null;
        }
        for (Move move : moves) {
            iteration++;
            Round nextRound = playForMeProcessor.move(CloneUtil.getClone(nodeRound.getRound()), move, true);
            NodeRound nextNodeRound = new NodeRound();
            nextNodeRound.setRound(nextRound);
            nextNodeRound.setLastPlayedMove(MoveHelper.getPlayForMeMove(move));
            nextNodeRound.setParent(nodeRound);
            nextNodeRound.setTreeHeight(1);
            nodeRound.getChildren().add(nextNodeRound);
            validateOpponentTiles(nextNodeRound, "playForMe");
            nodeRoundsByHeight.computeIfAbsent(1, k -> new ArrayList<>());
            nodeRoundsByHeight.get(1).add(nodeRound);
            getHeuristicValue(nextNodeRound, 2);
        }
        applyBottomUpMinMax();

        AiPrediction bestAiPrediction = null;
        NodeRound bestNodeRound = null;
        List<AiPrediction> aiPredictions = new ArrayList<>();

        for (NodeRound nr : nodeRoundsByHeight.get(1)) {
            Move move = new Move(nr.getLastPlayedMove().getLeft(), nr.getLastPlayedMove().getRight(), nr.getLastPlayedMove().getDirection());
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
        logger.info("MinMaxDFS took " + tookMs + "ms, iteration " + iteration + ", average " + (tookMs / iteration));
        iteration = 0;
        nodeRound.setHeuristic(bestAiPrediction.getHeuristicValue());
        logger.info("AIPrediction is [" + bestAiPrediction.getMove().getLeft() + "-" + bestAiPrediction.getMove().getRight() + " " +
                bestAiPrediction.getMove().getDirection().name() + "], " + "heuristic: " + bestAiPrediction.getHeuristicValue());

        AiPredictionsWrapper aiPredictionsWrapper = new AiPredictionsWrapper();
        aiPredictionsWrapper.setAiPredictions(aiPredictions);
        aiPredictionsWrapper.setWarnMsgKey(applyValidation());
        return aiPredictionsWrapper;
    }

    private void getHeuristicValue(NodeRound nodeRound, int height) throws DAIException {
        if (iteration >= systemParameterManager.getIntegerParameterValue(minMaxIteration)) {
            return;
        }
        iteration++;
        nodeRoundsByHeight.computeIfAbsent(height, k -> new ArrayList<>());
        nodeRoundsByHeight.get(height).add(nodeRound);
        Round round = nodeRound.getRound();
        TableInfo tableInfo = round.getTableInfo();
        GameInfo gameInfo = round.getGameInfo();

        // Recursion end conditions: Game is finished, Started new round, reached tree root
        if (round.getGameInfo().isFinished()) {
            nodeRound.setHeuristic(RoundHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedGames.getGameProperties(gameId).getPointsForWin()));
            return;
        }
        if (isNewRound(round)) {
            nodeRound.setHeuristic(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, tableInfo.isMyMove()));
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
                    nextNodeRound.setTreeHeight(height);
                    nodeRound.getChildren().add(nextNodeRound);
                    validateOpponentTiles(nextNodeRound, "playForMe");
                    getHeuristicValue(nextNodeRound, height + 1);
                }
            } else {
                double bazaarProbSum = round.getTableInfo().getBazaarTilesCount();
                for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
                    Tile tile = entry.getKey();
                    double prob = entry.getValue();
                    if (prob != 1.0) {
                        double probForPickTile = (1 - prob) / bazaarProbSum; // Probability fot choose this tile
                        Move move = new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT);
                        Round nextRound = addForMeProcessor.move(CloneUtil.getClone(round), move, true);
                        NodeRound nextNodeRound = new NodeRound();
                        nextNodeRound.setRound(nextRound);
                        nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForMeMove(move));
                        nextNodeRound.setParent(nodeRound);
                        nextNodeRound.setTreeHeight(height);
                        nextNodeRound.setLastPlayedProbability(probForPickTile);
                        nodeRound.getChildren().add(nextNodeRound);
                        validateOpponentTiles(nextNodeRound, "addForMe");
                        getHeuristicValue(nextNodeRound, height + 1);
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
                nextNodeRound.setTreeHeight(height);
                nodeRound.getChildren().add(nextNodeRound);
                validateOpponentTiles(nextNodeRound, "playForOpponent");
                getHeuristicValue(nextNodeRound, height + 1);
            }
            if (moves.size() <= round.getTableInfo().getBazaarTilesCount()) {
                Round nextRound = addForOpponentProcessor.move(CloneUtil.getClone(round), null, true);
                NodeRound nextNodeRound = new NodeRound();
                nextNodeRound.setRound(nextRound);
                nextNodeRound.setLastPlayedMove(MoveHelper.getAddTileForOpponentMove());
                nextNodeRound.setParent(nodeRound);
                nextNodeRound.setTreeHeight(height);
                nodeRound.setBazaarNodeRound(nextNodeRound);
                validateOpponentTiles(nextNodeRound, "addForOpponent");
                getHeuristicValue(nextNodeRound, height + 1);
            }
        }
    }

    private void applyBottomUpMinMax() {
        for (List<NodeRound> nodeRounds : nodeRoundsByHeight.values()) {
            for (NodeRound nodeRound : nodeRounds) {
                if (nodeRound.getHeuristic() == null) {
                    if (nodeRound.getChildren() != null && !nodeRound.getChildren().isEmpty()) {
                        if (nodeRound.getRound().getTableInfo().isMyMove()) {
                            NodeRound bestNodeRound = null;
                            for (NodeRound child : nodeRound.getChildren()) {
                                if (bestNodeRound == null || child.getHeuristic() > bestNodeRound.getHeuristic()) {
                                    if (bestNodeRound != null) {
                                        bestNodeRound.setLastPlayedProbability(0.0);
                                    }
                                    bestNodeRound = child;
                                    bestNodeRound.setLastPlayedProbability(1.0);
                                }
                            }
                            nodeRound.setHeuristic(bestNodeRound.getHeuristic());
                        } else {
                            // Possible node rounds sorted ASC
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
                        nodeRound.setHeuristic(roundHeuristic.getHeuristic(nodeRound.getRound()));
                    }
                }
            }
        }
    }

    private List<Move> getPossibleMoves(Round round) {
        List<Move> moves = new ArrayList<>();
        TableInfo tableInfo = round.getTableInfo();
        PlayedTile left = tableInfo.getLeft();
        PlayedTile right = tableInfo.getRight();
        PlayedTile top = tableInfo.getTop();
        PlayedTile bottom = tableInfo.getBottom();
        // First move
        if (tableInfo.getLeft() == null) {
            moves.addAll(round.getMyTiles().stream().map(tile -> new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT)).collect(Collectors.toList()));
        } else {
            if (round.getTableInfo().isMyMove()) {
                for (Tile tile : round.getMyTiles()) {
                    addPossibleMovesForTile(tile, left, right, top, bottom, moves);
                }
            } else {
                round.getOpponentTiles().entrySet().stream().filter(entry -> entry.getValue() > 0.0).forEach(
                        entry -> addPossibleMovesForTile(entry.getKey(), left, right, top, bottom, moves));
            }
        }
        return moves;
    }

    private void addPossibleMovesForTile(Tile tile, PlayedTile left, PlayedTile right, PlayedTile top, PlayedTile bottom, List<Move> moves) {
        Set<Integer> played = new HashSet<>();
        // LEFT RIGHT TOP BOTTOM sequence is important
        if (!played.contains(hashForPlayedTile(left))) {
            if (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight()) {
                moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT));
                played.add(hashForPlayedTile(left));
            }
        }
        if (!played.contains(hashForPlayedTile(right))) {
            if (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight()) {
                moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.RIGHT));
                played.add(hashForPlayedTile(right));
            }
        }
        if (top != null && !played.contains(hashForPlayedTile(top))) {
            if ((top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
                moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.TOP));
                played.add(hashForPlayedTile(top));
            }
        }
        if (bottom != null && !played.contains(hashForPlayedTile(bottom))) {
            if ((bottom.getOpenSide() == tile.getLeft() || bottom.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
                moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.BOTTOM));
                played.add(hashForPlayedTile(bottom));
            }
        }
    }

    private int hashForPlayedTile(PlayedTile playedTile) {
        int p = 10;
        return (playedTile.getOpenSide() + 1) * (playedTile.isTwin() ? p : 1);
    }

    private boolean isNewRound(Round round) {
        return round.getMyTiles().size() == 0 && round.getTableInfo().getOpponentTilesCount() == 7 && round.getTableInfo().getBazaarTilesCount() == 21;
    }

    private void validateOpponentTiles(NodeRound nodeRound, String msg) {
        if (systemParameterManager.getBooleanParameterValue(checkOpponentProbabilities)) {
            double sum = 0.0;
            Round round = nodeRound.getRound();
            for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
                double prob = entry.getValue();
                int left = entry.getKey().getLeft();
                int right = entry.getKey().getRight();
                if (prob > 1.0) {
                    notValidRound = nodeRound;
                    errorMsg = "Opponent tile probability is more than one, tile[" + left + "-" + right + "] method[" + msg + "]";
                    errorMsgKey = "opponentTileProbabilityIsMoreThanOne";
                    break;
                } else if (prob < 0.0) {
                    notValidRound = nodeRound;
                    errorMsg = "Opponent tile probability is less than zero, tile[" + left + "-" + right + "] method[" + msg + "]";
                    errorMsgKey = "opponentTileProbabilityIsLessThanZero";
                }
                sum += prob;
            }
            if (!ComparisonHelper.equal(sum, round.getTableInfo().getOpponentTilesCount())) {
                notValidRound = nodeRound;
                errorMsg = "Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + ", method[" + msg + "]";
                errorMsgKey = "probabilitiesSumIsNoEqualToOpponentTilesCount";
            }
        }
    }

    private String applyValidation() {
        if (notValidRound != null) {
            logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            logger.warn(errorMsg);
            logger.info("Rounds full info");

            List<NodeRound> parentRounds = new ArrayList<>();
            while (notValidRound.getParent() != null) {
                parentRounds.add(notValidRound);
                notValidRound = notValidRound.getParent();
            }
            GameLoggingProcessor.logRoundFullInfo(notValidRound.getRound(), false); // Still print if virtual
            for (int i = parentRounds.size() - 1; i >= 0; i--) {
                notValidRound = parentRounds.get(i);
                logger.info("Height: " + notValidRound.getTreeHeight());
                logger.info("Play move with probability[" + notValidRound.getLastPlayedProbability() + "], move[" + notValidRound.getLastPlayedMove() + "]");
                GameLoggingProcessor.logRoundFullInfo(notValidRound.getRound(), false); // Still print if virtual
            }

            logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            return errorMsgKey;
        }
        return null;
    }
}
