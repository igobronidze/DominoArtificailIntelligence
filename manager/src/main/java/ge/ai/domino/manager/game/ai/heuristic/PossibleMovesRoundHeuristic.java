package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class PossibleMovesRoundHeuristic implements RoundHeuristic {

    private static Logger logger = Logger.getLogger(PossibleMovesRoundHeuristic.class);

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam coefficientForComplexHeuristic = new SysParam("coefficientForComplexHeuristic", "12");

    @Override
    public double getHeuristic(Round round, boolean logTrace) {
        double pointDiff = round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint();
        RoundHeuristic.logInfo(logger, "Point diff is " + pointDiff, logTrace);

        int myTilesCount = round.getMyTiles().size();
        double movesDiff = sysParamManager.getIntegerParameterValue(coefficientForComplexHeuristic) * (countPossibleMoves(round, true) / myTilesCount / myTilesCount -
                countPossibleMoves(round, false) / round.getTableInfo().getOpponentTilesCount() / round.getTableInfo().getOpponentTilesCount());
        RoundHeuristic.logInfo(logger, "Moves diff is " + movesDiff, logTrace);

        return pointDiff + movesDiff;
    }

    private double countPossibleMoves(Round round, boolean me) {
        double count = 0.0;
        TableInfo tableInfo = round.getTableInfo();
        if (me) {
            for (Tile tile : round.getMyTiles()) {
                count += RoundHeuristicHelper.countMoveOnTable(tile, tableInfo);
            }
        } else {
            for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
                count += entry.getValue() * RoundHeuristicHelper.countMoveOnTable(entry.getKey(), tableInfo);
            }
        }
        return count;
    }
}
