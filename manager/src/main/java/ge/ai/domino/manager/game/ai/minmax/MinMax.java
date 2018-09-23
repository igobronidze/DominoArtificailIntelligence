package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.AiSolver;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class MinMax implements AiSolver {

	private final Logger logger = Logger.getLogger(super.getClass());

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	private final SysParam logAboutRoundHeuristic = new SysParam("logAboutRoundHeuristic", "true");

	protected final SysParam useMinMaxPredictor = new SysParam("useMinMaxPredictor", "false");

	protected static final SysParam bestMoveAutoPlay = new SysParam("bestMoveAutoPlay", "true");

	protected static final SysParam roundHeuristicType = new SysParam("roundHeuristicType", "POINT_DIFF_ROUND_HEURISTIC");

	private NodeRound notValidRound;

	private String errorMsg;

	private String errorMsgKey;

	protected boolean multithreadingMinMax;

	protected int threadCount = 1;

	public void setMultithreadingMinMax(boolean multithreadingMinMax) {
		this.multithreadingMinMax = multithreadingMinMax;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public abstract void minMaxForCachedNodeRound(Round round) throws DAIException;

	public abstract String getType();

	protected boolean isNewRound(Round round) {
		return round.getMyTiles().size() == 0 && round.getTableInfo().getOpponentTilesCount() == 7 && round.getTableInfo().getBazaarTilesCount() == 21;
	}

	protected void validateOpponentTiles(NodeRound nodeRound, String msg) {
		if (systemParameterManager.getBooleanParameterValue(checkOpponentProbabilities)) {
			double sum = 0.0;
			Round round = nodeRound.getRound();
			for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
				double prob = entry.getValue();
				int left = entry.getKey().getLeft();
				int right = entry.getKey().getRight();
				if (prob > 1.0) {
					notValidRound = nodeRound;
					errorMsg = "Opponent tile probability is more than one, tile[" + left + "-" + right + "] method[" + msg + "]";
					errorMsgKey = "opponentTileProbabilityIsMoreThanOne";
					break;
				} else if (prob < 0.0) {
					notValidRound = nodeRound;
					errorMsg = "Opponent tile probability is less than zero, tile[" + left + "-" + right + "] method[" + msg + "]";
					errorMsgKey = "opponentTileProbabilityIsLessThanZero";
				}
				sum += prob;
			}
			if (!ComparisonHelper.equal(sum, round.getTableInfo().getOpponentTilesCount())) {
				notValidRound = nodeRound;
				errorMsg = "Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + ", method[" + msg + "]";
				errorMsgKey = "probabilitiesSumIsNoEqualToOpponentTilesCount";
			}
		}
	}

	protected String applyValidation() {
		if (notValidRound != null) {
			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			logger.warn(errorMsg);
			logger.info("Rounds full info");

			List<NodeRound> parentRounds = new ArrayList<>();
			while (notValidRound.getParent() != null) {
				parentRounds.add(notValidRound);
				notValidRound = notValidRound.getParent();
			}
			RoundLogger.logRoundFullInfo(notValidRound.getRound());
			for (int i = parentRounds.size() - 1; i >= 0; i--) {
				notValidRound = parentRounds.get(i);
				logger.info("ID: " + notValidRound.getId() + ", Height: " + notValidRound.getTreeHeight());
				logger.info("Play move with probability[" + notValidRound.getLastPlayedProbability() + "], move[" + notValidRound.getLastPlayedMove() + "]");
				RoundLogger.logRoundFullInfo(notValidRound.getRound());
			}

			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			return errorMsgKey;
		}
		return null;
	}

	protected double getHeuristic(Round round, RoundHeuristic roundHeuristic) {
		Random random = new Random();
		double r = random.nextDouble();
		boolean logHeuristicInfo = (r < (1.0 / 1000)) && systemParameterManager.getBooleanParameterValue(logAboutRoundHeuristic);
		if (logHeuristicInfo) {
			logger.info("******************************RoundHeuristic(" + roundHeuristic.getClass().getSimpleName() + ")******************************");
			RoundLogger.logRoundFullInfo(round);
		}

		double heuristic = roundHeuristic.getHeuristic(round, logHeuristicInfo);

		if (logHeuristicInfo) {
			logger.info("Heuristic: " + heuristic);
			logger.info("************************************************************");
		}

		return heuristic;
	}
}
