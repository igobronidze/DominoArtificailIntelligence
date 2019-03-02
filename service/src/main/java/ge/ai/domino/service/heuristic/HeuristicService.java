package ge.ai.domino.service.heuristic;

import ge.ai.domino.domain.exception.DAIException;

import java.util.Map;

public interface HeuristicService {

	Map<String, Double> getHeuristics(int gameId) throws DAIException;
}
