package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticProcessor;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;

public abstract class RoundHeuristic {

    private final Logger logger = Logger.getLogger(RoundHeuristic.class);

    // TODO[IG] removed static for multithreading
    protected final RoundStatisticProcessor roundStatisticProcessor = new RoundStatisticProcessor();

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final SysParam logAboutRoundHeuristic = new SysParam("logAboutRoundHeuristic", "true");

    private static final SysParam heuristicValueForStartNextRound = new SysParam("heuristicValueForStartNextRound", "7");

    private static final SysParam rateForFinishedGameHeuristic = new SysParam("rateForFinishedGameHeuristic", "1.6");

    private double heuristicValueForStartNextRoundValue = systemParameterManager.getDoubleParameterValue(heuristicValueForStartNextRound);

    private double rateForFinishedGameHeuristicValue = systemParameterManager.getDoubleParameterValue(rateForFinishedGameHeuristic);

    abstract double getNotFinishedRoundHeuristic(Round round);

    public void setParams(List<Double> params) {
        roundStatisticProcessor.setParams(params.subList(0, 3));
        heuristicValueForStartNextRoundValue = params.get(3);
        rateForFinishedGameHeuristicValue = params.get(4);
    }

    public double getHeuristic(Round round, boolean forceLog) {
        if (round.getGameInfo().isFinished()) {
            return getFinishedGameHeuristic(round.getGameInfo(), CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin());
        }
        if (isNewRound(round)) {
            return getFinishedRoundHeuristic(round.getGameInfo(), round.getTableInfo().isMyMove(), CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin());
        }

        Random random = new Random();
        double r = random.nextDouble();
        boolean logHeuristicInfo = forceLog || (r < (1.0 / 1000)) && systemParameterManager.getBooleanParameterValue(logAboutRoundHeuristic);
        if (logHeuristicInfo) {
            logger.info("******************************RoundHeuristic(" + this.getClass().getSimpleName() + ")******************************");
            RoundLogger.logRoundFullInfo(round);
        }

        double heuristic = getNotFinishedRoundHeuristic(round);

        if (logHeuristicInfo) {
            logger.info("Heuristic: " + heuristic);
            logger.info("************************************************************");
        }

        return heuristic;
    }

    public double getBalancedPointDiff(double myPoint, double opponentPoint, double pointForWin) {
        return myPoint - opponentPoint;
    }

    private double getFinishedGameHeuristic(GameInfo gameInfo, int pointForWin) {
        if (gameInfo.getMyPoint() > gameInfo.getOpponentPoint()) {
            return pointForWin * rateForFinishedGameHeuristicValue;
        } else {
            return - 1 * pointForWin * rateForFinishedGameHeuristicValue;
        }
    }

    private double getFinishedRoundHeuristic(GameInfo gameInfo, boolean startMe, double pointForWin) {
        return getBalancedPointDiff(gameInfo.getMyPoint(), gameInfo.getOpponentPoint(), pointForWin)
                + (startMe ? heuristicValueForStartNextRoundValue : -1 * heuristicValueForStartNextRoundValue);
    }

    private boolean isNewRound(Round round) {
        return round.getMyTiles().size() == 0 && round.getTableInfo().getOpponentTilesCount() == 7 && round.getTableInfo().getBazaarTilesCount() == 21;
    }
}
