package ge.ai.domino.server.manager.domino.heuristic;

import ge.ai.domino.domain.domino.GameInfo;

public class HandHeuristicHelper {

    private static final int POINT_FOR_START_NEXT_HAND = 10;

    public static double getFinishedGameHeuristic(GameInfo gameInfo, int pointForWin) {
        if (gameInfo.getMyPoints() > gameInfo.getHimPoints()) {
            return pointForWin;
        } else {
            return - 1 * pointForWin;
        }
    }

    public static double getFinishedHandHeuristic(GameInfo gameInfo, boolean startMe) {
        return gameInfo.getMyPoints() - gameInfo.getHimPoints() + (startMe ? POINT_FOR_START_NEXT_HAND : -1 * POINT_FOR_START_NEXT_HAND);
    }
}
