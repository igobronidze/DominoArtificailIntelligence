package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;

import java.util.Map;

public class AddForMeProcessorVirtual extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) throws DAIException {
		TableInfo tableInfo = round.getTableInfo();

		// If omit -> a) If opponent also has omitted finish b) Make opponent try
		if (tableInfo.getBazaarTilesCount() == 2) {
			tableInfo.getRoundBlockingInfo().setOmitMe(true);
			if (tableInfo.getRoundBlockingInfo().isOmitOpponent()) {
				round = GameOperations.blockRound(round, GameOperations.countLeftTiles(round, false, true), true);
			} else {
				round.getTableInfo().setMyMove(false);
			}
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

		return round;
	}
}
