package ge.ai.domino.manager.game.ai.heuristic.statistic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoundStatisticProcessor {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam roundStatisticProcessorParam1 = new SysParam("roundStatisticProcessorParam1", "0.4");

	private final SysParam roundStatisticProcessorParam2 = new SysParam("roundStatisticProcessorParam2", "0.2");

	private final SysParam roundStatisticProcessorParam3 = new SysParam("roundStatisticProcessorParam3", "0.1");

	private double roundStatisticProcessorParam1Value = sysParamManager.getDoubleParameterValue(roundStatisticProcessorParam1);

	private double roundStatisticProcessorParam2Value = sysParamManager.getDoubleParameterValue(roundStatisticProcessorParam2);

	private double roundStatisticProcessorParam3Value = sysParamManager.getDoubleParameterValue(roundStatisticProcessorParam3);

	private Round round;

	private RoundTilesStatistic roundTilesStatistic;

	public List<String> getParamNames() {
		List<String> names = new ArrayList<>();
		names.add(roundStatisticProcessorParam1.getKey());
		names.add(roundStatisticProcessorParam2.getKey());
		names.add(roundStatisticProcessorParam3.getKey());
		return names;
	}

	public void setParams(List<Double> params) {
		roundStatisticProcessorParam1Value = params.get(0);
		roundStatisticProcessorParam2Value = params.get(1);
		roundStatisticProcessorParam3Value = params.get(2);
	}

	public void replaceRound(Round round) {
		this.round = round;
		this.roundTilesStatistic = null;
	}

	public double getStatistic(RoundStatisticType type) {
		switch (type) {
			case MY_POINT:
				return round.getGameInfo().getMyPoint();
			case OPPONENT_POINT:
				return round.getGameInfo().getOpponentPoint();
			case MY_TILES_COUNT:
				return round.getMyTiles().size();
			case OPPONENT_TILES_COUNT:
				return round.getTableInfo().getOpponentTilesCount();
			case PLAY_TURN:
				return round.getTableInfo().isMyMove() ? 1 : -1;
			case MY_PLAY_OPTIONS_COUNT:
				if (roundTilesStatistic == null) {
					roundTilesStatistic = RoundTilesStatisticProcessor.getTilesStatistic(round);
				}
				return countMyPossibleMoves(roundTilesStatistic);
			case OPPONENT_PLAY_OPTIONS_COUNT:
				if (roundTilesStatistic == null) {
					roundTilesStatistic = RoundTilesStatisticProcessor.getTilesStatistic(round);
				}
				return countOpponentPossibleMoves(roundTilesStatistic);
			case OPEN_TILES_SUM:
				return getOpenTilesSum();
			default:
				return 0;
		}
	}

	private double countOpponentPossibleMoves(RoundTilesStatistic tilesStatistic) {
		double opponentTilesCount = round.getTableInfo().getOpponentTilesCount();
		double result = 0.0;

		for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
			Tile tile = entry.getKey();
			double countForTile = 0.0;

			countForTile += tilesStatistic.getOnTable().get(tile.getLeft());
			countForTile += tilesStatistic.getForMe().get(tile.getLeft()) * roundStatisticProcessorParam2Value;
			double opponentTileNewValueLeft = 0;
			if (opponentTilesCount != entry.getValue()) {
				opponentTileNewValueLeft = (tilesStatistic.getForOpponent().get(tile.getLeft()) - entry.getValue()) / (opponentTilesCount - entry.getValue()) * (opponentTilesCount - 1);
			}
			countForTile += opponentTileNewValueLeft * roundStatisticProcessorParam1Value;
			countForTile += (tilesStatistic.getForBazaar().get(tile.getLeft()) + (tilesStatistic.getForOpponent().get(tile.getLeft()) - opponentTileNewValueLeft) - 1)
					* roundStatisticProcessorParam3Value;

			if (tile.getLeft() != tile.getRight()) {
				countForTile += tilesStatistic.getOnTable().get(tile.getRight());
				countForTile += tilesStatistic.getForMe().get(tile.getRight()) * roundStatisticProcessorParam2Value;
				double opponentTileNewValueRight = 0;
				if (opponentTilesCount != entry.getValue()) {
					opponentTileNewValueRight = (tilesStatistic.getForOpponent().get(tile.getRight()) - entry.getValue()) / (opponentTilesCount - entry.getValue()) * (opponentTilesCount - 1);
				}
				countForTile += opponentTileNewValueRight * roundStatisticProcessorParam1Value;
				countForTile += (tilesStatistic.getForBazaar().get(tile.getRight()) + (tilesStatistic.getForOpponent().get(tile.getRight()) - opponentTileNewValueRight) - 1)
						* roundStatisticProcessorParam3Value;
			}

			result += countForTile * entry.getValue();
		}
		return result;
	}

	private double countMyPossibleMoves(RoundTilesStatistic tilesStatistic) {
		double result = 0.0;
		for (Tile tile : round.getMyTiles()) {
			double countForTile = 0.0;
			countForTile += tilesStatistic.getOnTable().get(tile.getLeft());
			countForTile += (tilesStatistic.getForMe().get(tile.getLeft()) - 1) * roundStatisticProcessorParam1Value;
			countForTile += tilesStatistic.getForOpponent().get(tile.getLeft()) * roundStatisticProcessorParam2Value;
			countForTile += tilesStatistic.getForBazaar().get(tile.getLeft()) * roundStatisticProcessorParam3Value;
			if (tile.getLeft() != tile.getRight()) {
				countForTile += tilesStatistic.getOnTable().get(tile.getRight());
				countForTile += (tilesStatistic.getForMe().get(tile.getRight()) - 1) * roundStatisticProcessorParam1Value;
				countForTile += tilesStatistic.getForOpponent().get(tile.getRight()) * roundStatisticProcessorParam2Value;
				countForTile += tilesStatistic.getForBazaar().get(tile.getRight()) * roundStatisticProcessorParam3Value;
			}
			result += countForTile;
		}
		return result;
	}

	private double getOpenTilesSum() {
		TableInfo tableInfo = round.getTableInfo();

		double sum = 0.0;
		if (tableInfo.getLeft() != null && tableInfo.getLeft().isConsiderInSum()) {
			sum += tableInfo.getLeft().getOpenSide() * (tableInfo.getLeft().isTwin() ? 2 : 1);
		}
		if (tableInfo.getRight() != null && tableInfo.getRight().isConsiderInSum()) {
			sum += tableInfo.getRight().getOpenSide() * (tableInfo.getRight().isTwin() ? 2 : 1);
		}
		if (tableInfo.getTop() != null && tableInfo.getTop().isConsiderInSum()) {
			sum += tableInfo.getTop().getOpenSide() * (tableInfo.getTop().isTwin() ? 2 : 1);
		}
		if (tableInfo.getBottom() != null && tableInfo.getBottom().isConsiderInSum()) {
			sum += tableInfo.getBottom().getOpenSide() * (tableInfo.getBottom().isTwin() ? 2 : 1);
		}
		return sum;
	}
}
