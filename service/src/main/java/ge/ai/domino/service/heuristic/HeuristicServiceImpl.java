package ge.ai.domino.service.heuristic;

import ge.ai.domino.manager.heuristic.HeuristicManager;

import java.util.Map;

public class HeuristicServiceImpl implements HeuristicService {

	private HeuristicManager heuristicManager = new HeuristicManager();

	@Override
	public Map<String, Double> getHeuristics(int gameId) {
		return heuristicManager.getHeuristics(gameId);
	}
}
