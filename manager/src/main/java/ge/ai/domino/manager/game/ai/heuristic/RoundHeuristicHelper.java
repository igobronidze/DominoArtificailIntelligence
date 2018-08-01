package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class RoundHeuristicHelper {

    private static final SystemParameterManager sysParamManager = new SystemParameterManager();

    private static final SysParam heuristicValueForStartNextRound = new SysParam("heuristicValueForStartNextRound", "15");

    private static final SysParam rateForFinishedGameHeuristic = new SysParam("rateForFinishedGameHeuristic", "1.0");

    public static double getFinishedGameHeuristic(GameInfo gameInfo, int pointForWin) {
        if (gameInfo.getMyPoint() > gameInfo.getOpponentPoint()) {
            return pointForWin * sysParamManager.getDoubleParameterValue(rateForFinishedGameHeuristic);
        } else {
            return - 1 * pointForWin * sysParamManager.getDoubleParameterValue(rateForFinishedGameHeuristic);
        }
    }

    public static double getFinishedRoundHeuristic(GameInfo gameInfo, boolean startMe) {
        int value = sysParamManager.getIntegerParameterValue(heuristicValueForStartNextRound);
        return gameInfo.getMyPoint() - gameInfo.getOpponentPoint() + (startMe ? value : -1 * value);
    }

    public static int countMoveOnTable(Tile tile, TableInfo tableInfo) {
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
