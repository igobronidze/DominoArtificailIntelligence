package ge.ai.domino.server.manager.domino.minmax;

import ge.ai.domino.domain.ai.PossibleTurn;
import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.server.manager.domino.DominoManager;
import ge.ai.domino.server.manager.domino.heuristic.HandHeuristic;
import ge.ai.domino.server.manager.domino.heuristic.SimpleHandHeuristic;
import ge.ai.domino.server.manager.domino.processor.HimTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.MyTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.TurnProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.server.manager.util.CloneUtil;
import ge.ai.domino.util.tile.TileUtil;
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

    private static final TurnProcessor myTurnProcessor = new MyTurnProcessor();

    private static final TurnProcessor himTurnProcessor = new HimTurnProcessor();

    private static int treeHeight;

    public static AIPrediction minMax(Hand hand) {
        treeHeight = systemParameterManager.getIntegerParameterValue(minMaxTreeHeight);
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        PossibleTurn bestTurn = null;
        double bestHeuristic = Integer.MIN_VALUE;
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand = himTurnProcessor.play(CloneUtil.getClone(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
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
        AIPrediction aiPrediction = new AIPrediction();
        aiPrediction.setX(bestTurn.getX());
        aiPrediction.setY(bestTurn.getY());
        aiPrediction.setDirection(bestTurn.getDirection());
        logger.info("AIPrediction is [" + bestTurn.getX() + "-" + bestTurn.getY() + " " + bestTurn.getDirection().name() + "], " + "heuristic - " + bestHeuristic);
        return aiPrediction;
    }

    private static double getHeuristicValue(Hand hand, int height) {
        TableInfo tableInfo = hand.getTableInfo();
        if (tableInfo.isNeedToAddLeftTiles()) {
            hand.getGameInfo().setMyPoints(hand.getGameInfo().getMyPoints() + DominoHelper.countLeftTiles(hand, false));
            return handHeuristic.getHeuristic(hand);
        }
        if (tableInfo.getMyTilesCount() == 0 && tableInfo.getHimTilesCount() == 7 && tableInfo.getBazaarTilesCount() == 21) {
            return handHeuristic.getHeuristic(hand);
        }
        if (height == treeHeight) {
            return handHeuristic.getHeuristic(hand);
        }
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        Queue<Hand> possibleHands = new PriorityQueue<>(new Comparator<Hand>() {
            @Override
            public int compare(Hand o1, Hand o2) {
                if (hand.getTableInfo().isMyTurn()) {
                    return Double.compare(handHeuristic.getHeuristic(o2), handHeuristic.getHeuristic(o1));
                } else {
                    return Double.compare(handHeuristic.getHeuristic(o1), handHeuristic.getHeuristic(o2));
                }
            }
        });
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand;
            if (tableInfo.isMyTurn()) {
                nextHand = myTurnProcessor.play(CloneUtil.getClone(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
            } else {
                nextHand = himTurnProcessor.play(CloneUtil.getClone(hand), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection(), true);
            }
            possibleHands.add(nextHand);
        }
        double heuristic = 0.0;
        double remainingProbability = 1.0;
        for (Hand nextHand : possibleHands) {
            Tile lastPlayedTile = hand.getTiles().get(nextHand.getTableInfo().getLastPlayedUID());
            double prob;
            if (hand.getTableInfo().isMyTurn()) {
                prob = remainingProbability * lastPlayedTile.getMe();
            } else {
                prob = remainingProbability * lastPlayedTile.getHim();
            }
            heuristic += getHeuristicValue(nextHand, height + 1) * prob;
            remainingProbability -= prob;
        }
        if (remainingProbability > 0.0) {
            if (hand.getTableInfo().isMyTurn()) {
                heuristic += -20 * remainingProbability;   // TODO[IG] აქ მაგარი მიიქარება
            } else {
                heuristic += handHeuristic.getHeuristic(himTurnProcessor.addTile(CloneUtil.getClone(hand), 0, 0, true)) * remainingProbability;
            }
        }
        return heuristic;
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
                if (tile.getMe() > 0.0) {
                    possibleTurns.add(new PossibleTurn(tile.getX(), tile.getY(), PlayDirection.LEFT));
                }
            }
        } else {
            for (Tile tile : hand.getTiles().values()) {
                if (!tile.isPlayed()) {
                    Set<Integer> played = new HashSet<>();
                    if ((me && tile.getMe() > 0.0) || (!me && tile.getHim() > 0)) {
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
