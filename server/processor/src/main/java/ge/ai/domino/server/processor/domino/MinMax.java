package ge.ai.domino.server.processor.domino;

import ge.ai.domino.domain.ai.PossibleTurn;
import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.processor.ai.heuristic.HandHeuristic;
import ge.ai.domino.server.processor.ai.heuristic.SimpleHandHeuristic;
import ge.ai.domino.server.processor.sysparam.SystemParameterProcessor;
import ge.ai.domino.server.processor.util.CloneUtil;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class MinMax {

    private static final SystemParameterProcessor systemParameterProcessor = new SystemParameterProcessor();

    private static final int sumAndLastTilesConst = 1009;

    private static final SysParam minMaxTreeHeight = new SysParam("minMaxTreeHeight", "4");

    private static final HandHeuristic handheuristic = new SimpleHandHeuristic();

    private static final DominoProcessor dominoProcessor = new DominoProcessor();

    private static int treeHeight;

    public static AIPrediction minMax(Hand hand) {
        treeHeight = systemParameterProcessor.getIntegerParameterValue(minMaxTreeHeight);
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        PossibleTurn bestTurn = null;
        double bestHeuristic = Double.MIN_VALUE;
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand = play(hand, possibleTurn, true);
            updateHeuristicValue(nextHand, treeHeight, true);
            if (bestTurn == null || nextHand.getAiExtraInfo().getHeuristicValue() > bestHeuristic) {
                bestTurn = possibleTurn;
                bestHeuristic = nextHand.getAiExtraInfo().getHeuristicValue();
            }
        }
        if (bestTurn == null) {
            return null;
        }
        AIPrediction aiPrediction = new AIPrediction();
        aiPrediction.setX(bestTurn.getTile().getX());
        aiPrediction.setY(bestTurn.getTile().getY());
        aiPrediction.setDirection(bestTurn.getDirection());
        return aiPrediction;
    }

    private static void updateHeuristicValue(Hand hand, int height, boolean me) {
        if (height == treeHeight) {
            hand.getAiExtraInfo().setHeuristicValue(handheuristic.getHeuristicValue(hand));
            return;
        }
        Set<PossibleTurn> possibleTurns = getPossibleTurns(hand);
        Set<Hand> possibleHands = new TreeSet<>(new Comparator<Hand>() {
            @Override
            public int compare(Hand o1, Hand o2) {
                return Double.compare(o2.getAiExtraInfo().getHeuristicValue(), o1.getAiExtraInfo().getHeuristicValue());
            }
        });
        for (PossibleTurn possibleTurn : possibleTurns) {
            Hand nextHand = play(hand, possibleTurn, me);
            updateHeuristicValue(nextHand, height + 1, !me);
            possibleHands.add(nextHand);
        }
        double heuristic = 0.0;
        double remainingProbability = 1.0;
        for (Hand nextHand : possibleHands) {
            Tile lastPlayedTile = hand.getTiles().get(nextHand.getTableInfo().getLastPlayedUID());
            if (me) {
                double prob = remainingProbability * lastPlayedTile.getMe();
                heuristic += nextHand.getAiExtraInfo().getHeuristicValue() * prob;
                remainingProbability -= prob;
            } else {
                double prob = remainingProbability * lastPlayedTile.getHim();
                heuristic += nextHand.getAiExtraInfo().getHeuristicValue() * prob;
                remainingProbability -= prob;
            }
        }
        if (possibleTurns.isEmpty()) {
            hand.getAiExtraInfo().setHeuristicValue(me ? -20 : 20);
        } else {
            hand.getAiExtraInfo().setHeuristicValue(heuristic);
        }
    }

    private static Hand play(Hand hand, PossibleTurn possibleTurn, boolean me) {
        Hand nextHand = CloneUtil.getClone(hand);
        dominoProcessor.makeTileAsPlayed(possibleTurn.getTile());
        dominoProcessor.playTile(nextHand.getTableInfo(), possibleTurn.getTile().getX(), possibleTurn.getTile().getY(), possibleTurn.getDirection());
        if (me) {
            dominoProcessor.updateTileCountBeforePlayMe(nextHand);
        } else {
            dominoProcessor.updateTileCountBeforePlayHim(nextHand);
        }
        return nextHand;
    }

    private static Set<PossibleTurn> getPossibleTurns(Hand hand) {
        boolean me = !hand.getTableInfo().isMyTurn();
        Set<PossibleTurn> possibleTurns = new TreeSet<>(new Comparator<PossibleTurn>() {
            @Override
            public int compare(PossibleTurn o1, PossibleTurn o2) {
                if (me) {
                    return Double.compare(o2.getTile().getMe(), o1.getTile().getMe());
                } else {
                    return Double.compare(o1.getTile().getHim(), o2.getTile().getHim());
                }
            }
        });
        TableInfo tableInfo = hand.getTableInfo();
        for (Tile tile : hand.getTiles().values()) {
            if (!tile.isPlayed()) {
                Set<Integer> played = new HashSet<>();
                if ((me && tile.getMe() > 0.0) || (!me && tile.getHim() > 0)) {
                    // LEFT RIGHT TOP BOTTOM მიმდევრობა მნიშვნელოვანია
                    PlayedTile left = tableInfo.getLeft();
                    if (left.isDouble() || !played.contains(left.getOpenSide())) {
                        if (left.getOpenSide() == tile.getX() || left.getOpenSide() == tile.getY()) {
                            possibleTurns.add(new PossibleTurn(tile, PlayDirection.LEFT));
                            played.add(left.getOpenSide());
                        }
                    }
                    PlayedTile right = tableInfo.getRight();
                    if (right.isDouble() || !played.contains(right.getOpenSide())) {
                        if (right.getOpenSide() == tile.getX() || right.getOpenSide() == tile.getY()) {
                            possibleTurns.add(new PossibleTurn(tile, PlayDirection.RIGHT));
                            played.add(right.getOpenSide());
                        }
                    }
                    PlayedTile top = tableInfo.getTop();
                    if (top != null && (top.isDouble() || !played.contains(top.getOpenSide()))) {
                        if (top.getOpenSide() == tile.getX() || top.getOpenSide() == tile.getY()) {
                            possibleTurns.add(new PossibleTurn(tile, PlayDirection.TOP));
                            played.add(top.getOpenSide());
                        }
                    }
                    PlayedTile bottom = tableInfo.getBottom();
                    if (bottom != null && (bottom.isDouble() || !played.contains(bottom.getOpenSide()))) {
                        if (bottom.getOpenSide() == tile.getX() || bottom.getOpenSide() == tile.getY()) {
                            possibleTurns.add(new PossibleTurn(tile, PlayDirection.BOTTOM));
                            played.add(bottom.getOpenSide());
                        }
                    }
                }
            }
        }
        return possibleTurns;
    }
}
