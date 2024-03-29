package ge.ai.domino.manager.game.move;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.helper.play.GameOperations;
import ge.ai.domino.manager.game.helper.play.ProbabilitiesDistributor;

public class AddForOpponentProcessorVirtual extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) {
		int gameId = round.getGameInfo().getGameId();
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
				round = GameOperations.blockRound(round, CachedGames.getOpponentLeftTilesCount(gameId), true);
			} else {
				round.getTableInfo().setMyMove(true);
			}
			return round;
		} else {
			tableInfo.setTilesFromBazaar(tableInfo.getTilesFromBazaar() + 1);

			tableInfo.setOpponentTilesCount(tableInfo.getOpponentTilesCount() + 1);
			tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
			ProbabilitiesDistributor.updateProbabilitiesForLastPickedTiles(round, false, true);

			return round;
		}
	}
}
