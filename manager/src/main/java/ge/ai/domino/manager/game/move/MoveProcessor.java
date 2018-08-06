package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;
import org.apache.log4j.Logger;

public abstract class MoveProcessor {

	protected static final Logger logger = Logger.getLogger(MoveProcessor.class);

	public abstract Round move(Round round, Move move) throws DAIException;
}
