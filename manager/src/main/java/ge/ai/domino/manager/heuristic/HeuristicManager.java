package ge.ai.domino.manager.heuristic;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicFactory;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristicType;
import ge.ai.domino.manager.game.logging.RoundLogger;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HeuristicManager {

	private final Logger logger = Logger.getLogger(HeuristicManager.class);

	public Map<String, Double> getHeuristics(Round round) {
		logger.info("Start getHeuristics method");
		RoundLogger.logRoundFullInfo(round);

		Map<String, Double> result = new HashMap<>();
		for (RoundHeuristicType type : RoundHeuristicType.values()) {
			RoundHeuristic roundHeuristic = RoundHeuristicFactory.getRoundHeuristic(type.name());
			result.put(type.name(), roundHeuristic.getHeuristic(round, true));
		}

		logger.info("Finished getHeuristics method");
		return result;
	}
}
