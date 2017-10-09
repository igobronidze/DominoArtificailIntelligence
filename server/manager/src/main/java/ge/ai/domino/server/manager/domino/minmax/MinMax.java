package ge.ai.domino.server.manager.domino.minmax;

import ge.ai.domino.domain.ai.PossibleTurn;
import ge.ai.domino.domain.ai.AIPrediction;
import ge.ai.domino.domain.domino.game.GameInfo;
import ge.ai.domino.domain.domino.game.Hand;
import ge.ai.domino.domain.domino.game.PlayDirection;
import ge.ai.domino.domain.domino.game.PlayedTile;
import ge.ai.domino.domain.domino.game.TableInfo;
import ge.ai.domino.domain.domino.game.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.domino.DominoManager;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.server.manager.domino.heuristic.ComplexHandHeuristic;
import ge.ai.domino.server.manager.domino.heuristic.HandHeuristic;
import ge.ai.domino.server.manager.domino.heuristic.HandHeuristicHelper;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.server.manager.util.CloneUtil;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class MinMax {

    private static Logger logger = Logger.getLogger(DominoManager.class);

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final HandHeuristic handHeuristic = new ComplexHandHeuristic();

    private final DominoManager dominoManager = new DominoManager();

    private final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "7");

    private final SysParam epsilonForProbabilities = new SysParam("epsilonForProbabilities", "0.000001");

    private int treeHeight;

    private int recursionCount;

    public AIPrediction minMax(Hand hand) throws DAIException {
        long ms = System.currentTimeMillis();
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        PossibleTurn bestTurn = null;
        double bestHeuristic = Integer.MIN_VALUE;
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand = dominoManager.playForMe(CloneUtil.getCloneForMinMax(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
            double heuristic = getHeuristicValue(nextHand, 1);
            if (bestTurn == null || heuristic > bestHeuristic) {
                bestTurn = possibleTurn;
                bestHeuristic = heuristic;
            }
            logger.info("Turn- " + possibleTurn.getX() + ":" + possibleTurn.getY() + " " + possibleTurn.getDirection() + ", heuristic: " + heuristic);
        }
        double tookMs = System.currentTimeMillis() - ms;
        logger.info("MinMax took " + tookMs + "ms, recursion count " + recursionCount + ", average " + (tookMs / recursionCount));
        recursionCount = 0;
        if (bestTurn == null) {
            logger.info("No AIPrediction");
            return null;
        }
        hand.getAiExtraInfo().setHeuristicValue(bestHeuristic);
        AIPrediction aiPrediction = new AIPrediction();
        aiPrediction.setX(bestTurn.getX());
        aiPrediction.setY(bestTurn.getY());
        aiPrediction.setDirection(bestTurn.getDirection());
        logger.info("AIPrediction is [" + bestTurn.getX() + "-" + bestTurn.getY() + " " + bestTurn.getDirection().name() + "], " + "heuristic: " + bestHeuristic);
        return aiPrediction;
    }

    private double getHeuristicValue(Hand hand, int height) throws DAIException {
        recursionCount++;
        TableInfo tableInfo = hand.getTableInfo();
        GameInfo gameInfo = hand.getGameInfo();
        // თუ გაიჭედა თამაში
        if (tableInfo.isOmittedMe() && tableInfo.isOmittedHim()) {
            int himTilesCount = DominoHelper.countLeftTiles(hand, false, true);
            int myTilesCount = DominoHelper.countLeftTiles(hand, true, false);
            if (myTilesCount < himTilesCount) {
                DominoHelper.addLeftTiles(gameInfo, himTilesCount, true, gameInfo.getGameId(), true);
            } else if (myTilesCount > himTilesCount) {
                DominoHelper.addLeftTiles(gameInfo, myTilesCount, false, gameInfo.getGameId(), true);
            }
            hand.getAiExtraInfo().setHeuristicValue(HandHeuristicHelper.getFinishedHandHeuristic(gameInfo, tableInfo.isMyTurn()));
            return hand.getAiExtraInfo().getHeuristicValue();
        }
        // თუ მთლიანად დამთავრდა თამაში
        if (gameInfo.isFinished()) {
            return HandHeuristicHelper.getFinishedGameHeuristic(gameInfo, CachedDominoGames.getGame(gameInfo.getGameId()).getGameProperties().getPointsForWin());
        }
        // თუ მოწინააღმდეგემ გაიარა, ვიმატებთ მის სავარაუდო დარჩენილ ქვებს და ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (tableInfo.isNeedToAddLeftTiles()) {
            int himTilesCount = DominoHelper.countLeftTiles(hand, false, true);
            DominoHelper.addLeftTiles(gameInfo, himTilesCount, true, gameInfo.getGameId(), true);
            hand.getAiExtraInfo().setHeuristicValue(HandHeuristicHelper.getFinishedHandHeuristic(gameInfo, !tableInfo.isMyTurn()));
            return hand.getAiExtraInfo().getHeuristicValue();
        }
        // თუ ახალი ხელი დაიწყო ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (DominoHelper.isNewHand(tableInfo)) {
            hand.getAiExtraInfo().setHeuristicValue(HandHeuristicHelper.getFinishedHandHeuristic(gameInfo, !tableInfo.isMyTurn()));
            return hand.getAiExtraInfo().getHeuristicValue();
        }
        // თუ ჩავედით ხის ფოთოლში, ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (height == treeHeight) {
            hand.getAiExtraInfo().setHeuristicValue(handHeuristic.getHeuristic(hand));
            return hand.getAiExtraInfo().getHeuristicValue();
        }
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        if (hand.getTableInfo().isMyTurn()) {
            // ჩემთვის საუკეთესო სვლის დადგენა
            Hand bestHand = null;
            for (PossibleTurn possibleTurn : possibleTurns) {
                Hand nextHand = dominoManager.playForMe(CloneUtil.getCloneForMinMax(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
                getHeuristicValue(nextHand, height + 1);
                if (bestHand == null || nextHand.getAiExtraInfo().getHeuristicValue() > bestHand.getAiExtraInfo().getHeuristicValue()) {
                    bestHand = nextHand;
                }
            }
            // თუ სვლა არ მქონდა გასაკეთებელი ვიხილავთ ბაზარში არსებული ქვების აღების ვარიანტებს
            if (bestHand == null) {
                double heuristic = 0.0;
                double bazaarProbSum = hand.getTableInfo().getBazaarTilesCount();
                for (Tile tile : hand.getTiles().values()) {
                    if (!tile.isMine() && tile.getHim() != 1.0) {
                        double probForPickTile = (1 - tile.getHim()) / bazaarProbSum; // ალბათობა, რომ კონკრეტულად ეს tile შემხვდება
                        heuristic += getHeuristicValue(dominoManager.addTileForMe(CloneUtil.getCloneForMinMax(hand), tile.getX(), tile.getY(), true), height + 1) * probForPickTile;
                    }
                }
                hand.getAiExtraInfo().setHeuristicValue(heuristic);
                return heuristic;
            } else {
                hand.getAiExtraInfo().setHeuristicValue(bestHand.getAiExtraInfo().getHeuristicValue());
                return hand.getAiExtraInfo().getHeuristicValue();
            }
        } else {
            // შესაძლო გაგრძელებები დალაგებული ზრდადობით
            Queue<Hand> possibleHands = new PriorityQueue<>(new Comparator<Hand>() {
                @Override
                public int compare(Hand o1, Hand o2) {
                    return Double.compare(o1.getAiExtraInfo().getHeuristicValue(), o2.getAiExtraInfo().getHeuristicValue());
                }
            });
            // ყველა შესძლო სვლის გათამაშება და რიგში ჩამატება
            for (PossibleTurn possibleTurn : possibleTurns) {
                Hand nextHand = dominoManager.playForHim(CloneUtil.getCloneForMinMax(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
                getHeuristicValue(nextHand, height + 1);
                possibleHands.add(nextHand);
            }

            int notPlayedTilesCount = getBazaarTileCountForSure(hand.getTiles().values());   // ქვების რაოდენობა რომელიც არ/ვერ ითამაშა მოწინააღმდეგემ
            double heuristic = 0.0;
            double remainingProbability = 1.0;
            // მივყვებით მოწინააღმდეგისთვის საუკეთესო სვლებს
            for (Hand nextHand : possibleHands) {
                Tile lastPlayedTile = hand.getTiles().get(nextHand.getTableInfo().getLastPlayedUID()); // ბოლოს ნათამაშები ქვა
                double prob = remainingProbability * lastPlayedTile.getHim();  // ალბათობა ბოლოს ნათამაშებიქ ვის ქონის, იმის გათვალისწინებით, რომ უკვე სხვა აქამდე არჩეული ქვები არ ქონია
                heuristic += nextHand.getAiExtraInfo().getHeuristicValue() * prob;
                remainingProbability -= prob;   // remainingProbability ინახავს ალბათობას, რომ აქამდე გგავლილი ქვები არ ქონდა

                // იმაზე მეტჯერ, ვერ "არ ჩამოვა" ქვას ვიდრე ბაზარშია
                notPlayedTilesCount++;
                if (notPlayedTilesCount > tableInfo.getBazaarTilesCount()) {
                    break;
                }
            }
            // ბაზარში წასვლი შემთხვევა
            double epsilon = systemParameterManager.getFloatParameterValue(epsilonForProbabilities);
            if (remainingProbability > epsilon && notPlayedTilesCount <= tableInfo.getBazaarTilesCount()) {
                heuristic += getHeuristicValue(dominoManager.addTileForHim(CloneUtil.getCloneForMinMax(hand), true), height + 1) * remainingProbability;
            }
            hand.getAiExtraInfo().setHeuristicValue(heuristic);
            return heuristic;
        }
    }

    private int getBazaarTileCountForSure(Collection<Tile> tiles) {
        int count = 0;
        for (Tile tile : tiles) {
            if (!tile.isMine() && tile.getHim() == 0) {
                count++;
            }
        }
        return count;
    }

    private Set<PossibleTurn> getPossibleTurns(Hand hand) {
        boolean me = hand.getTableInfo().isMyTurn();
        Set<PossibleTurn> possibleTurns = new HashSet<>();
        TableInfo tableInfo = hand.getTableInfo();
        PlayedTile left = tableInfo.getLeft();
        PlayedTile right = tableInfo.getRight();
        PlayedTile top = tableInfo.getTop();
        PlayedTile bottom = tableInfo.getBottom();
        Collection<Tile> tiles = hand.getTiles().values();
        if (me && tableInfo.getLeft() == null) {
            for (Tile tile : tiles) {
                if (tile.isMine()) {
                    possibleTurns.add(new PossibleTurn(tile.getX(), tile.getY(), PlayDirection.LEFT));
                }
            }
        } else {
            for (Tile tile : tiles) {
                Set<Integer> played = new HashSet<>();
                if ((me && tile.isMine()) || (!me && tile.getHim() > 0)) {
                    // LEFT RIGHT TOP BOTTOM მიმდევრობა მნიშვნელოვანია
                    if (!played.contains(hashForPlayedTile(left))) {
                        if (left.getOpenSide() == tile.getX() || left.getOpenSide() == tile.getY()) {
                            possibleTurns.add(new PossibleTurn(tile.getX(), tile.getY(), PlayDirection.LEFT));
                            played.add(hashForPlayedTile(left));
                        }
                    }
                    if (!played.contains(hashForPlayedTile(right))) {
                        if (right.getOpenSide() == tile.getX() || right.getOpenSide() == tile.getY()) {
                            possibleTurns.add(new PossibleTurn(tile.getX(), tile.getY(), PlayDirection.RIGHT));
                            played.add(hashForPlayedTile(right));
                        }
                    }
                    if (top != null && !played.contains(hashForPlayedTile(top))) {
                        if ((top.getOpenSide() == tile.getX() || top.getOpenSide() == tile.getY()) && !left.isCenter() && !right.isCenter()) {
                            possibleTurns.add(new PossibleTurn(tile.getX(), tile.getY(), PlayDirection.TOP));
                            played.add(hashForPlayedTile(top));
                        }
                    }
                    if (bottom != null && !played.contains(hashForPlayedTile(bottom))) {
                        if ((bottom.getOpenSide() == tile.getX() || bottom.getOpenSide() == tile.getY()) && !left.isCenter() && !right.isCenter()) {
                            possibleTurns.add(new PossibleTurn(tile.getX(), tile.getY(), PlayDirection.BOTTOM));
                            played.add(hashForPlayedTile(bottom));
                        }
                    }
                }
            }
        }
        return possibleTurns;
    }

    private int hashForPlayedTile(PlayedTile playedTile) {
        int p = 10;
        return (playedTile.getOpenSide() + 1) * (playedTile.isDouble() ? p : 1);
    }
}
