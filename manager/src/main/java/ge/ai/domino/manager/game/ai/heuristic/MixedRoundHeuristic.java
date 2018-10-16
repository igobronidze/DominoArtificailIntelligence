package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.statistic.RoundStatisticType;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class MixedRoundHeuristic extends RoundHeuristic {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam mixedRoundHeuristicParam1 = new SysParam("mixedRoundHeuristicParam1", "2");

	private final SysParam mixedRoundHeuristicParam2 = new SysParam("mixedRoundHeuristicParam2", "10");

	private final SysParam mixedRoundHeuristicParam6 = new SysParam("mixedRoundHeuristicParam6", "0.3");

	private final SysParam mixedRoundHeuristicParam7 = new SysParam("mixedRoundHeuristicParam7", "2.6");

	private final double mixedRoundHeuristicParam1Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam1);

	private final double mixedRoundHeuristicParam2Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam2);

	private final double mixedRoundHeuristicParam6Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam6);

	private final double mixedRoundHeuristicParam7Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam7);

	@Override
	public double getNotFinishedRoundHeuristic(Round round) {
		roundStatisticProcessor.replaceRound(round);

		double pointForWin = CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin();

		double myPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_POINT);
		double opponentPoint = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_POINT);
		double myTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_TILES_COUNT);
		double opponentTilesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_TILES_COUNT);

		double balancedMyPoint = myPoint * (myPoint >= pointForWin ? (1.0 + mixedRoundHeuristicParam6Value) :
				(1.0 + mixedRoundHeuristicParam6Value * (myPoint / pointForWin)));
		double balancedOpponentPoint = opponentPoint * (opponentPoint >= pointForWin ? (1.0 + mixedRoundHeuristicParam6Value) :
				(1.0 + mixedRoundHeuristicParam6Value * (opponentPoint / pointForWin)));
		double pointDiff = balancedMyPoint - balancedOpponentPoint;

		double tilesDiff = mixedRoundHeuristicParam1Value * (opponentTilesCount - myTilesCount);

		double myMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.MY_PLAY_OPTIONS_COUNT);
		if (myPoint - opponentPoint >= 60) {
			myMovesCount *= mixedRoundHeuristicParam7Value;
		} else if (myPoint - opponentPoint >= 30) {
			myMovesCount *= mixedRoundHeuristicParam7Value / 2;
		}

		double opponentMovesCount = roundStatisticProcessor.getStatistic(RoundStatisticType.OPPONENT_PLAY_OPTIONS_COUNT);
		if (opponentPoint - myPoint >= 60) {
			opponentMovesCount *= mixedRoundHeuristicParam7Value;
		} else if (opponentPoint - myPoint >= 30) {
			opponentMovesCount *= mixedRoundHeuristicParam7Value / 2;
		}

		double movesDiff = mixedRoundHeuristicParam2Value * (myMovesCount / myTilesCount - opponentMovesCount / opponentTilesCount);

		return pointDiff + tilesDiff + movesDiff;
	}
}
