package ge.ai.domino.manager.game.ai.heuristic.statistic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.Map;

public class RoundStatisticProcessor {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam mixedRoundHeuristicParam3 = new SysParam("mixedRoundHeuristicParam3", "0.4");

	private final SysParam mixedRoundHeuristicParam4 = new SysParam("mixedRoundHeuristicParam4", "0.2");

	private final SysParam mixedRoundHeuristicParam5 = new SysParam("mixedRoundHeuristicParam5", "0.1");

	private final double mixedRoundHeuristicParam3Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam3);

	private final double mixedRoundHeuristicParam4Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam4);

	private final double mixedRoundHeuristicParam5Value = sysParamManager.getDoubleParameterValue(mixedRoundHeuristicParam5);

	private Round round;

	private RoundTilesStatistic roundTilesStatistic;

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
			case OPPONENT_TILES_INFO_ACCURACY:
				return 0; //TODO[IG]
			case OPEN_TILES_SUM:
				return 0; //TODO[IG]
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

	private double countMyPossibleMoves(RoundTilesStatistic tilesStatistic) {
		double result = 0.0;
		for (Tile tile : round.getMyTiles()) {
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
}
