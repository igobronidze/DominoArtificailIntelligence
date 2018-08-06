package ge.ai.domino.manager.game.move;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.Map;

public class AddForMeProcessor extends MoveProcessor {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam minMaxOnFirstTile = new SysParam("minMaxOnFirstTile", "false");

	@Override
	public Round move(Round round, Move move) throws DAIException {
		int gameId = round.getGameInfo().getGameId();
		logger.info("Start addTileForMe method for tile [" + move.getLeft() + "-" + move.getRight() + "], gameId[" + gameId + "]");
		TableInfo tableInfo = round.getTableInfo();

		// If omit -> a) If opponent also has omitted finish b) Make opponent try
		if (tableInfo.getBazaarTilesCount() == 2) {
			tableInfo.getRoundBlockingInfo().setOmitMe(true);
			if (tableInfo.getRoundBlockingInfo().isOmitOpponent()) {
				round = GameOperations.blockRound(round, CachedGames.getOpponentLeftTilesCount(gameId), false);
			} else {
				round.getTableInfo().setMyMove(false);
				if (new MinMaxPredictor().usePredictor()) {
					MinMaxFactory.getMinMax().minMaxForCachedNodeRound(round);
				}
			}
			logger.info("I omitted, gameId[" + gameId + "]");
			RoundLogger.logRoundFullInfo(round);
			return round;
		}

		// Add for me
		Tile tile = new Tile(move.getLeft(), move.getRight());
		round.getMyTiles().add(tile);

		// Delete for opponent and produce probability
		Map<Tile, Double> opponentTiles = round.getOpponentTiles();
		double prob = opponentTiles.get(tile);
		opponentTiles.remove(tile);
		ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTiles, prob);

		tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);

		// Execute MinMaxDFS
		if (tableInfo.getLeft() == null && round.getMyTiles().size() == 7) {
			round.getTableInfo().setMyMove(!CachedGames.isOpponentNextRoundBeginner(gameId));
			if (sysParamManager.getBooleanParameterValue(minMaxOnFirstTile) && !round.getTableInfo().isFirstRound() && round.getTableInfo().isMyMove()) {
				round.setAiPredictions(MinMaxFactory.getMinMax().solve(round));
			}
		} else if (round.getTableInfo().getLeft() != null) {
			round.setAiPredictions(MinMaxFactory.getMinMax().solve(round));
		}

		logger.info("Added tile for me, gameId[" + gameId + "]");
		RoundLogger.logRoundFullInfo(round);

		return round;
	}
}
