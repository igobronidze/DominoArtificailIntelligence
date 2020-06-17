package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticType;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.List;

public class MixedRoundHeuristic extends RoundHeuristic {

	private static final int ROUND_HEURISTIC_PARAMS_COUNT = 3;

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	private static final SysParam mixedRoundHeuristicTilesDiffRate = new SysParam("mixedRoundHeuristicTilesDiffRate", "2");

	private static final SysParam mixedRoundHeuristicMovesDiffRate = new SysParam("mixedRoundHeuristicMovesDiffRate", "10");

	private static final SysParam mixedRoundHeuristicPointsBalancingRate = new SysParam("mixedRoundHeuristicPointsBalancingRate", "0.3");

	private static final SysParam mixedRoundHeuristicOpenTilesSumBalancingRate = new SysParam("mixedRoundHeuristicOpenTilesSumBalancingRate", "0.15");

	private static final SysParam mixedRoundHeuristicPointsDiffCoefficientRate = new SysParam("mixedRoundHeuristicPointsDiffCoefficientRate", "0.5");

	private static final SysParam mixedRoundHeuristicPLayTurnRate = new SysParam("mixedRoundHeuristicPLayTurnRate", "0.3");

	private static double mixedRoundHeuristicTilesDiffRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicTilesDiffRate);

	private static double mixedRoundHeuristicMovesDiffRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicMovesDiffRate);

	private static double mixedRoundHeuristicPointsBalancingRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicPointsBalancingRate);

	private static double mixedRoundHeuristicOpenTilesSumBalancingRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicOpenTilesSumBalancingRate);

	private static double mixedRoundHeuristicPointsDiffCoefficientRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicPointsDiffCoefficientRate);

	private static double mixedRoundHeuristicPLayTurnRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicPLayTurnRate);

	@Override
	public void setParams(List<Double> params) {
		super.setParams(params.subList(0, ROUND_HEURISTIC_PARAMS_COUNT));
		int ind = ROUND_HEURISTIC_PARAMS_COUNT;
		mixedRoundHeuristicTilesDiffRateValue = params.get(ind++);
		mixedRoundHeuristicMovesDiffRateValue = params.get(ind++);
		mixedRoundHeuristicPointsBalancingRateValue = params.get(ind++);
		mixedRoundHeuristicOpenTilesSumBalancingRateValue = params.get(ind++);
		mixedRoundHeuristicPointsDiffCoefficientRateValue = params.get(ind++);
		mixedRoundHeuristicPLayTurnRateValue = params.get(ind);
	}

	@Override
	public double getNotFinishedRoundHeuristic(Round round) {
		roundStatisticProcessor.replaceRound(round);

		double pointForWin = CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin();
		double myPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_POINT);
		double opponentPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_POINT);
		double myTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_TILES_COUNT);
		double opponentTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_TILES_COUNT);
		double proportionalPointsDiffCoefficient = getPointsDiffCoefficient(myPoint, opponentPoint, pointForWin, true);
		double inverseProportionalPointsDiffCoefficient = getPointsDiffCoefficient(myPoint, opponentPoint, pointForWin, false);


		double pointDiff = getBalancedPointDiff(myPoint, opponentPoint, pointForWin);

		// Tiles Diff
		double tilesDiff = mixedRoundHeuristicTilesDiffRateValue * (inverseProportionalPointsDiffCoefficient * opponentTilesCount - proportionalPointsDiffCoefficient * myTilesCount);

		// Moves Diff
		double myMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_PLAY_OPTIONS_COUNT) / myTilesCount;
		double opponentMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_PLAY_OPTIONS_COUNT) / opponentTilesCount;
		double movesDiff = mixedRoundHeuristicMovesDiffRateValue * (proportionalPointsDiffCoefficient * myMovesCount - inverseProportionalPointsDiffCoefficient * opponentMovesCount);

		// Open tiles sum
		double openTilesSum = roundStatisticProcessor.getStatistic(RoundStatisticType.OPEN_TILES_SUM);
		double balancedOpenTilesSum = openTilesSum * mixedRoundHeuristicOpenTilesSumBalancingRateValue * inverseProportionalPointsDiffCoefficient;

		// Play turn
		double playTurn = roundStatisticProcessor.getStatistic(RoundStatisticType.PLAY_TURN);
		double balancedPlayTurn = mixedRoundHeuristicPLayTurnRateValue * playTurn;


		return pointDiff + tilesDiff + movesDiff + balancedOpenTilesSum + balancedPlayTurn;
	}

	private double getPointsDiffCoefficient(double myPoint, double opponentPoint, double pointForWin, boolean proportional) {
		double diff = (myPoint - opponentPoint) / pointForWin * mixedRoundHeuristicPointsDiffCoefficientRateValue;
		if (!proportional) {
			diff *= -1;
		}
		return 1.0 + diff;
	}

	public double getBalancedPointDiff(double myPoint, double opponentPoint, double pointForWin) {
		double balancedMyPoint = myPoint * (myPoint >= pointForWin ? (1.0 + mixedRoundHeuristicPointsBalancingRateValue) :
				(1.0 + mixedRoundHeuristicPointsBalancingRateValue * (myPoint / pointForWin)));
		double balancedOpponentPoint = opponentPoint * (opponentPoint >= pointForWin ? (1.0 + mixedRoundHeuristicPointsBalancingRateValue) :
				(1.0 + mixedRoundHeuristicPointsBalancingRateValue * (opponentPoint / pointForWin)));
		return getPointsDiffCoefficient(myPoint, opponentPoint, pointForWin, true) * (balancedMyPoint - balancedOpponentPoint);
	}
}
