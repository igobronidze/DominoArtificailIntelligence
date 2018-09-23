package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MixedRoundHeuristic implements RoundHeuristic {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam mixedRoundHeuristicParam1 = new SysParam("mixedRoundHeuristicParam1", "2");

	private final SysParam mixedRoundHeuristicParam2 = new SysParam("testRoundHeuristicParam2", "10");

	private final SysParam mixedRoundHeuristicParam3 = new SysParam("testRoundHeuristicParam3", "0.4");

	private final SysParam mixedRoundHeuristicParam4 = new SysParam("testRoundHeuristicParam4", "0.2");

	private final SysParam mixedRoundHeuristicParam5 = new SysParam("testRoundHeuristicParam5", "0.1");

	private final SysParam mixedRoundHeuristicParam6 = new SysParam("testRoundHeuristicParam6", "0.5");

	private final SysParam mixedRoundHeuristicParam7 = new SysParam("testRoundHeuristicParam7", "3");

	private final double mixedRoundHeuristicParam1Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam1);

	private final double mixedRoundHeuristicParam2Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam2);

	private final double mixedRoundHeuristicParam3Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam3);

	private final double mixedRoundHeuristicParam4Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam4);

	private final double mixedRoundHeuristicParam5Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam5);

	private final double mixedRoundHeuristicParam6Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam6);

	private final double mixedRoundHeuristicParam7Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam7);

	@Override
	public double getHeuristic(Round round, boolean logTrace) {
		GameInfo gameInfo = round.getGameInfo();
		double pointForWin = CachedGames.getGameProperties(round.getGameInfo().getGameId()).getPointsForWin();

		double balancedMyPoint = gameInfo.getMyPoint() * (gameInfo.getMyPoint() >= pointForWin ? (1.0 + mixedRoundHeuristicParam6Value) :
				(1.0 + mixedRoundHeuristicParam6Value * (gameInfo.getMyPoint() / pointForWin)));
		double balancedOpponentPoint = gameInfo.getOpponentPoint() * (gameInfo.getOpponentPoint() >= pointForWin ? (1.0 + mixedRoundHeuristicParam6Value) :
				(1.0 + mixedRoundHeuristicParam6Value * (gameInfo.getOpponentPoint() / pointForWin)));
		double pointDiff = balancedMyPoint - balancedOpponentPoint;

		double tilesDiff = mixedRoundHeuristicParam1Value * (round.getTableInfo().getOpponentTilesCount() - round.getMyTiles().size());

		TilesStatistic tilesStatistic = getTilesStatistic(round);
		double myMovesCount = countPossibleMoves(round.getMyTiles(), tilesStatistic);
		if (gameInfo.getMyPoint() - gameInfo.getOpponentPoint() >= 60) {
			myMovesCount *= mixedRoundHeuristicParam7Value;
		} else if (gameInfo.getMyPoint() - gameInfo.getOpponentPoint() >= 30) {
			myMovesCount *= mixedRoundHeuristicParam7Value / 2;
		}

		double opponentMovesCount = countPossibleMoves(round.getOpponentTiles(), round.getTableInfo().getOpponentTilesCount(), tilesStatistic);
		if (gameInfo.getOpponentPoint() - gameInfo.getMyPoint() >= 60) {
			opponentMovesCount *= mixedRoundHeuristicParam7Value;
		} else if (gameInfo.getOpponentPoint() - gameInfo.getMyPoint() >= 30) {
			opponentMovesCount *= mixedRoundHeuristicParam7Value / 2;
		}

		double movesDiff = mixedRoundHeuristicParam2Value * (myMovesCount / round.getMyTiles().size() - opponentMovesCount / round.getTableInfo().getOpponentTilesCount());

		return pointDiff + tilesDiff + movesDiff;
	}

	private double countPossibleMoves(Map<Tile, Double> opponentTiles, double opponentTilesCount, TilesStatistic tilesStatistic) {
		double result = 0.0;
		for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
			Tile tile = entry.getKey();
			double countForTile = 0.0;

			countForTile += tilesStatistic.getOnTable().get(tile.getLeft());
			countForTile += tilesStatistic.getForMe().get(tile.getLeft()) * mixedRoundHeuristicParam4Value;
			double opponentTileNewValueLeft = 0;
			if (opponentTilesCount != entry.getValue()) {
				opponentTileNewValueLeft = (tilesStatistic.getForOpponent().get(tile.getLeft()) - entry.getValue()) / (opponentTilesCount - entry.getValue()) * (opponentTilesCount - 1);
			}
			countForTile += opponentTileNewValueLeft * mixedRoundHeuristicParam3Value;
			countForTile += (tilesStatistic.getForBazaar().get(tile.getLeft()) + (tilesStatistic.getForOpponent().get(tile.getLeft()) - opponentTileNewValueLeft) - 1)
					* mixedRoundHeuristicParam5Value;

			if (tile.getLeft() != tile.getRight()) {
				countForTile += tilesStatistic.getOnTable().get(tile.getRight());
				countForTile += tilesStatistic.getForMe().get(tile.getRight()) * mixedRoundHeuristicParam4Value;
				double opponentTileNewValueRight = 0;
				if (opponentTilesCount != entry.getValue()) {
					opponentTileNewValueRight = (tilesStatistic.getForOpponent().get(tile.getRight()) - entry.getValue()) / (opponentTilesCount - entry.getValue()) * (opponentTilesCount - 1);
				}
				countForTile += opponentTileNewValueRight * mixedRoundHeuristicParam3Value;
				countForTile += (tilesStatistic.getForBazaar().get(tile.getRight()) + (tilesStatistic.getForOpponent().get(tile.getRight()) - opponentTileNewValueRight) - 1)
						* mixedRoundHeuristicParam5Value;
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
			countForTile += (tilesStatistic.getForMe().get(tile.getLeft()) - 1) * mixedRoundHeuristicParam3Value;
			countForTile += tilesStatistic.getForOpponent().get(tile.getLeft()) * mixedRoundHeuristicParam4Value;
			countForTile += tilesStatistic.getForBazaar().get(tile.getLeft()) * mixedRoundHeuristicParam5Value;
			if (tile.getLeft() != tile.getRight()) {
				countForTile += tilesStatistic.getOnTable().get(tile.getRight());
				countForTile += (tilesStatistic.getForMe().get(tile.getRight()) - 1) * mixedRoundHeuristicParam3Value;
				countForTile += tilesStatistic.getForOpponent().get(tile.getRight()) * mixedRoundHeuristicParam4Value;
				countForTile += tilesStatistic.getForBazaar().get(tile.getRight()) * mixedRoundHeuristicParam5Value;
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
