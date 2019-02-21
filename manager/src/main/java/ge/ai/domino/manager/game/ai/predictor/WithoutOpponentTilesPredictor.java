package ge.ai.domino.manager.game.ai.predictor;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;

public class WithoutOpponentTilesPredictor implements OpponentTilesPredictor {

    @Override
    public void predict(Round round, Round roundBeforePlay, Move move) throws DAIException {

    }
}
