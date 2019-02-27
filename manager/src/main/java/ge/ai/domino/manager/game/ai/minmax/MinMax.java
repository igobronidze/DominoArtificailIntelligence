package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.AiSolver;
import ge.ai.domino.manager.game.ai.heuristic.RoundHeuristic;
import ge.ai.domino.manager.game.ai.heuristic.factory.RoundHeuristicFactory;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.game.move.AddForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.AddForOpponentProcessorVirtual;
import ge.ai.domino.manager.game.move.MoveProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessorVirtual;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessorVirtual;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MinMax implements AiSolver {

	private final Logger logger = Logger.getLogger(super.getClass());

	private final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	private static final SysParam roundHeuristicType = new SysParam("roundHeuristicType", "POINT_DIFF_ROUND_HEURISTIC");

	private NodeRound notValidRound;

	private String errorMsg;

	private String errorMsgKey;

	protected final MoveProcessor playForMeProcessorVirtual = new PlayForMeProcessorVirtual();

	protected final MoveProcessor playForOpponentProcessorVirtual = new PlayForOpponentProcessorVirtual();

	protected final MoveProcessor addForMeProcessorVirtual = new AddForMeProcessorVirtual();

	protected final MoveProcessor addForOpponentProcessorVirtual = new AddForOpponentProcessorVirtual();

	protected final SystemParameterManager systemParameterManager = new SystemParameterManager();

	protected static final SysParam bestMoveAutoPlay = new SysParam("bestMoveAutoPlay", "true");

	protected final SysParam minMaxForCachedNodeRoundIterationRate = new SysParam("minMaxForCachedNodeRoundIterationRate", "4");

	protected final RoundHeuristic roundHeuristic = RoundHeuristicFactory.getRoundHeuristic(systemParameterManager.getStringParameterValue(roundHeuristicType));

	protected int processCount = 1;

	public void setProcessCount(int processCount) {
		this.processCount = processCount;
	}

	public abstract void minMaxForCachedNodeRound(Round round) throws DAIException;

	public AiPredictionsWrapper minMaxForNodeRound(NodeRound nodeRound) throws DAIException {
		return null;
	}

	public abstract String getType();

	public void setRoundHeuristicParams(List<Double> params) {
		roundHeuristic.setParams(params);
	}

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
					break;
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
}
