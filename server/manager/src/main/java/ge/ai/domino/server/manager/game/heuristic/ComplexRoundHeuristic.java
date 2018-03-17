package ge.ai.domino.server.manager.game.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

import java.util.Map;

public class ComplexRoundHeuristic implements RoundHeuristic {

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam coefficientForComplexHeuristic = new SysParam("coefficientForComplexHeuristic", "12");

    @Override
    public float getHeuristic(Round round) {
        float heuristic = round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint();
        int myTilesCount = round.getMyTiles().size();
        heuristic += sysParamManager.getIntegerParameterValue(coefficientForComplexHeuristic) * (countPossibleMoves(round, true) / myTilesCount / myTilesCount -
                countPossibleMoves(round, false) / round.getTableInfo().getOpponentTilesCount() / round.getTableInfo().getOpponentTilesCount());
        return heuristic;
    }

    private float countPossibleMoves(Round round, boolean me) {
        float count = 0.0F;
        TableInfo tableInfo = round.getTableInfo();
        if (me) {
            for (Tile tile : round.getMyTiles()) {
                count += countMove(tile, tableInfo);
            }
        } else {
            for (Map.Entry<Tile, Float> entry : round.getOpponentTiles().entrySet()) {
                count += entry.getValue() * countMove(entry.getKey(), tableInfo);
            }
        }
        return count;
    }

    private int countMove(Tile tile, TableInfo tableInfo) {
        int count = 0;
        int left = tile.getLeft();
        int right = tile.getRight();
        if (tableInfo.getLeft() != null) {
            if (tableInfo.getLeft().getOpenSide() == left || tableInfo.getLeft().getOpenSide() == right) {
                count++;
            }
        }
        if (tableInfo.getRight() != null) {
            if (tableInfo.getRight().getOpenSide() == left || tableInfo.getRight().getOpenSide() == right) {
                count++;
            }
        }
        if (tableInfo.getTop() != null) {
            if (tableInfo.getTop().getOpenSide() == left || tableInfo.getTop().getOpenSide() == right) {
                count++;
            }
        }
        if (tableInfo.getBottom() != null) {
            if (tableInfo.getBottom().getOpenSide() == left || tableInfo.getBottom().getOpenSide() == right) {
                count++;
            }
        }
        return count;
    }
}
