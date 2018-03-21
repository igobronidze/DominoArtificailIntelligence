package ge.ai.domino.server.manager.game.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.helper.CloneUtil;
import ge.ai.domino.server.manager.game.helper.ComparisonHelper;
import ge.ai.domino.server.manager.game.helper.GameOperations;
import ge.ai.domino.server.manager.game.heuristic.ComplexRoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristicHelper;
import ge.ai.domino.server.manager.game.move.AddForMeProcessor;
import ge.ai.domino.server.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.server.manager.game.move.MoveProcessor;
import ge.ai.domino.server.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.server.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
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

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

    private int treeHeight;

    private int recursionCount;

    private int gameId;

    public Move minMax(Round round) throws DAIException {
        long ms = System.currentTimeMillis();
        this.gameId = round.getGameInfo().getGameId();
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);
        List<Move> moves = getPossibleMoves(round);
        Move bestMove = null;
        float bestHeuristic = Integer.MIN_VALUE;
        for (Move move : moves) {
            Round nextRound = addForMeProcessor.move(CloneUtil.getClone(round), move, true);
            float heuristic = getHeuristicValue(nextRound, 1);
            if (bestMove == null || heuristic > bestHeuristic) {
                bestMove = move;
                bestHeuristic = heuristic;
            }
            logger.info("PlayedMove- " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + heuristic);
        }
        float tookMs = System.currentTimeMillis() - ms;
        logger.info("MinMax took " + tookMs + "ms, recursion count " + recursionCount + ", average " + (tookMs / recursionCount));
        recursionCount = 0;
        if (bestMove == null) {
            logger.info("No AIPrediction");
            return null;
        }
        round.setHeuristicValue(bestHeuristic);
        Move aiPrediction = new Move(bestMove.getLeft(), bestMove.getRight(), bestMove.getDirection());
        logger.info("AIPrediction is [" + bestMove.getLeft() + "-" + bestMove.getRight() + " " + bestMove.getDirection().name() + "], " + "heuristic: " + bestHeuristic);
        return aiPrediction;
    }

    private float getHeuristicValue(Round round, int height) throws DAIException {
        recursionCount++;
        TableInfo tableInfo = round.getTableInfo();
        GameInfo gameInfo = round.getGameInfo();
        // If game is blocked
        if (tableInfo.isOmittedMe() && tableInfo.isOmittedOpponent()) {
            int opponentTilesCount = GameOperations.countLeftTiles(round, false, true);
            int myTilesCount = GameOperations.countLeftTiles(round, true, false);
            if (myTilesCount < opponentTilesCount) {
                GameOperations.addLeftTiles(gameInfo, opponentTilesCount, true, 0, true);
            } else if (myTilesCount > opponentTilesCount) {
                GameOperations.addLeftTiles(gameInfo, myTilesCount, false, 0, true);
            }
            round.setHeuristicValue(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, tableInfo.isMyMove()));
            return round.getHeuristicValue();
        }
        if (gameInfo.isFinished()) {
            return RoundHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedGames.getGameProperties(gameId).getPointsForWin());
        }
        // If opponent omit, add him left tiles and return pure heuristic value
        if (tableInfo.isNeedToAddLeftTiles()) {
            int opponentTilesCount = GameOperations.countLeftTiles(round, false, true);
            GameOperations.addLeftTiles(gameInfo, opponentTilesCount, true, 0, true);
            round.setHeuristicValue(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, !tableInfo.isMyMove()));
            return round.getHeuristicValue();
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
                getHeuristicValue(nextRound, height + 1);
                if (bestRound == null || nextRound.getHeuristicValue() > bestRound.getHeuristicValue()) {
                    bestRound = nextRound;
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
                        heuristic += getHeuristicValue(addForMeProcessor.move(CloneUtil.getClone(round), new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT), true), height + 1) * probForPickTile;
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
            Queue<Round> possibleRounds = new PriorityQueue<>(new Comparator<Round>() {
                @Override
                public int compare(Round o1, Round o2) {
                    return Float.compare(o1.getHeuristicValue(), o2.getHeuristicValue());
                }
            });
            // Play all possible move and add in queue
            for (Move move : moves) {
                Round nextRound = playForOpponentProcessor.move(CloneUtil.getClone(round), move, true);
                getHeuristicValue(nextRound, height + 1);
                possibleRounds.add(nextRound);
            }

            int notPlayedTilesCount = (int)round.getOpponentTiles().values().stream().filter(prob -> prob == 0).count();   // Tile count which opponent didn't play
            float heuristic = 0.0F;
            float remainingProbability = 1.0F;
            for (Round nextRound : possibleRounds) {
                float prob;
                if (notPlayedTilesCount == tableInfo.getBazaarTilesCount()) {
                    prob = remainingProbability;   // Last chance to play
                } else {
                    prob = remainingProbability * nextRound.getTableInfo().getLastPlayedProb();
                }
                heuristic += nextRound.getHeuristicValue() * prob;
                remainingProbability -= prob;

                // Can't move more tile than there are in bazaar
                notPlayedTilesCount++;
                if (notPlayedTilesCount > tableInfo.getBazaarTilesCount()) {
                    break;

                }
            }
            // Bazaar case
            if (!ComparisonHelper.equal(remainingProbability, 0)) {
                Round addedRound = addForOpponentProcessor.move(CloneUtil.getClone(round), null, true);
                if (tableInfo.getBazaarTilesCount() > 2) {
                    float bazaarSum = tableInfo.getBazaarTilesCount();
                    float usedProb = 0.0F;
                    PlayedTile left = tableInfo.getLeft();
                    PlayedTile right = tableInfo.getRight();
                    PlayedTile top = tableInfo.getTop();
                    PlayedTile bottom = tableInfo.getBottom();
                    for (Tile tile : addedRound.getOpponentTiles().keySet()) {
                        if ((left != null && (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight())) ||
                                (right != null && (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight())) ||
                                (top != null && (top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight())) ||
                                (bottom != null && (bottom.getOpenSide() == tile.getLeft() || bottom.getOpenSide() == tile.getRight()))) {
                            Round cloneRound = CloneUtil.getClone(addedRound);
                            float prob = remainingProbability * 1 / bazaarSum;
                            usedProb += prob;
                            cloneRound.getOpponentTiles().put(tile, 1.0F);
                            heuristic += getHeuristicValue(cloneRound, height + 1) * prob;
                        }
                    }
                    remainingProbability -= usedProb;
                }
                if (!ComparisonHelper.equal(remainingProbability, 0)) {
                    heuristic += getHeuristicValue(addedRound, height + 1) * remainingProbability;
                }
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
                round.getOpponentTiles().entrySet().stream().filter(entry -> entry.getValue() > 0.0).forEach(entry -> {
                    addPossibleMovesForTile(entry.getKey(), left, right, top, bottom, moves);
                });
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
