package ge.ai.domino.server.manager.game.heuristic;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

public class RoundHeuristicHelper {

    private static final SystemParameterManager sysParamManager = new SystemParameterManager();

    private static final SysParam heuristicValueForStartNextRound = new SysParam("heuristicValueForStartNextRound", "15");

    public static double getFinishedGameHeuristic(GameInfo gameInfo, int pointForWin) {
        if (gameInfo.getMyPoint() > gameInfo.getOpponentPoint()) {
            return pointForWin;
        } else {
            return - 1 * pointForWin;
        }
    }

    public static double getFinishedRoundHeuristic(GameInfo gameInfo, boolean startMe) {
        int value = sysParamManager.getIntegerParameterValue(heuristicValueForStartNextRound);
        return gameInfo.getMyPoint() - gameInfo.getOpponentPoint() + (startMe ? value : -1 * value);
    }
}
