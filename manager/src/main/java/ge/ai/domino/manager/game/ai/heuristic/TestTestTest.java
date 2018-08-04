package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestTestTest implements RoundHeuristic {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam testRoundHeuristicParam1 = new SysParam("testRoundHeuristicParam1", "2");

	private final SysParam testRoundHeuristicParam2 = new SysParam("testRoundHeuristicParam2", "8");

	private final SysParam testRoundHeuristicParam3 = new SysParam("testRoundHeuristicParam3", "0.4");

	private final SysParam testRoundHeuristicParam4 = new SysParam("testRoundHeuristicParam4", "0.2");

	private final SysParam testRoundHeuristicParam5 = new SysParam("testRoundHeuristicParam5", "0.1");

	@Override
	public double getHeuristic(Round round, boolean logTrace) {
		double pointDiff = round.getGameInfo().getMyPoint() - round.getGameInfo().getOpponentPoint();

		double tilesDiff = sysParamManager.getDoubleParameterValue(testRoundHeuristicParam1) * (round.getTableInfo().getOpponentTilesCount() - round.getMyTiles().size());

		TilesStatistic tilesStatistic = getTilesStatistic(round);
		double myMovesCount = countPossibleMoves(round.getMyTiles(), tilesStatistic);
		double opponentMovesCount = countPossibleMoves(round.getOpponentTiles(), round.getTableInfo().getOpponentTilesCount(), round.getTableInfo().getBazaarTilesCount(), tilesStatistic);
		double movesDiff = sysParamManager.getDoubleParameterValue(testRoundHeuristicParam2) *
				(myMovesCount / round.getMyTiles().size() - opponentMovesCount / round.getTableInfo().getOpponentTilesCount());

		return pointDiff + tilesDiff + movesDiff;
	}

	private double countPossibleMoves(Map<Tile, Double> opponentTiles, double opponentTilesCount, double bazaarTilesCount, TilesStatistic tilesStatistic) {
		double result = 0.0;
		for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
			Tile tile = entry.getKey();

			double countForTile = 0.0;
			countForTile += tilesStatistic.getOnTable().get(tile.getLeft());
			countForTile += tilesStatistic.getForMe().get(tile.getLeft()) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam4);
			countForTile += ((tilesStatistic.getForOpponent().get(tile.getLeft()) - entry.getValue()) / opponentTilesCount * (opponentTilesCount  -1))
					* sysParamManager.getDoubleParameterValue(testRoundHeuristicParam3);
			countForTile += ((tilesStatistic.getForBazaar().get(tile.getLeft()) - (1 - entry.getValue())) / bazaarTilesCount * (bazaarTilesCount - 1))
					* sysParamManager.getDoubleParameterValue(testRoundHeuristicParam5);
			if (tile.getLeft() != tile.getRight()) {
				countForTile += tilesStatistic.getOnTable().get(tile.getRight());
				countForTile += tilesStatistic.getForMe().get(tile.getRight()) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam4);
				countForTile += ((tilesStatistic.getForOpponent().get(tile.getRight()) - entry.getValue()) / opponentTilesCount * (opponentTilesCount  -1))
						* sysParamManager.getDoubleParameterValue(testRoundHeuristicParam3);
				countForTile += ((tilesStatistic.getForBazaar().get(tile.getRight()) - (1 - entry.getValue())) / bazaarTilesCount * (bazaarTilesCount - 1))
						* sysParamManager.getDoubleParameterValue(testRoundHeuristicParam5);
			}
			result += countForTile * entry.getValue();
		}
		return result;
	}

	private double countPossibleMoves(Set<Tile> myTiles, TilesStatistic tilesStatistic) {
		double result = 0.0;
		for (Tile tile : myTiles) {
			double countForTile = 0.0;
			countForTile += tilesStatistic.getOnTable().get(tile.getLeft());
			countForTile += (tilesStatistic.getForMe().get(tile.getLeft()) - 1) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam3);
			countForTile += tilesStatistic.getForOpponent().get(tile.getLeft()) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam4);
			countForTile += tilesStatistic.getForBazaar().get(tile.getLeft()) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam5);
			if (tile.getLeft() != tile.getRight()) {
				countForTile += tilesStatistic.getOnTable().get(tile.getRight());
				countForTile += (tilesStatistic.getForMe().get(tile.getRight()) - 1) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam3);
				countForTile += tilesStatistic.getForOpponent().get(tile.getRight()) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam4);
				countForTile += tilesStatistic.getForBazaar().get(tile.getRight()) * sysParamManager.getDoubleParameterValue(testRoundHeuristicParam5);
			}
			result += countForTile;
		}
		return result;
	}

	private TilesStatistic getTilesStatistic(Round round) {
		TilesStatistic tilesStatistic = new TilesStatistic();

		setOnTable(round, tilesStatistic);
		setForMe(round, tilesStatistic);
		setForOpponentAndBazaar(round, tilesStatistic);

		return tilesStatistic;
	}

	private void setOnTable(Round round, TilesStatistic tilesStatistic) {
		Map<Integer, Double> onTable = getInitMap();
		TableInfo tableInfo = round.getTableInfo();
		if (tableInfo.getLeft() != null) {
			onTable.put(tableInfo.getLeft().getOpenSide(), onTable.get(tableInfo.getLeft().getOpenSide()) + 1);
		}
		if (tableInfo.getRight() != null) {
			onTable.put(tableInfo.getRight().getOpenSide(), onTable.get(tableInfo.getRight().getOpenSide()) + 1);
		}
		if (tableInfo.getTop() != null) {
			onTable.put(tableInfo.getTop().getOpenSide(), onTable.get(tableInfo.getTop().getOpenSide()) + 1);
		}
		if (tableInfo.getBottom() != null) {
			onTable.put(tableInfo.getBottom().getOpenSide(), onTable.get(tableInfo.getBottom().getOpenSide()) + 1);
		}
		tilesStatistic.setOnTable(onTable);
	}

	private void setForMe(Round round, TilesStatistic tilesStatistic) {
		Map<Integer, Double> forMe = getInitMap();
		for (Tile tile : round.getMyTiles()) {
			forMe.put(tile.getLeft(), forMe.get(tile.getLeft()) + 1);
			if (tile.getLeft() != tile.getRight()) {
				forMe.put(tile.getRight(), forMe.get(tile.getRight()) + 1);
			}
		}
		tilesStatistic.setForMe(forMe);
	}

	private void setForOpponentAndBazaar(Round round, TilesStatistic tilesStatistic) {
		Map<Integer, Double> forOpponent = getInitMap();
		Map<Integer, Double> forBazaar = getInitMap();
		for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
			forOpponent.put(entry.getKey().getLeft(), forOpponent.get(entry.getKey().getLeft()) + entry.getValue());
			forBazaar.put(entry.getKey().getLeft(), forBazaar.get(entry.getKey().getLeft()) + 1 - entry.getValue());
			if (entry.getKey().getLeft() != entry.getKey().getRight()) {
				forOpponent.put(entry.getKey().getRight(), forOpponent.get(entry.getKey().getRight()) + entry.getValue());
				forBazaar.put(entry.getKey().getRight(), forBazaar.get(entry.getKey().getRight()) + 1 - entry.getValue());
			}
		}

		tilesStatistic.setForOpponent(forOpponent);
		tilesStatistic.setForBazaar(forBazaar);
	}

	private Map<Integer, Double> getInitMap() {
		Map<Integer, Double> map = new HashMap<>();
		for (int i = 0; i < 7; i++) {
			map.put(i, 0.0);
		}
		return map;
	}
}
