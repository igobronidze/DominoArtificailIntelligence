package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticProcessor;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.Random;

public abstract class RoundHeuristic {

    private final Logger logger = Logger.getLogger(RoundHeuristic.class);

    protected static final RoundStatisticProcessor roundStatisticProcessor = new RoundStatisticProcessor();

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final SysParam logAboutRoundHeuristic = new SysParam("logAboutRoundHeuristic", "true");

    private static final SysParam heuristicValueForStartNextRound = new SysParam("heuristicValueForStartNextRound", "12");

    private static final SysParam rateForFinishedGameHeuristic = new SysParam("rateForFinishedGameHeuristic", "1.0");

    abstract double getNotFinishedRoundHeuristic(Round round);

    public double getHeuristic(Round round, boolean forceLog) {
        if (round.getGameInfo().isFinished()) {
            return getFinishedGameHeuristic(round.getGameInfo(), CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin());
        }
        if (isNewRound(round)) {
            return getFinishedRoundHeuristic(round.getGameInfo(), round.getTableInfo().isMyMove());
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

    static void logInfo(Logger logger, String text, boolean log) {
        if (log) {
            logger.info(text);
        }
    }

    private double getFinishedGameHeuristic(GameInfo gameInfo, int pointForWin) {
        if (gameInfo.getMyPoint() > gameInfo.getOpponentPoint()) {
            return pointForWin * systemParameterManager.getDoubleParameterValue(rateForFinishedGameHeuristic);
        } else {
            return - 1 * pointForWin * systemParameterManager.getDoubleParameterValue(rateForFinishedGameHeuristic);
        }
    }

    private double getFinishedRoundHeuristic(GameInfo gameInfo, boolean startMe) {
        int value = systemParameterManager.getIntegerParameterValue(heuristicValueForStartNextRound);
        return gameInfo.getMyPoint() - gameInfo.getOpponentPoint() + (startMe ? value : -1 * value);
    }

    private boolean isNewRound(Round round) {
        return round.getMyTiles().size() == 0 && round.getTableInfo().getOpponentTilesCount() == 7 && round.getTableInfo().getBazaarTilesCount() == 21;
    }
}
