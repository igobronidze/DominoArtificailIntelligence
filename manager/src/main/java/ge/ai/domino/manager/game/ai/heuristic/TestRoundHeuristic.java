package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class TestRoundHeuristic implements RoundHeuristic {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam testRoundHeuristicParam1 = new SysParam("testRoundHeuristicParam1", "5");

	@Override
	public double getHeuristic(Round round) {
		double heuristic = round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint();
		return heuristic + sysParamManager.getDoubleParameterValue(testRoundHeuristicParam1) * (round.getTableInfo().getOpponentTilesCount() - round.getMyTiles().size());
	}
}
