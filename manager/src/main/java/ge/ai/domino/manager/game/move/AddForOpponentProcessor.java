package ge.ai.domino.manager.game.move;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.helper.play.GameOperations;
import ge.ai.domino.manager.game.helper.play.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.logging.RoundLogger;

public class AddForOpponentProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) throws DAIException {
		int gameId = round.getGameInfo().getGameId();
		logger.info("Start addTileForOpponent method, gameId[" + gameId + "]");
		TableInfo tableInfo = round.getTableInfo();

		// If it's first time, make possible tiles as bazaar and distribute their probabilities for other
		if (tableInfo.getTilesFromBazaar() == 0) {
			double sum = GameOperations.makeTilesAsBazaarAndReturnProbabilitiesSum(round);
			if (sum != 0) {
				ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
			}
		}

		if (tableInfo.getBazaarTilesCount() == 2) { // If omit
			tableInfo.getRoundBlockingInfo().setOmitOpponent(true);
			if (tableInfo.getRoundBlockingInfo().isOmitMe()) {
				round = GameOperations.blockRound(round, CachedGames.getOpponentLeftTilesCount(gameId), false);
			} else {
				round.getTableInfo().setMyMove(true);
				if (tableInfo.getTilesFromBazaar() > 0) {
					ProbabilitiesDistributor.updateProbabilitiesForLastPickedTiles(round, false, false);
				}
				round.setAiPredictions(MinMaxFactory.getMinMax(true).solve(round));
			}

			logger.info("Opponent omitted, gameId[" + gameId + "]");
			RoundLogger.logRoundFullInfo(round);
			return round;
		} else {
			tableInfo.setTilesFromBazaar(tableInfo.getTilesFromBazaar() + 1);

			tableInfo.setOpponentTilesCount(tableInfo.getOpponentTilesCount() + 1);
			tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);

			logger.info("Added tile for opponent, gameId[" + gameId + "]");
			RoundLogger.logRoundFullInfo(round);
			return round;
		}
	}
}
