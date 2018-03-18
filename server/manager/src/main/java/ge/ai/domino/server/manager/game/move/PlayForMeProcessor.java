package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.server.manager.game.helper.GameOperations;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.validator.OpponentTilesValidator;

public class PlayForMeProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move, boolean virtual) throws DAIException {
		// Logging
		GameLoggingProcessor.logInfoAboutMove(virtual ? "<<<<<<<Virtual Mode>>>>>>>" : "<<<<<<<Real Mode<<<<<<<", virtual);
		MoveDirection direction = move.getDirection();

		round.getTableInfo().setOmittedMe(false);
		int gameId = round.getGameInfo().getGameId();
		GameLoggingProcessor.logInfoAboutMove("Start playForMe method for tile [" + move.getLeft() + "-" + move.getRight() + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);

		// Not played twins case
		if (round.getTableInfo().isFirstRound() && round.getTableInfo().getLeft() == null) {
			float sum = GameOperations.makeTwinTilesAsBazaarAndReturnProbabilitiesSum(round.getOpponentTiles(), (move.getLeft() == move.getRight() ? move.getLeft() : -1));
			GameOperations.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		// Play tile
		Tile tmpTile = new Tile(move.getLeft(), move.getRight());
		round.getMyTiles().remove(tmpTile);
		GameOperations.playTile(round, move);

		GameOperations.addLeftTiles(round.getGameInfo(), GameOperations.countScore(round), true, gameId, virtual);
		round.getTableInfo().setMyMove(false);

		if (round.getMyTiles().size() == 0) {
			round.getTableInfo().setNeedToAddLeftTiles(true);
		}

		GameLoggingProcessor.logInfoAboutMove("Played tile for me, gameId[" + gameId + "]", virtual);
		GameLoggingProcessor.logRoundFullInfo(round, virtual);

		OpponentTilesValidator.validateOpponentTiles(round, 0, "playForMe" + move);
		return round;
	}
}
