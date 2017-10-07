package ge.ai.domino.server.manager.domino.heuristic;

import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

public class HandHeuristicHelper {

    private static final SystemParameterManager sysParamManager = new SystemParameterManager();

    private static final SysParam heuristicValueForStartNextHand = new SysParam("heuristicValueForStartNextHand", "15");

    public static double getFinishedGameHeuristic(GameInfo gameInfo, int pointForWin) {
        if (gameInfo.getMyPoints() > gameInfo.getHimPoints()) {
            return pointForWin;
        } else {
            return - 1 * pointForWin;
        }
    }

    public static double getFinishedHandHeuristic(GameInfo gameInfo, boolean startMe) {
        int value = sysParamManager.getIntegerParameterValue(heuristicValueForStartNextHand);
        return gameInfo.getMyPoints() - gameInfo.getHimPoints() + (startMe ? value : -1 * value);
    }
}
