package ge.ai.domino.console.debug.operation.heuristic;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.manager.heuristic.HeuristicManager;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Scanner;

public class CountHeuristicOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(CountHeuristicOperation.class);

	private static final HeuristicManager heuristicManager = new HeuristicManager();

	@Override
	public void process(Scanner scanner) {
		Map<String, Double> result = heuristicManager.getHeuristics(GameDebuggerHelper.round);
		for (Map.Entry<String, Double> entry : result.entrySet()) {
			logger.info(entry.getKey() + ": " + entry.getValue());
		}
		logger.info("Heuristics counted successfully");
	}
}
