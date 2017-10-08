package ge.ai.domino.server.manager.domino.heuristic;

import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

public class ComplexHandHeuristic implements HandHeuristic {

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam coefficientForComplexHeuristic = new SysParam("coefficientForComplexHeuristic", "12");

    @Override
    public double getHeuristic(Hand hand) {
        double heuristic = hand.getGameInfo().getMyPoints() - hand.getGameInfo().getHimPoints();
        heuristic += sysParamManager.getIntegerParameterValue(coefficientForComplexHeuristic) * (getPossibleTurnCount(hand, true) / hand.getTableInfo().getMyTilesCount() / hand.getTableInfo().getMyTilesCount() -
                getPossibleTurnCount(hand, false) / hand.getTableInfo().getHimTilesCount() / hand.getTableInfo().getHimTilesCount());
        return heuristic;
    }

    private double getPossibleTurnCount(Hand hand, boolean me) {
        double count = 0.0;
        TableInfo tableInfo = hand.getTableInfo();
        for (Tile tile : hand.getTiles().values()) {
            if (me) {
                if (tile.isMine()) {
                    count += countPlay(tile, tableInfo);
                }
            } else {
                if (tile.getHim() > 0.0) {
                    count += tile.getHim() * countPlay(tile, tableInfo);
                }
            }
        }
        return count;
    }

    private int countPlay(Tile tile, TableInfo tableInfo) {
        int count = 0;
        int x = tile.getX();
        int y = tile.getY();
        if (tableInfo.getLeft() != null) {
            if (tableInfo.getLeft().getOpenSide() == x || tableInfo.getLeft().getOpenSide() == y) {
                count++;
            }
        }
        if (tableInfo.getRight() != null) {
            if (tableInfo.getRight().getOpenSide() == x || tableInfo.getRight().getOpenSide() == y) {
                count++;
            }
        }
        if (tableInfo.getTop() != null) {
            if (tableInfo.getTop().getOpenSide() == x || tableInfo.getTop().getOpenSide() == y) {
                count++;
            }
        }
        if (tableInfo.getBottom() != null) {
            if (tableInfo.getBottom().getOpenSide() == x || tableInfo.getBottom().getOpenSide() == y) {
                count++;
            }
        }
        return count;
    }
}