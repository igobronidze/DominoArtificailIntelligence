package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.helper.play.GameOperations;

public class PlayForMeProcessorVirtual extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) {
		round.getTableInfo().getRoundBlockingInfo().setOmitMe(false);
		if (move.getRight() != move.getLeft()) {
			round.getTableInfo().getRoundBlockingInfo().setLastNotTwinPlayedTileMy(true);
		}

		// Play tile
		GameOperations.playTile(round, move, true);

		round.getGameInfo().setMyPoint(round.getGameInfo().getMyPoint() + GameOperations.countScore(round));
		round.getTableInfo().setMyMove(false);

		if (round.getMyTiles().size() == 0) {
			round = GameOperations.finishedLastAndGetNewRound(round, true, GameOperations.countLeftTiles(round, false, true), true);
		} else if (GameOperations.isRoundBlocked(round)) {
			logger.info("Round is blocked");
			round = GameOperations.blockRound(round, GameOperations.countLeftTiles(round, false, true), true);
		}

		return round;
	}
}
