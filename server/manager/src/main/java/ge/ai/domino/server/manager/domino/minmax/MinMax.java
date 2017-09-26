package ge.ai.domino.server.manager.domino.minmax;

import ge.ai.domino.domain.ai.PossibleTurn;
import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.domino.helper.DominoHelper;
import ge.ai.domino.server.manager.domino.DominoManager;
import ge.ai.domino.server.manager.domino.heuristic.HandHeuristic;
import ge.ai.domino.server.manager.domino.heuristic.SimpleHandHeuristic;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.server.manager.util.CloneUtil;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class MinMax {

    private static Logger logger = Logger.getLogger(DominoManager.class);

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private static final HandHeuristic handHeuristic = new SimpleHandHeuristic();

    private static final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "6");

    private static final DominoManager dominoManager = new DominoManager();

    private static int treeHeight;

    public static AIPrediction minMax(Hand hand) throws DAIException {
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        PossibleTurn bestTurn = null;
        double bestHeuristic = Integer.MIN_VALUE;
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand = dominoManager.playForMe(CloneUtil.getClone(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
            double heuristic = getHeuristicValue(nextHand, 1);
            if (bestTurn == null || heuristic > bestHeuristic) {
                bestTurn = possibleTurn;
                bestHeuristic = heuristic;
            }
        }
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

    private static double getHeuristicValue(Hand hand, int height) throws DAIException {
        TableInfo tableInfo = hand.getTableInfo();
        // თუ მოწინააღმდეგემ გაიარა, ვიმატებთ მის სავარაუდო დარჩენილ ქვებს და ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (tableInfo.isNeedToAddLeftTiles()) {
            hand.getGameInfo().setMyPoints(hand.getGameInfo().getMyPoints() + DominoHelper.countLeftTiles(hand, false, true));
            hand.getAiExtraInfo().setHeuristicValue(handHeuristic.getHeuristic(hand));
            return hand.getAiExtraInfo().getHeuristicValue();
        }
        // თუ ახალი ხელი დაიწყო ვაბრუნებთ სუფთა ევრისტიკულ მნიშვნელობას
        if (tableInfo.getMyTilesCount() == 0 && tableInfo.getHimTilesCount() == 7 && tableInfo.getBazaarTilesCount() == 21) {
            hand.getAiExtraInfo().setHeuristicValue(handHeuristic.getHeuristic(hand));
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
                Hand nextHand = dominoManager.playForMe(CloneUtil.getClone(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
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
                    if (!tile.isPlayed() && !tile.isMine() && bazaarProbSum != 0.0) {
                        double probForPickTile = (1 - tile.getHim()) / bazaarProbSum; // ალბათობა, რომ კონკრეტულად ეს tile შემხვდება
                        heuristic += getHeuristicValue(dominoManager.addTileForMe(CloneUtil.getClone(hand), tile.getX(), tile.getY(), true), height + 1) * probForPickTile;
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
                Hand nextHand = dominoManager.playForHim(CloneUtil.getClone(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
                getHeuristicValue(nextHand, height + 1);
                possibleHands.add(nextHand);
            }

            double heuristic = 0.0;
            double remainingProbability = 1.0;
            // მივყვებით მოწინააღმდეგისთვის საუკეთესო სვლებს
            for (Hand nextHand : possibleHands) {
                Tile lastPlayedTile = hand.getTiles().get(nextHand.getTableInfo().getLastPlayedUID()); // ბოლოს ნათამაშები ქვა
                double prob = remainingProbability * lastPlayedTile.getHim();  // ალბათობა ბოლოს ნათამაშებიქ ვის ქონის, იმის გათვალისწინებით, რომ უკვე სხვა აქამდე არჩეული ქვები არ ქონია
                heuristic += nextHand.getAiExtraInfo().getHeuristicValue() * prob;
                remainingProbability -= prob;   // remainingProbability ინახავს ალბათობას, რომ აქამდე გგავლილი ქვები არ ქონდა
            }
            // ბაზარში წასვლი შემთხვევა
            if (remainingProbability > 0.0001) {
                heuristic += getHeuristicValue(dominoManager.addTileForHim(CloneUtil.getClone(hand), true), height + 1) * remainingProbability;
            }
            hand.getAiExtraInfo().setHeuristicValue(heuristic);
            return heuristic;
        }
    }

    private static Set<PossibleTurn> getPossibleTurns(Hand hand) {
        boolean me = hand.getTableInfo().isMyTurn();
        Set<PossibleTurn> possibleTurns = new HashSet<>();
        TableInfo tableInfo = hand.getTableInfo();
        PlayedTile left = tableInfo.getLeft();
        PlayedTile right = tableInfo.getRight();
        PlayedTile top = tableInfo.getTop();
        PlayedTile bottom = tableInfo.getBottom();
        if (me && tableInfo.getLeft() == null) {
            for (Tile tile : hand.getTiles().values()) {
                if (tile.isMine()) {
                    possibleTurns.add(new PossibleTurn(tile.getX(), tile.getY(), PlayDirection.LEFT));
                }
            }
        } else {
            for (Tile tile : hand.getTiles().values()) {
                if (!tile.isPlayed()) {
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
        }
        return possibleTurns;
    }

    private static int hashForPlayedTile(PlayedTile playedTile) {
        int p = 10;
        return (playedTile.getOpenSide() + 1) * (playedTile.isDouble() ? p : 1);
    }
}
