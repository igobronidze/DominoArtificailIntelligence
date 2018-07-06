package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.logging.GameLoggingProcessor;

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
			double sum = GameOperations.makeTilesAsBazaarAndReturnProbabilitiesSum(round);
			ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		if (tableInfo.getBazaarTilesCount() == 2) { // If omit
			tableInfo.getRoundBlockingInfo().setOmitOpponent(true);
			if (tableInfo.getRoundBlockingInfo().isOmitMe()) {
				round = GameOperations.blockRound(round, CachedGames.getOpponentLeftTilesCount(gameId), virtual);
			} else {
				round.getTableInfo().setMyMove(true);
				if (!virtual) {
					if (tableInfo.getTilesFromBazaar() > 0) {
						ProbabilitiesDistributor.updateProbabilitiesForLastPickedTiles(round, false, false);
					}
					round.setAiPredictions(MinMaxFactory.getMinMax().solve(round));
				}

			}
			GameLoggingProcessor.logInfoAboutMove("Opponent omitted, gameId[" + gameId + "]", virtual);
			GameLoggingProcessor.logRoundFullInfo(round, virtual);
			return round;
		} else {
			tableInfo.setTilesFromBazaar(tableInfo.getTilesFromBazaar() + 1);

			tableInfo.setOpponentTilesCount(tableInfo.getOpponentTilesCount() + 1);
			tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
			if (virtual) {
				ProbabilitiesDistributor.updateProbabilitiesForLastPickedTiles(round, false, true);
			}

			GameLoggingProcessor.logInfoAboutMove("Added tile for opponent, gameId[" + gameId + "]", virtual);
			GameLoggingProcessor.logRoundFullInfo(round, virtual);
			return round;
		}
	}
}