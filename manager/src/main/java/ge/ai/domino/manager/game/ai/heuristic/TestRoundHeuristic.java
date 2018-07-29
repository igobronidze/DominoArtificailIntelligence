package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class TestRoundHeuristic implements RoundHeuristic {

	private static Logger logger = Logger.getLogger(TestRoundHeuristic.class);

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam testRoundHeuristicParam1 = new SysParam("testRoundHeuristicParam1", "3");

	private final SysParam testRoundHeuristicParam2 = new SysParam("testRoundHeuristicParam2", "3");

	private final SysParam testRoundHeuristicParam3 = new SysParam("testRoundHeuristicParam3", "0.5");

	private final SysParam testRoundHeuristicParam4 = new SysParam("testRoundHeuristicParam4", "0.25");

	private final SysParam testRoundHeuristicParam5 = new SysParam("testRoundHeuristicParam5", "0.1");

	@Override
	public double getHeuristic(Round round, boolean logTrace) {
		double pointDiff = round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint();
		RoundHeuristic.logInfo(logger, "Point diff is " + pointDiff, logTrace);

		double myMovesCount = countPossibleMoves(round, true);
		RoundHeuristic.logInfo(logger, "My moves count is " + myMovesCount, logTrace);
		double opponentMovesCount = countPossibleMoves(round, false);
		RoundHeuristic.logInfo(logger, "Opponent moves count is " + opponentMovesCount, logTrace);

		double movesDiff = sysParamManager.getIntegerParameterValue(testRoundHeuristicParam2) *
				(myMovesCount / round.getMyTiles().size() - opponentMovesCount / round.getTableInfo().getOpponentTilesCount());
		RoundHeuristic.logInfo(logger, "Moves diff is " + movesDiff, logTrace);

		double tilesDiff = sysParamManager.getDoubleParameterValue(testRoundHeuristicParam1) * (round.getTableInfo().getOpponentTilesCount() - round.getMyTiles().size());
		RoundHeuristic.logInfo(logger, "Tiles diff is " + tilesDiff, logTrace);

		return pointDiff + movesDiff + tilesDiff;
	}

	private double countPossibleMoves(Round round, boolean me) {
		double count = 0.0;
		TableInfo tableInfo = round.getTableInfo();
		if (me) {
			for (Tile tile : round.getMyTiles()) {
				count += countMoveOnTable(tile, tableInfo) + countFuturePossibleMovesForMe(tile, round);
			}
		} else {
			for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
				count += entry.getValue() * (countMoveOnTable(entry.getKey(), tableInfo) + countFuturePossibleMovesForOpponent(entry.getKey(), round));
			}
		}
		return count;
	}

	private double countFuturePossibleMovesForOpponent(Tile opponentTile, Round round) {
		double count = 0.0;
		for (int i = 6; i >= 0; i--) {
			for (int j = i; j >= 0; j--) {
				Tile tile = new Tile(i, j);
				if (!tile.equals(opponentTile)) {
					if (round.getMyTiles().contains(tile)) {
						count += sysParamManager.getDoubleParameterValue(testRoundHeuristicParam4);
					} else if (round.getOpponentTiles().containsKey(tile)) {
						double prob = round.getOpponentTiles().get(tile);
						count += prob * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam3);
						count += (1 - prob) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam5);
					}
				}
			}
		}
		return count;
	}

	private double countFuturePossibleMovesForMe(Tile myTile, Round round) {
		double count = 0.0;
		for (int i = 6; i >= 0; i--) {
			for (int j = i; j >= 0; j--) {
				Tile tile = new Tile(i, j);
				if (!tile.equals(myTile)) {
					if (round.getMyTiles().contains(tile)) {
						count += sysParamManager.getDoubleParameterValue(testRoundHeuristicParam3);
					} else if (round.getOpponentTiles().containsKey(tile)) {
						double prob = round.getOpponentTiles().get(tile);
						count += prob * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam4);
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

	private int countMoveOnTable(Tile tile, TableInfo tableInfo) {
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
