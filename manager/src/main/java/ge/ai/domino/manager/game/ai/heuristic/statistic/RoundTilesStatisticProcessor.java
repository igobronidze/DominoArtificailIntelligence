package ge.ai.domino.manager.game.ai.heuristic.statistic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;

import java.util.HashMap;
import java.util.Map;

public class RoundTilesStatisticProcessor {

	public static RoundTilesStatistic getTilesStatistic(Round round) {
		RoundTilesStatistic tilesStatistic = new RoundTilesStatistic();

		setOnTable(round, tilesStatistic);
		setForMe(round, tilesStatistic);
		setForOpponentAndBazaar(round, tilesStatistic);

		return tilesStatistic;
	}

	private static void setOnTable(Round round, RoundTilesStatistic tilesStatistic) {
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

	private static void setForMe(Round round, RoundTilesStatistic tilesStatistic) {
		Map<Integer, Double> forMe = getInitMap();
		for (Tile tile : round.getMyTiles()) {
			forMe.put(tile.getLeft(), forMe.get(tile.getLeft()) + 1);
			if (tile.getLeft() != tile.getRight()) {
				forMe.put(tile.getRight(), forMe.get(tile.getRight()) + 1);
			}
		}
		tilesStatistic.setForMe(forMe);
	}

	private static void setForOpponentAndBazaar(Round round, RoundTilesStatistic tilesStatistic) {
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

	private static Map<Integer, Double> getInitMap() {
		Map<Integer, Double> map = new HashMap<>();
		for (int i = 0; i < 7; i++) {
			map.put(i, 0.0);
		}
		return map;
	}
}
