package ge.ai.domino.manager.heuristic;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicFactory;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicType;

import java.util.HashMap;
import java.util.Map;

public class HeuristicManager {

	public Map<String, Double> getHeuristics(int gameId) {
		Round round = CachedGames.getCurrentRound(gameId, true);

		Map<String, Double> result = new HashMap<>();
		for (RoundHeuristicType type : RoundHeuristicType.values()) {
			RoundHeuristic roundHeuristic = RoundHeuristicFactory.getRoundHeuristic(type.name());
			result.put(type.name(), roundHeuristic.getHeuristic(round));
		}
		return result;
	}
}
