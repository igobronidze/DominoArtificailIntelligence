package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticType;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.List;

public class MixedRoundHeuristic extends RoundHeuristic {

	private final Logger logger = Logger.getLogger(RoundHeuristic.class);

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam mixedRoundHeuristicTilesDiffRate = new SysParam("mixedRoundHeuristicTilesDiffRate", "2");

	private final SysParam mixedRoundHeuristicMovesDiffRate = new SysParam("mixedRoundHeuristicMovesDiffRate", "10");

	private final SysParam mixedRoundHeuristicPointsBalancingRate = new SysParam("mixedRoundHeuristicPointsBalancingRate", "0.3");

	private final SysParam mixedRoundHeuristicOpenTilesSumBalancingRate = new SysParam("mixedRoundHeuristicOpenTilesSumBalancingRate", "0.15");

	private final SysParam mixedRoundHeuristicPointsDiffCoefficientRate = new SysParam("mixedRoundHeuristicPointsDiffCoefficientRate", "0.5");

	private final SysParam mixedRoundHeuristicPLayTurnRate = new SysParam("mixedRoundHeuristicPLayTurnRate", "0.3");

	private double mixedRoundHeuristicTilesDiffRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicTilesDiffRate);

	private double mixedRoundHeuristicMovesDiffRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicMovesDiffRate);

	private double mixedRoundHeuristicPointsBalancingRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicPointsBalancingRate);

	private double mixedRoundHeuristicOpenTilesSumBalancingRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicOpenTilesSumBalancingRate);

	private double mixedRoundHeuristicPointsDiffCoefficientRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicPointsDiffCoefficientRate);

	private double mixedRoundHeuristicPLayTurnRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicPLayTurnRate);

	@Override
	public void setParams(List<Double> params) {
		super.setParams(params.subList(0, 5));
		mixedRoundHeuristicTilesDiffRateValue = params.get(5);
		mixedRoundHeuristicMovesDiffRateValue = params.get(6);
		mixedRoundHeuristicPointsBalancingRateValue = params.get(7);
		mixedRoundHeuristicOpenTilesSumBalancingRateValue = params.get(8);
		mixedRoundHeuristicPointsDiffCoefficientRateValue = params.get(9);
	}

	@Override
	public double getNotFinishedRoundHeuristic(Round round) {
		roundStatisticProcessor.replaceRound(round);

		double pointForWin = CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin();
//		logger.info("pointForWin: " + pointForWin);
		double myPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_POINT);
//		logger.info("myPoint: " + myPoint);
		double opponentPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_POINT);
//		logger.info("opponentPoint: " + opponentPoint);
		double myTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_TILES_COUNT);
//		logger.info("myTilesCount: " + myTilesCount);
		double opponentTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_TILES_COUNT);
//		logger.info("opponentTilesCount: " + opponentTilesCount);
		double proportionalPointsDiffCoefficient = getPointsDiffCoefficient(myPoint, opponentPoint, pointForWin, true);
//		logger.info("proportionalPointsDiffCoefficient: " + proportionalPointsDiffCoefficient);
		double inverseProportionalPointsDiffCoefficient = getPointsDiffCoefficient(myPoint, opponentPoint, pointForWin, false);
//		logger.info("inverseProportionalPointsDiffCoefficient: " + inverseProportionalPointsDiffCoefficient);

		// Point Diff
		double balancedMyPoint = myPoint * (myPoint >= pointForWin ? (1.0 + mixedRoundHeuristicPointsBalancingRateValue) :
				(1.0 + mixedRoundHeuristicPointsBalancingRateValue * (myPoint / pointForWin)));
//		logger.info("balancedMyPoint: " + balancedMyPoint);
		double balancedOpponentPoint = opponentPoint * (opponentPoint >= pointForWin ? (1.0 + mixedRoundHeuristicPointsBalancingRateValue) :
				(1.0 + mixedRoundHeuristicPointsBalancingRateValue * (opponentPoint / pointForWin)));
//		logger.info("balancedOpponentPoint: " + balancedOpponentPoint);
		double pointDiff = proportionalPointsDiffCoefficient * (balancedMyPoint - balancedOpponentPoint);
//		logger.info("pointDiff: " + pointDiff);

		// Tiles Diff
		double tilesDiff = mixedRoundHeuristicTilesDiffRateValue * (proportionalPointsDiffCoefficient * myTilesCount - inverseProportionalPointsDiffCoefficient * opponentTilesCount);
//		logger.info("tilesDiff: " + tilesDiff);

		// Moves Diff
		double myMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_PLAY_OPTIONS_COUNT) / myTilesCount;
//		logger.info("myMovesCount: " + myMovesCount);
		double opponentMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_PLAY_OPTIONS_COUNT) / opponentTilesCount;
//		logger.info("opponentMovesCount: " + opponentMovesCount);
		double movesDiff = mixedRoundHeuristicMovesDiffRateValue * (proportionalPointsDiffCoefficient * myMovesCount - inverseProportionalPointsDiffCoefficient * opponentMovesCount);
//		logger.info("movesDiff: " + movesDiff);

		// Open tiles sum
		double openTilesSum = roundStatisticProcessor.getStatistic(RoundStatisticType.OPEN_TILES_SUM);
//		logger.info("openTilesSum: " + openTilesSum);
		double balancedOpenTilesSum = openTilesSum * mixedRoundHeuristicOpenTilesSumBalancingRateValue * inverseProportionalPointsDiffCoefficient;
//		logger.info("balancedOpenTilesSum: " + balancedOpenTilesSum);

		// Play turn
		double playTurn = roundStatisticProcessor.getStatistic(RoundStatisticType.PLAY_TURN);
//		logger.info("playTurn: " + playTurn);
		double balancedPlayTurn = mixedRoundHeuristicPLayTurnRateValue * playTurn;
//		logger.info("balancedPlayTurn: " + balancedPlayTurn);


		return pointDiff + tilesDiff + movesDiff + balancedOpenTilesSum + balancedPlayTurn;
	}

	private double getPointsDiffCoefficient(double myPoint, double opponentPoint, double pointForWin, boolean proportional) {
		double diff = (myPoint - opponentPoint) / pointForWin * mixedRoundHeuristicPointsDiffCoefficientRateValue;
		if (!proportional) {
			diff *= -1;
		}
		return 1.0 + diff;
	}
}
