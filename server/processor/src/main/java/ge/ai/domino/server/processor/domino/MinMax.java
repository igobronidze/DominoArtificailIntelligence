package ge.ai.domino.server.processor.domino;

import ge.ai.domino.domain.ai.PossibleTurn;
import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.processor.sysparam.SystemParameterProcessor;
import ge.ai.domino.server.processor.util.CloneUtil;
import ge.ai.domino.util.tile.TileUtil;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class MinMax {

    private static Logger logger = Logger.getLogger(DominoProcessor.class);

    private static final SystemParameterProcessor systemParameterProcessor = new SystemParameterProcessor();

    private static final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "8");

    private static final DominoProcessor dominoProcessor = new DominoProcessor();

    private static int treeHeight;

    public static AIPrediction minMax(Hand hand) {
        treeHeight = systemParameterProcessor.getIntegerParameterValue(minMaxTreeHeight);
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        PossibleTurn bestTurn = null;
        double bestHeuristic = Integer.MIN_VALUE;
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand = play(hand, possibleTurn);
            updateHeuristicValue(nextHand, 1);
            if (bestTurn == null || nextHand.getAiExtraInfo().getHeuristicValue() > bestHeuristic) {
                bestTurn = possibleTurn;
                bestHeuristic = nextHand.getAiExtraInfo().getHeuristicValue();
            }
        }
        if (bestTurn == null) {
            logger.info("No AIPrediction");
        } else {
            logger.info("AIPrediction is [" + bestTurn.getX() + "-" + bestTurn.getY() + " " + bestTurn.getDirection().name() + "], " +
                    "heuristic - " + bestHeuristic);
        }
        if (bestTurn == null) {
            return null;
        }
        AIPrediction aiPrediction = new AIPrediction();
        aiPrediction.setX(bestTurn.getX());
        aiPrediction.setY(bestTurn.getY());
        aiPrediction.setDirection(bestTurn.getDirection());
        return aiPrediction;
    }

    private static void updateHeuristicValue(Hand hand, int height) {
        if (height == treeHeight) {
            hand.getAiExtraInfo().setHeuristicValue(countScore(hand));
            return;
        }
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        Queue<Hand> possibleHands = new PriorityQueue<>(new Comparator<Hand>() {
            @Override
            public int compare(Hand o1, Hand o2) {
                if (hand.getTableInfo().isMyTurn()) {
                    return Double.compare(o2.getAiExtraInfo().getHeuristicValue(), o1.getAiExtraInfo().getHeuristicValue());
                } else {
                    return Double.compare(o1.getAiExtraInfo().getHeuristicValue(), o2.getAiExtraInfo().getHeuristicValue());
                }
            }
        });
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand = play(hand, possibleTurn);
            updateHeuristicValue(nextHand, height + 1);
            possibleHands.add(nextHand);
        }
        double heuristic = 0.0;
        double remainingProbability = 1.0;
        for (Hand nextHand : possibleHands) {
            Tile lastPlayedTile = hand.getTiles().get(nextHand.getTableInfo().getLastPlayedUID());
            if (hand.getTableInfo().isMyTurn()) {
                double prob = remainingProbability * lastPlayedTile.getMe();
                heuristic -= nextHand.getAiExtraInfo().getHeuristicValue() * prob;
                remainingProbability -= prob;
            } else {
                double prob = remainingProbability * lastPlayedTile.getHim();
                heuristic += nextHand.getAiExtraInfo().getHeuristicValue() * prob;
                remainingProbability -= prob;
            }
        }
        if (possibleTurns.isEmpty()) {
            hand.getAiExtraInfo().setHeuristicValue(countScore(hand) + (hand.getTableInfo().isMyTurn() ? -20 : 20));
        } else {
            hand.getAiExtraInfo().setHeuristicValue(countScore(hand) + heuristic);
        }
    }

    private static Hand play(Hand hand, PossibleTurn possibleTurn) {
        Hand nextHand = CloneUtil.getClone(hand);
        dominoProcessor.makeTileAsPlayed(nextHand.getTiles().get(TileUtil.getTileUID(possibleTurn.getX(), possibleTurn.getY())));
        dominoProcessor.playTile(nextHand.getTableInfo(), possibleTurn.getX(), possibleTurn.getY(), possibleTurn.getDirection());
        if (nextHand.getTableInfo().isMyTurn()) {
            dominoProcessor.updateTileCountBeforePlayMe(nextHand);
        } else {
            dominoProcessor.updateTileCountBeforePlayHim(nextHand);
        }
        nextHand.getTableInfo().setMyTurn(!nextHand.getTableInfo().isMyTurn());
        return nextHand;
    }

    private static Set<PossibleTurn> getPossibleTurns(Hand hand) {
        boolean me = hand.getTableInfo().isMyTurn();
        Set<PossibleTurn> possibleTurns = new HashSet<>();
        TableInfo tableInfo = hand.getTableInfo();
        PlayedTile left = tableInfo.getLeft();
        PlayedTile right = tableInfo.getRight();
        PlayedTile top = tableInfo.getTop();
        PlayedTile bottom = tableInfo.getBottom();
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
        return possibleTurns;
    }

    private static int hashForPlayedTile(PlayedTile playedTile) {
        int p = 10;
        return (playedTile.getOpenSide() + 1) * (playedTile.isDouble() ? p : 1);
    }

    private static double countScore(Hand hand) {
        int count = 0;
        TableInfo tableInfo = hand.getTableInfo();
        if (tableInfo.getLeft().getOpenSide() == tableInfo.getRight().getOpenSide() && tableInfo.getLeft().isDouble() && tableInfo.getRight().isDouble()) {
            count = tableInfo.getLeft().getOpenSide() * 2;
        } else {
            count += tableInfo.getLeft().isDouble() ? (tableInfo.getLeft().getOpenSide() * 2) : tableInfo.getLeft().getOpenSide();
            count += tableInfo.getRight().isDouble() ? (tableInfo.getRight().getOpenSide() * 2) : tableInfo.getRight().getOpenSide();
            if (tableInfo.getTop().isCountInSum()) {
                count += tableInfo.getTop().isDouble() ? (tableInfo.getTop().getOpenSide() * 2) : tableInfo.getTop().getOpenSide();
            }
            if (tableInfo.getBottom().isCountInSum()) {
                count += tableInfo.getBottom().isDouble() ? (tableInfo.getBottom().getOpenSide() * 2) : tableInfo.getBottom().getOpenSide();
            }
        }
        if (count > 0 && count % 5 == 0) {
            if (tableInfo.isMyTurn()) {
                return -1 * count;
            } else {
                return count;
            }
        } else {
            return 0;
        }
    }
}
