package ge.ai.domino.manager.game.validator;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.helper.ComparisonHelper;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class OpponentTilesValidator {

	private static final Logger logger = Logger.getLogger(OpponentTilesValidator.class);

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	private static final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	/**
	 * Check if tiles probabilities is correct
	 * All probability must be in closed interval [0-1] and sum must be equal of tiles count
	 *
	 * @param round   Round
	 * @param addProb Some probabilities may not be added, so we provide this fact(for example whe opponent get a new tile, it's probability is not added
	 *                while will not play or omit)
	 */
	public static String validateOpponentTiles(Round round, double addProb, String msg) {
		if (sysParamManager.getBooleanParameterValue(checkOpponentProbabilities)) {
			double sum = 0.0;
			for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
				double prob = entry.getValue();
				int left = entry.getKey().getLeft();
				int right = entry.getKey().getRight();
				if (prob > 1.0) {
					logger.warn("Opponent tile probability is more than one, tile[" + left + "-" + right + "] method[" + msg + "]");
					RoundLogger.logRoundFullInfo(round);
					return "opponentTileProbabilityIsMoreThanOne";
				} else if (prob < 0.0) {
					logger.warn("Opponent tile probability is less than zero, tile[" + left + "-" + right + "] method[" + msg + "]");
					RoundLogger.logRoundFullInfo(round);
					return "opponentTileProbabilityIsLessThanZero";
				}
				sum += prob;
			}
			if (!ComparisonHelper.equal(sum + addProb, round.getTableInfo().getOpponentTilesCount())) {
				logger.warn("Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + "  addProb:" + addProb + ", method[" + msg + "]");
				RoundLogger.logRoundFullInfo(round);
				return "probabilitiesSumIsNoEqualToOpponentTilesCount";
			}
		}
		return null;
	}
}
