package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.server.manager.game.minmax.MinMax;

public abstract class MoveProcessor {

	MinMax minMax = new MinMax();

	public abstract Round move(Round round, Move move, boolean virtual) throws DAIException;
}
