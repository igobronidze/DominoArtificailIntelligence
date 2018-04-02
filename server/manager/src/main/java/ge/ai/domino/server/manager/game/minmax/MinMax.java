package ge.ai.domino.server.manager.game.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.*;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.helper.ComparisonHelper;
import ge.ai.domino.server.manager.game.helper.MoveHelper;
import ge.ai.domino.server.manager.game.heuristic.ComplexRoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristicHelper;
import ge.ai.domino.server.manager.game.move.AddForMeProcessor;
import ge.ai.domino.server.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.server.manager.game.move.MoveProcessor;
import ge.ai.domino.server.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.server.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.server.manager.game.validator.OpponentTilesValidator;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class MinMax {

    private static Logger logger = Logger.getLogger(MinMax.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final RoundHeuristic roundHeuristic = new ComplexRoundHeuristic();

    private final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "7");

    private OpponentTilesValidator opponentTilesValidator = new OpponentTilesValidator();

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor(opponentTilesValidator);

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor(opponentTilesValidator);

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor(opponentTilesValidator);

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor(opponentTilesValidator);

    private int treeHeight;

    private int recursionCount;

    private int gameId;

    public List<AiPrediction> minMax(Round round) throws DAIException {
        long ms = System.currentTimeMillis();
        this.gameId = round.getGameInfo().getGameId();
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);
        List<Move> moves = getPossibleMoves(round);
        Round bestRound = null;
        AiPrediction bestAiPrediction = null;
        List<AiPrediction> aiPredictions = new ArrayList<>();
        logger.info("Ai predictions:");
        for (Move move : moves) {
            Round nextRound = playForMeProcessor.move(CloneUtil.getClone(round), move, true);
            nextRound.setParentRound(new ParentRound(round, MoveHelper.getPlayForMeMove(move), -1.0F, 0));
            float heuristic = getHeuristicValue(nextRound, 1);
            AiPrediction aiPrediction = new AiPrediction();
            aiPrediction.setMove(move);
            aiPrediction.setHeuristicValue(heuristic);
            aiPredictions.add(aiPrediction);
            if (bestAiPrediction == null || bestAiPrediction.getHeuristicValue() < aiPrediction.getHeuristicValue()) {
                if (bestAiPrediction != null) {
                    bestAiPrediction.setBestMove(false);
                }
                bestAiPrediction = aiPrediction;
                bestAiPrediction.setBestMove(true);
                if (bestRound != null) {
                    bestRound.getParentRound().setProbability(0.0F);
                }
                bestRound = nextRound;
                bestRound.getParentRound().setProbability(1.0F);
            }
            logger.info("PlayedMove- " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + heuristic);
        }
        float tookMs = System.currentTimeMillis() - ms;
        logger.info("MinMax took " + tookMs + "ms, recursion count " + recursionCount + ", average " + (tookMs / recursionCount));
        recursionCount = 0;
        if (aiPredictions.isEmpty() || bestAiPrediction == null) {
            logger.info("No AIPrediction");
            return null;
        }
        round.setHeuristicValue(bestAiPrediction.getHeuristicValue());
        logger.info("AIPrediction is [" + bestAiPrediction.getMove().getLeft() + "-" + bestAiPrediction.getMove().getRight() + " " +
                bestAiPrediction.getMove().getDirection().name() + "], " + "heuristic: " + bestAiPrediction.getHeuristicValue());
        opponentTilesValidator.applyValidation();
        return aiPredictions;
    }

    private float getHeuristicValue(Round round, int height) throws DAIException {
        recursionCount++;
        TableInfo tableInfo = round.getTableInfo();
        GameInfo gameInfo = round.getGameInfo();

        // Recursion end conditions: Game is finished, Started new round, reached tree root
        if (round.getGameInfo().isFinished()) {
            return RoundHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedGames.getGameProperties(gameId).getPointsForWin());
        }
        if (isNewRound(round)) {
            round.setHeuristicValue(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, !tableInfo.isMyMove()));
            return round.getHeuristicValue();
        }
        if (height == treeHeight) {
            round.setHeuristicValue(roundHeuristic.getHeuristic(round));
            return round.getHeuristicValue();
        }

        List<Move> moves = getPossibleMoves(round);
        if (round.getTableInfo().isMyMove()) {
            // Best move for me
            Round bestRound = null;
            for (Move move : moves) {
                Round nextRound = playForMeProcessor.move(CloneUtil.getClone(round), move, true);
                nextRound.setParentRound(new ParentRound(round, MoveHelper.getPlayForMeMove(move), 0.0F, height));
                getHeuristicValue(nextRound, height + 1);
                if (bestRound == null || nextRound.getHeuristicValue() > bestRound.getHeuristicValue()) {
                    if (bestRound != null) {
                        bestRound.getParentRound().setProbability(0.0F);
                    }
                    bestRound = nextRound;
                    bestRound.getParentRound().setProbability(1.0F);
                }
            }
            // If there are no available move, use bazaar tiles
            if (bestRound == null) {
                float heuristic = 0.0F;
                float bazaarProbSum = round.getTableInfo().getBazaarTilesCount();
                for (Map.Entry<Tile, Float> entry : round.getOpponentTiles().entrySet()) {
                    Tile tile = entry.getKey();
                    float prob = entry.getValue();
                    if (prob != 1.0) {
                        float probForPickTile = (1 - prob) / bazaarProbSum; // Probability fot choose this tile
                        Move move = new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT);
                        Round nextRound = addForMeProcessor.move(CloneUtil.getClone(round), move, true);
                        nextRound.setParentRound(new ParentRound(round, MoveHelper.getAddTileForMeMove(move), 1.0F, height));
                        heuristic += getHeuristicValue(nextRound, height + 1) * probForPickTile;
                    }
                }
                round.setHeuristicValue(heuristic);
                return heuristic;
            } else {
                round.setHeuristicValue(bestRound.getHeuristicValue());
                return round.getHeuristicValue();
            }
        } else {
            // Possible moves sorted ASC
            Queue<Round> possibleRounds = new PriorityQueue<>((o1, o2) -> Float.compare(o1.getHeuristicValue(), o2.getHeuristicValue()));
            // Play all possible move and add in queue
            for (Move move : moves) {
                Round nextRound = playForOpponentProcessor.move(CloneUtil.getClone(round), move, true);
                nextRound.setParentRound(new ParentRound(round, MoveHelper.getPlayForOpponentMove(move), 0.0F, height));
                getHeuristicValue(nextRound, height + 1);
                possibleRounds.add(nextRound);
            }

            int canNotPlayTilesCount = (int)round.getOpponentTiles().values().stream().filter(prob -> prob == 0).count();   // Tile count which opponent didn't play
            float heuristic = 0.0F;
            float remainingProbability = 1.0F;
            for (Round nextRound : possibleRounds) {
                float prob;
                // Can't move more tile than there are in bazaar or was played
                if (canNotPlayTilesCount > tableInfo.getBazaarTilesCount()) {
                    break;
                } else if (canNotPlayTilesCount == tableInfo.getBazaarTilesCount()) {
                    prob = remainingProbability;   // Last chance to play
                } else {
                    prob = remainingProbability * nextRound.getTableInfo().getLastPlayedProb();
                }
                heuristic += nextRound.getHeuristicValue() * prob;
                nextRound.getParentRound().setProbability(prob);
                remainingProbability -= prob;

                canNotPlayTilesCount++;
            }
            // Bazaar case
            if (!ComparisonHelper.equal(remainingProbability, 0)) {
                Round addedRound = addForOpponentProcessor.move(CloneUtil.getClone(round), null, true);
                addedRound.setParentRound(new ParentRound(round, MoveHelper.getAddTileForOpponentMove(), remainingProbability, height));
                heuristic += getHeuristicValue(addedRound, height + 1) * remainingProbability;
            }
            round.setHeuristicValue(heuristic);
            return heuristic;
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
}
