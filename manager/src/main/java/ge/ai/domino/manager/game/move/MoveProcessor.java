package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;

public abstract class MoveProcessor {

	public abstract Round move(Round round, Move move, boolean virtual) throws DAIException;
}
