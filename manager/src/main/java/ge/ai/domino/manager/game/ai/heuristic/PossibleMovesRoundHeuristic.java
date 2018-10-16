package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticType;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.Map;

public class PossibleMovesRoundHeuristic extends RoundHeuristic {

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam coefficientForComplexHeuristic = new SysParam("coefficientForComplexHeuristic", "12");

    @Override
    public double getNotFinishedRoundHeuristic(Round round) {
        roundStatisticProcessor.replaceRound(round);

        double myPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_POINT);
        double opponentPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_POINT);
        double pointDiff = myPoint - opponentPoint;

        double myTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_TILES_COUNT);
        double opponentTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_TILES_COUNT);
        double movesDiff = sysParamManager.getIntegerParameterValue(coefficientForComplexHeuristic) * (countPossibleMoves(round, true) / myTilesCount / myTilesCount -
                countPossibleMoves(round, false) / opponentTilesCount / opponentTilesCount);

        return pointDiff + movesDiff;
    }

    private double countPossibleMoves(Round round, boolean me) {
        double count = 0.0;
        TableInfo tableInfo = round.getTableInfo();
        if (me) {
            for (Tile tile : round.getMyTiles()) {
                count += countMoveOnTable(tile, tableInfo);
            }
        } else {
            for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
                count += entry.getValue() * countMoveOnTable(entry.getKey(), tableInfo);
            }
        }
        return count;
    }

    private static int countMoveOnTable(Tile tile, TableInfo tableInfo) {
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
