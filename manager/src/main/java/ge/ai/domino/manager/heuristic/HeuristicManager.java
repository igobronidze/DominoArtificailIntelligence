package ge.ai.domino.manager.heuristic;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.heuristic.Heuristic;
import ge.ai.domino.domain.heuristic.RoundHeuristicType;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.factory.RoundHeuristicFactory;
import ge.ai.domino.manager.game.ai.minmax.MinMax;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeuristicManager {

	private final Logger logger = Logger.getLogger(HeuristicManager.class);

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private static final SysParam roundHeuristicType = new SysParam("roundHeuristicType", "POINT_DIFF_ROUND_HEURISTIC");

	public Double getHeuristic(Round round) {
		RoundHeuristic roundHeuristic = RoundHeuristicFactory.getRoundHeuristic(systemParameterManager.getStringParameterValue(roundHeuristicType));
		return roundHeuristic.getHeuristic(round);
	}

	public Map<String, Double> getHeuristics(Round round) {
		logger.info("Start getHeuristics method");
		RoundLogger.logRoundFullInfo(round);

		Map<String, Double> result = new HashMap<>();
		for (RoundHeuristicType type : RoundHeuristicType.values()) {
			RoundHeuristic roundHeuristic = RoundHeuristicFactory.getRoundHeuristic(type.name());
			result.put(type.name(), roundHeuristic.getHeuristic(round));
		}

		logger.info("Finished getHeuristics method");
		return result;
	}

	public Heuristic getHeuristic(Round round, RoundHeuristicType roundHeuristicType, List<Double> params) throws DAIException {
		Heuristic heuristic = new Heuristic();
		heuristic.setType(roundHeuristicType);
		heuristic.setRound(round);

		RoundHeuristic roundHeuristic = RoundHeuristicFactory.getRoundHeuristic(roundHeuristicType.name());
		heuristic.setValue(roundHeuristic.getHeuristic(round));

		MinMax minMax = MinMaxFactory.getMinMax(true);
		minMax.setRoundHeuristicParams(params);
		AiPredictionsWrapper aiPredictionsWrapper = minMax.solve(round);
		for (AiPrediction aiPrediction : aiPredictionsWrapper.getAiPredictions()) {
			if (aiPrediction.getMoveProbability() == 1.0) {
				heuristic.setAiValue(aiPrediction.getHeuristicValue());
				break;
			}
		}

		return heuristic;
	}
}
