package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticType;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class MixedRoundHeuristic extends RoundHeuristic {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam mixedRoundHeuristicTilesDiffRate = new SysParam("mixedRoundHeuristicTilesDiffRate", "2");

	private final SysParam mixedRoundHeuristicMovesDiffRate = new SysParam("mixedRoundHeuristicMovesDiffRate", "10");

	private final SysParam mixedRoundHeuristicPointsBalancingRate = new SysParam("mixedRoundHeuristicPointsBalancingRate", "0.3");

	private final double mixedRoundHeuristicTilesDiffRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicTilesDiffRate);

	private final double mixedRoundHeuristicMovesDiffRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicMovesDiffRate);

	private final double mixedRoundHeuristicPointsBalancingRateValue = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicPointsBalancingRate);

	@Override
	public double getNotFinishedRoundHeuristic(Round round) {
		roundStatisticProcessor.replaceRound(round);

		double pointForWin = CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin();
		double myPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_POINT);
		double opponentPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_POINT);
		double myTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_TILES_COUNT);
		double opponentTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_TILES_COUNT);
		double pointsDiffCoefficient = Math.abs(myPoint - opponentPoint) / pointForWin;

		// Point Diff
		double balancedMyPoint = myPoint * (myPoint >= pointForWin ? (1.0 + mixedRoundHeuristicPointsBalancingRateValue) :
				(1.0 + mixedRoundHeuristicPointsBalancingRateValue * (myPoint / pointForWin)));
		double balancedOpponentPoint = opponentPoint * (opponentPoint >= pointForWin ? (1.0 + mixedRoundHeuristicPointsBalancingRateValue) :
				(1.0 + mixedRoundHeuristicPointsBalancingRateValue * (opponentPoint / pointForWin)));
		double pointDiff = balancedMyPoint - balancedOpponentPoint;

		// Tiles Diff
		double balancedMyTilesCount = myTilesCount - myTilesCount * pointsDiffCoefficient;
		double balancedOpponentTilesCount = opponentTilesCount - opponentTilesCount * pointsDiffCoefficient;
		double tilesDiff = mixedRoundHeuristicTilesDiffRateValue * (balancedOpponentTilesCount - balancedMyTilesCount);

		// Moves Diff
		double myMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_PLAY_OPTIONS_COUNT) / myTilesCount;
		double opponentMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_PLAY_OPTIONS_COUNT) / opponentTilesCount;
		double balancesMyMovesCount =  myMovesCount * pointsDiffCoefficient;
		double balancedOpponentMovesCount = opponentMovesCount * pointsDiffCoefficient;
		double movesDiff = mixedRoundHeuristicMovesDiffRateValue * (balancesMyMovesCount - balancedOpponentMovesCount);

		return pointDiff + tilesDiff + movesDiff;
	}
}
