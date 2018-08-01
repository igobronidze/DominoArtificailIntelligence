package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;

public class TestRoundHeuristic implements RoundHeuristic {

	private static Logger logger = Logger.getLogger(TestRoundHeuristic.class);

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam testRoundHeuristicParam1 = new SysParam("testRoundHeuristicParam1", "2");

	private final SysParam testRoundHeuristicParam2 = new SysParam("testRoundHeuristicParam2", "8");

	private final SysParam testRoundHeuristicParam3 = new SysParam("testRoundHeuristicParam3", "0.4");

	private final SysParam testRoundHeuristicParam4 = new SysParam("testRoundHeuristicParam4", "0.2");

	private final SysParam testRoundHeuristicParam5 = new SysParam("testRoundHeuristicParam5", "0.1");

	@Override
	public double getHeuristic(Round round, boolean logTrace) {
		double pointDiff = round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint();
		RoundHeuristic.logInfo(logger, "Point diff is " + pointDiff, logTrace);

		double myMovesCount = countPossibleMoves(round, true, logTrace);
		double opponentMovesCount = countPossibleMoves(round, false, logTrace);
		RoundHeuristic.logInfo(logger, "My moves count is " + myMovesCount, logTrace);
		RoundHeuristic.logInfo(logger, "Balanced my moves count is " + (myMovesCount / round.getMyTiles().size()), logTrace);
		RoundHeuristic.logInfo(logger, "Opponent moves count is " + opponentMovesCount, logTrace);
		RoundHeuristic.logInfo(logger, "Balanced opponent moves count is " + (opponentMovesCount / round.getTableInfo().getOpponentTilesCount()), logTrace);

		double tilesDiff = sysParamManager.getDoubleParameterValue(testRoundHeuristicParam1) * (round.getTableInfo().getOpponentTilesCount() - round.getMyTiles().size());
		RoundHeuristic.logInfo(logger, "Tiles diff is " + tilesDiff, logTrace);

		double movesDiff = sysParamManager.getDoubleParameterValue(testRoundHeuristicParam2) *
				(myMovesCount / round.getMyTiles().size() - opponentMovesCount / round.getTableInfo().getOpponentTilesCount());
		RoundHeuristic.logInfo(logger, "Moves diff is " + movesDiff, logTrace);

		return pointDiff + tilesDiff + movesDiff;
	}

	private double countPossibleMoves(Round round, boolean me, boolean logTrace) {
		double count = 0.0;
		TableInfo tableInfo = round.getTableInfo();
		if (me) {
			for (Tile tile : round.getMyTiles()) {
				RoundHeuristic.logInfo(logger, "Tile: " + tile + ", myTurn: true", logTrace);
				int countOnTable = RoundHeuristicHelper.countMoveOnTable(tile, tableInfo);
				double countFutureMoves = countFuturePossibleMoves(tile, round.getMyTiles(), round.getOpponentTiles(), true);
				RoundHeuristic.logInfo(logger, "countOnTable: " + countOnTable, logTrace);
				RoundHeuristic.logInfo(logger, "countFutureMoves: " + countFutureMoves, logTrace);
				count += countOnTable + countFutureMoves;
			}
		} else {
			for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
				RoundHeuristic.logInfo(logger, "Tile: " + entry.getKey() + ", myTurn: false", logTrace);
				int countOnTable = RoundHeuristicHelper.countMoveOnTable(entry.getKey(), tableInfo);

				Map<Tile, Double> opponentTilesClone = CloneUtil.getClone(round.getOpponentTiles());
				double prob = opponentTilesClone.get(entry.getKey());
				opponentTilesClone.put(entry.getKey(), 0.0);
				ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTilesClone, prob - 1);
				double countFutureMoves = countFuturePossibleMoves(entry.getKey(), round.getMyTiles(), opponentTilesClone, false);
				RoundHeuristic.logInfo(logger, "countOnTable: " + countOnTable, logTrace);
				RoundHeuristic.logInfo(logger, "countFutureMoves: " + countFutureMoves, logTrace);

				double movesSum = entry.getValue() * (countOnTable + countFutureMoves);
				RoundHeuristic.logInfo(logger, "movesSum: " + movesSum, logTrace);
				count += movesSum;
			}
		}
		return count;
	}

	private double countFuturePossibleMoves(Tile tileForPlay, Set<Tile> myTiles, Map<Tile, Double> opponentTiles, boolean myTurn) {
		double count = 0.0;
		for (int i = 6; i >= 0; i--) {
			for (int j = i; j >= 0; j--) {
				Tile tile = new Tile(i, j);
				if (!tile.equals(tileForPlay) && isAdjacentTiles(tile, tileForPlay)) {
					if (myTiles.contains(tile)) {
						count += sysParamManager.getDoubleParameterValue(myTurn ? testRoundHeuristicParam3 : testRoundHeuristicParam4);
					} else if (opponentTiles.containsKey(tile)) {
						double prob = opponentTiles.get(tile);
						count += prob * sysParamManager.getDoubleParameterValue(myTurn ? testRoundHeuristicParam4 : testRoundHeuristicParam3);
						count += (1 - prob) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam5);
					}
				}
			}
		}
		return count;
	}

	private boolean isAdjacentTiles(Tile tile1, Tile tile2) {
		return tile1.getLeft() == tile2.getLeft() || tile1.getLeft() == tile2.getRight() || tile1.getRight() == tile2.getLeft() || tile1.getRight() == tile2.getRight();
	}
}
