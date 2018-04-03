package ge.ai.domino.server.manager.game.ai;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.AiPrediction;
import ge.ai.domino.domain.game.Round;

import java.util.List;

public interface AiSolver {

	List<AiPrediction> solve(Round round) throws DAIException;
}
