package ge.ai.domino.server.manager.game.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.PlayedTile;
import ge.ai.domino.domain.tile.Tile;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.GameHelper;
import ge.ai.domino.server.manager.game.GameManager;
import ge.ai.domino.server.manager.game.heuristic.ComplexRoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristic;
import ge.ai.domino.server.manager.game.heuristic.RoundHeuristicHelper;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.server.manager.util.CloneUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class MinMax {

    private static Logger logger = Logger.getLogger(GameManager.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final RoundHeuristic roundHeuristic = new ComplexRoundHeuristic();

    private final GameManager gameManager = new GameManager();

    private final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "7");

    private final SysParam epsilonForProbabilities = new SysParam("epsilonForProbabilities", "0.000001");

    private int treeHeight;

    private int recursionCount;

    public Move minMax(Round round) throws DAIException {
        long ms = System.currentTimeMillis();
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);
        List<Move> moves = getPossibleMoves(round);
        Move bestMove = null;
        double bestHeuristic = Integer.MIN_VALUE;
        for (Move move : moves) {
            Round nextRound = gameManager.playForMe(CloneUtil.getCloneForMinMax(round), move, true);
            double heuristic = getHeuristicValue(nextRound, 1);
            if (bestMove == null || heuristic > bestHeuristic) {
                bestMove = move;
                bestHeuristic = heuristic;
            }
            logger.info("PlayedMove- " + move.getLeft() + ":" + move.getRight() + " " + move.getDirection() + ", heuristic: " + heuristic);
        }
        double tookMs = System.currentTimeMillis() - ms;
        logger.info("MinMax took " + tookMs + "ms, recursion count " + recursionCount + ", average " + (tookMs / recursionCount));
        recursionCount = 0;
        if (bestMove == null) {
            logger.info("No AIPrediction");
            return null;
        }
        round.getHeuristicInfo().setValue(bestHeuristic);
        Move aiPrediction = new Move(bestMove.getLeft(), bestMove.getRight(), bestMove.getDirection());
        logger.info("AIPrediction is [" + bestMove.getLeft() + "-" + bestMove.getRight() + " " + bestMove.getDirection().name() + "], " + "heuristic: " + bestHeuristic);
        return aiPrediction;
    }

    private double getHeuristicValue(Round round, int height) throws DAIException {
        recursionCount++;
        TableInfo tableInfo = round.getTableInfo();
        GameInfo gameInfo = round.getGameInfo();
        // თუ გაიჭედა თამაში
        if (tableInfo.isOmittedMe() && tableInfo.isOmittedOpponent()) {
            int opponentTilesCount = GameHelper.countLeftTiles(round, false, true);
            int myTilesCount = GameHelper.countLeftTiles(round, true, false);
            if (myTilesCount < opponentTilesCount) {
                GameHelper.addLeftTiles(gameInfo, opponentTilesCount, true, gameInfo.getGameId(), true);
            } else if (myTilesCount > opponentTilesCount) {
                GameHelper.addLeftTiles(gameInfo, myTilesCount, false, gameInfo.getGameId(), true);
            }
            round.getHeuristicInfo().setValue(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, tableInfo.isMyMove()));
            return round.getHeuristicInfo().getValue();
        }
        // თუ მთლიანად დამთავრდა თამაში
        if (gameInfo.isFinished()) {
            return RoundHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedGames.getGame(gameInfo.getGameId()).getProperties().getPointsForWin());
        }
        // თუ მოწინააღმდეგემ გაიარა, ვიმატებთ მის სავარაუდო დარჩენილ ქვებს და ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (tableInfo.isNeedToAddLeftTiles()) {
            int opponentTilesCount = GameHelper.countLeftTiles(round, false, true);
            GameHelper.addLeftTiles(gameInfo, opponentTilesCount, true, gameInfo.getGameId(), true);
            round.getHeuristicInfo().setValue(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, !tableInfo.isMyMove()));
            return round.getHeuristicInfo().getValue();
        }
        // თუ ახალი ხელი დაიწყო ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (GameHelper.isNewRound(round)) {
            round.getHeuristicInfo().setValue(RoundHeuristicHelper.getFinishedRoundHeuristic(gameInfo, !tableInfo.isMyMove()));
            return round.getHeuristicInfo().getValue();
        }
        // თუ ჩავედით ხის ფოთოლში, ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (height == treeHeight) {
            round.getHeuristicInfo().setValue(roundHeuristic.getHeuristic(round));
            return round.getHeuristicInfo().getValue();
        }
        List<Move> moves = getPossibleMoves(round);
        if (round.getTableInfo().isMyMove()) {
            // ჩემთვის საუკეთესო სვლის დადგენა
            Round bestRound = null;
            for (Move move : moves) {
                Round nextRound = gameManager.playForMe(CloneUtil.getCloneForMinMax(round), move, true);
                getHeuristicValue(nextRound, height + 1);
                if (bestRound == null || nextRound.getHeuristicInfo().getValue() > bestRound.getHeuristicInfo().getValue()) {
                    bestRound = nextRound;
                }
            }
            // თუ სვლა არ მქონდა გასაკეთებელი ვიხილავთ ბაზარში არსებული ქვების აღების ვარიანტებს
            if (bestRound == null) {
                double heuristic = 0.0;
                double bazaarProbSum = round.getTableInfo().getBazaarTilesCount();
                for (OpponentTile tile : round.getOpponentTiles().values()) {
                    if (tile.getProb() != 1.0) {
                        double probForPickTile = (1 - tile.getProb()) / bazaarProbSum; // ალბათობა, რომ კონკრეტულად ეს tile შემხვდება
                        heuristic += getHeuristicValue(gameManager.addTileForMe(CloneUtil.getCloneForMinMax(round), tile.getLeft(), tile.getRight(), true), height + 1) * probForPickTile;
                    }
                }
                round.getHeuristicInfo().setValue(heuristic);
                return heuristic;
            } else {
                round.getHeuristicInfo().setValue(bestRound.getHeuristicInfo().getValue());
                return round.getHeuristicInfo().getValue();
            }
        } else {
            // შესაძლო გაგრძელებები დალაგებული ზრდადობით
            Queue<Round> possibleRounds = new PriorityQueue<>(new Comparator<Round>() {
                @Override
                public int compare(Round o1, Round o2) {
                    return Double.compare(o1.getHeuristicInfo().getValue(), o2.getHeuristicInfo().getValue());
                }
            });
            // ყველა შესძლო სვლის გათამაშება და რიგში ჩამატება
            for (Move move : moves) {
                Round nextRound = gameManager.playForOpponent(CloneUtil.getCloneForMinMax(round), move, true);
                getHeuristicValue(nextRound, height + 1);
                possibleRounds.add(nextRound);
            }

            int notPlayedTilesCount = getBazaarTileCountForSure(round.getOpponentTiles().values());   // ქვების რაოდენობა რომელიც არ/ვერ ითამაშა მოწინააღმდეგემ
            double heuristic = 0.0;
            double remainingProbability = 1.0;
            // მივყვებით მოწინააღმდეგისთვის საუკეთესო სვლებს
            for (Round nextRound : possibleRounds) {
                double prob;
                if (notPlayedTilesCount == tableInfo.getBazaarTilesCount()) {
                    prob = remainingProbability;   // ბოლო შანსია ჩამოსვლის შესაბამისად უეჭველი ჩამოდის
                } else {
                    prob = remainingProbability * nextRound.getTableInfo().getLastPlayedProb();  // ალბათობა ბოლოს ნათამაშები ქვის ქონის, იმის გათვალისწინებით, რომ უკვე სხვა აქამდე არჩეული ქვები არ ქონია
                }
                heuristic += nextRound.getHeuristicInfo().getValue() * prob;
                remainingProbability -= prob;   // remainingProbability ინახავს ალბათობას, რომ აქამდე გგავლილი ქვები არ ქონდა

                // იმაზე მეტჯერ, ვერ "არ ჩამოვა" ქვას ვიდრე ბაზარშია
                notPlayedTilesCount++;
                if (notPlayedTilesCount > tableInfo.getBazaarTilesCount()) {
                    break;

                }
            }
            // ბაზარში წასვლი შემთხვევა
            double epsilon = systemParameterManager.getFloatParameterValue(epsilonForProbabilities);
            if (remainingProbability > epsilon) {
                Round addedRound = gameManager.addTileForOpponent(CloneUtil.getCloneForMinMax(round), true);
                if (tableInfo.getBazaarTilesCount() > 2) {
                    double bazaarSum = tableInfo.getBazaarTilesCount();
                    double usedProb = 0.0;
                    PlayedTile left = tableInfo.getLeft();
                    PlayedTile right = tableInfo.getRight();
                    PlayedTile top = tableInfo.getTop();
                    PlayedTile bottom = tableInfo.getBottom();
                    for (OpponentTile tile : addedRound.getOpponentTiles().values()) {
                        if ((left != null && (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight())) ||
                                (right != null && (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight())) ||
                                (top != null && (top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight())) ||
                                (bottom != null && (bottom.getOpenSide() == tile.getLeft() || bottom.getOpenSide() == tile.getRight()))) {
                            Round cloneRound = CloneUtil.getClone(addedRound);
                            double prob = remainingProbability * 1 / bazaarSum;
                            usedProb += prob;
                            cloneRound.getOpponentTiles().get(tile.hashCode()).setProb(1.0);
                            heuristic += getHeuristicValue(cloneRound, height + 1) * prob;
                        }
                    }
                    remainingProbability -= usedProb;
                }
                if (remainingProbability > epsilon) {
                    heuristic += getHeuristicValue(addedRound, height + 1) * remainingProbability;
                }
            }
            round.getHeuristicInfo().setValue(heuristic);
            return heuristic;
        }
    }

    private int getBazaarTileCountForSure(Collection<OpponentTile> tiles) {
        int count = 0;
        for (OpponentTile tile : tiles) {
            if (tile.getProb() == 0) {
                count++;
            }
        }
        return count;
    }

    private List<Move> getPossibleMoves(Round round) {
        List<Move> moves = new ArrayList<>();
        TableInfo tableInfo = round.getTableInfo();
        PlayedTile left = tableInfo.getLeft();
        PlayedTile right = tableInfo.getRight();
        PlayedTile top = tableInfo.getTop();
        PlayedTile bottom = tableInfo.getBottom();
        // პირველი ქვის ჩამოსვლა
        if (tableInfo.getLeft() == null) {
            for (Tile tile : round.getMyTiles()) {
                moves.add(new Move(tile.getLeft(), tile.getRight(), MoveDirection.LEFT));
            }
        } else {
            if (round.getTableInfo().isMyMove()) {
                for (Tile tile : round.getMyTiles()) {
                    addPossibleMovesForTile(tile, left, right, top, bottom, moves);
                }
            } else {
                for (OpponentTile tile : round.getOpponentTiles().values()) {
                    if (tile.getProb() > 0.0) {
                        addPossibleMovesForTile(tile, left, right, top, bottom, moves);
                    }
                }
            }
        }
        return moves;
    }

    private void addPossibleMovesForTile(Tile tile, PlayedTile left, PlayedTile right, PlayedTile top, PlayedTile bottom, List<Move> moves) {
        Set<Integer> played = new HashSet<>();
        // LEFT RIGHT TOP BOTTOM მიმდევრობა მნიშვნელოვანია
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
}
