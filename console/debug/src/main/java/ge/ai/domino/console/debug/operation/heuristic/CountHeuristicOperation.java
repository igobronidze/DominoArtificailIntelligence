package ge.ai.domino.console.debug.operation.heuristic;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Scanner;

public class CountHeuristicOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(CountHeuristicOperation.class);

	@Override
	public void process(Scanner scanner) throws DAIException {
		Map<String, Double> result = GameDebuggerHelper.heuristicManager.getHeuristics(GameDebuggerHelper.round);
		for (Map.Entry<String, Double> entry : result.entrySet()) {
			logger.info(entry.getKey() + ": " + entry.getValue());
		}
		logger.info("Heuristics counted successfully");
	}
}
