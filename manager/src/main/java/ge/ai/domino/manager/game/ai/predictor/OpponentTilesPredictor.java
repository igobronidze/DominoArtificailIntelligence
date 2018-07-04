package ge.ai.domino.manager.game.ai.predictor;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;

public interface OpponentTilesPredictor {

	void predict(Round round, Move move) throws DAIException;

	boolean usePredictor();
}
