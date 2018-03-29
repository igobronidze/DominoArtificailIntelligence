package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.helper.GameOperations;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.minmax.MinMax;
import ge.ai.domino.server.manager.game.validator.OpponentTilesValidator;

public class AddForOpponentProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move, boolean virtual) throws DAIException {
		if (virtual) {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
		} else {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
		}
		int gameId = round.getGameInfo().getGameId();
		GameLoggingProcessor.logInfoAboutMove("Start addTileForOpponent method, gameId[" + gameId + "]", virtual);
		TableInfo tableInfo = round.getTableInfo();

		// If it's first time, make possible tiles as bazaar and distribute their probabilities for other
		if (tableInfo.getTilesFromBazaar() == 0 && !tableInfo.getRoundBlockingInfo().isOmitOpponent()) {
			float sum = GameOperations.makeTilesAsBazaarAndReturnProbabilitiesSum(round);
			GameOperations.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		if (tableInfo.getBazaarTilesCount() == 2) { // If omit
			tableInfo.getRoundBlockingInfo().setOmitOpponent(true);
			if (tableInfo.getRoundBlockingInfo().isOmitMe()) {
				round = GameOperations.blockRound(round, CachedGames.getOpponentLeftTilesCount(gameId), virtual);
			} else {
				if (tableInfo.getTilesFromBazaar() > 0) {
					GameOperations.updateProbabilitiesForLastPickedTiles(round, false);
				}

				round.getTableInfo().setMyMove(true);
				if (!virtual) {
//					round.setAiPrediction(new MinMax().minMax(round)); TODO
				}
			}
			GameLoggingProcessor.logRoundFullInfo(round, virtual);
			return round;
		} else {
			tableInfo.setTilesFromBazaar(tableInfo.getTilesFromBazaar() + 1);

			tableInfo.setOpponentTilesCount(tableInfo.getOpponentTilesCount() + 1);
			tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);

			GameLoggingProcessor.logInfoAboutMove("Added tile for opponent, gameId[" + gameId + "]", virtual);
			GameLoggingProcessor.logRoundFullInfo(round, virtual);

			OpponentTilesValidator.validateOpponentTiles(round, round.getTableInfo().getTilesFromBazaar(), "addTileForOpponent");
			return round;
		}
	}
}
