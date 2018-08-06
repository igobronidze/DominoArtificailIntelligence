package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;

public class PlayForMeProcessorVirtual extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) throws DAIException {
		boolean firstMove = round.getTableInfo().getLeft() == null;

		round.getTableInfo().getRoundBlockingInfo().setOmitMe(false);
		if (move.getRight() != move.getLeft()) {
			round.getTableInfo().getRoundBlockingInfo().setLastNotTwinPlayedTileMy(true);
		}
		int gameId = round.getGameInfo().getGameId();

		// Not played twins case
		if (round.getTableInfo().isFirstRound() && round.getTableInfo().getLeft() == null) {
			double sum = GameOperations.makeTwinTilesAsBazaarAndReturnProbabilitiesSum(round.getOpponentTiles(), (move.getLeft() == move.getRight() ? move.getLeft() : -1));
			ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		// Play tile
		Tile tmpTile = new Tile(move.getLeft(), move.getRight());
		round.getMyTiles().remove(tmpTile);
		GameOperations.playTile(round, move);

		round.getGameInfo().setMyPoint(round.getGameInfo().getMyPoint() + GameOperations.countScore(round));
		round.getTableInfo().setMyMove(false);

		if (round.getMyTiles().size() == 0) {
			round = GameOperations.finishedLastAndGetNewRound(round, true, GameOperations.countLeftTiles(round, false, true), true);
		}

		return round;
	}
}
