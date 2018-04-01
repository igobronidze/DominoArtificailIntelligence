package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.server.manager.game.validator.OpponentTilesValidator;

public abstract class MoveProcessor {

	protected OpponentTilesValidator opponentTilesValidator;

	public MoveProcessor(OpponentTilesValidator opponentTilesValidator) {
		this.opponentTilesValidator = opponentTilesValidator;
	}

	public abstract Round move(Round round, Move move, boolean virtual) throws DAIException;
}
