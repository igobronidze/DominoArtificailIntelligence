package ge.ai.domino.service.heuristic;

import java.util.Map;

public interface HeuristicService {

	Map<String, Double> getHeuristics(int gameId);
}
