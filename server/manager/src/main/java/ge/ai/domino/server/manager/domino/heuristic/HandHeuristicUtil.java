package ge.ai.domino.server.manager.domino.heuristic;

import ge.ai.domino.domain.domino.TableInfo;

public class HandHeuristicUtil {

    private static final int POINT_FOR_START_NEXT_HAND = 15;

    public static Double getFinishedGameHeuristic(TableInfo tableInfo) {
        return tableInfo.getMyTilesCount() - tableInfo.getHimTilesCount();
    }

    public static Double getFinishedHandHeuristic(TableInfo tableInfo) {
        return tableInfo.getMyTilesCount() - tableInfo.getHimTilesCount() + (tableInfo.isMyTurn() ? POINT_FOR_START_NEXT_HAND : -1 * POINT_FOR_START_NEXT_HAND);
    }
}
