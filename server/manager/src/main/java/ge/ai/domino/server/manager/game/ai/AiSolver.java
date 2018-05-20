package ge.ai.domino.server.manager.game.ai;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;

import java.util.List;

public interface AiSolver {

	AiPredictionsWrapper solve(Round round) throws DAIException;
}
