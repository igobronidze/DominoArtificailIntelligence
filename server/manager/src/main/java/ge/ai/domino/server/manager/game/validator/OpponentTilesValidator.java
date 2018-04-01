package ge.ai.domino.server.manager.game.validator;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.ParentRound;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.game.helper.ComparisonHelper;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpponentTilesValidator {

	private static final Logger logger = Logger.getLogger(OpponentTilesValidator.class);

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	private static final SysParam checkOpponentProbabilities = new SysParam("checkOpponentProbabilities", "false");

	// For only debugging
	private Round notValidRound;

	// For only debugging
	private String errorMsg;

	/**
	 * Check if tiles probabilities is correct
	 * All probability must be in closed interval [0-1] and sum must be equal of tiles count
	 * @param round Round
	 * @param addProb Some probabilities may not be added, so we provide this fact(for example whe opponent get a new tile, it's probability is not added
	 *                while will not play or omit)
	 * @throws DAIException Throw if not correct probabilities
	 */
	public void validateOpponentTiles(Round round, float addProb, String msg, boolean virtual) throws DAIException {
		if (sysParamManager.getBooleanParameterValue(checkOpponentProbabilities)) {
			float sum = 0.0F;
			for (Map.Entry<Tile, Float> entry : round.getOpponentTiles().entrySet()) {
				float prob = entry.getValue();
				int left = entry.getKey().getLeft();
				int right = entry.getKey().getRight();
				if (prob > 1.0) {
					if (virtual) {
						notValidRound = round;
						errorMsg = "Opponent tile probability is more than one, tile[" + left + "-" + right + "] method[" + msg + "]";
						break;
					} else {
						logger.warn("Opponent tile probability is more than one, tile[" + left + "-" + right + "] method[" + msg + "]");
						GameLoggingProcessor.logRoundFullInfo(round, false);   // Still print if virtual
						throw new DAIException("opponentTileProbabilityIsMoreThanOne");
					}
				} else if (prob < 0.0) {
					if (virtual) {
						notValidRound = round;
						errorMsg = "Opponent tile probability is less than zero, tile[" + left + "-" + right + "] method[" + msg + "]";
					} else {
						logger.warn("Opponent tile probability is less than zero, tile[" + left + "-" + right + "] method[" + msg + "]");
						GameLoggingProcessor.logRoundFullInfo(round, false);   // Still print if virtual
						throw new DAIException("opponentTileProbabilityIsLessThanZero");
					}
				}
				sum += prob;
			}
			if (!ComparisonHelper.equal(sum + addProb, round.getTableInfo().getOpponentTilesCount())) {
				if (virtual) {
					notValidRound = round;
					errorMsg = "Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + "  addProb:" + addProb + ", method[" + msg + "]";
				} else {
					logger.warn("Opponent tile count and probabilities sum is not same... count:" + round.getTableInfo().getOpponentTilesCount() + "  sum:" + sum + "  addProb:" + addProb + ", method[" + msg + "]");
					GameLoggingProcessor.logRoundFullInfo(round, false);   // Still print if virtual
					throw new DAIException("probabilitiesSumIsNoEqualToOpponentTilesCount");
				}
			}
		}
	}

	public void applyValidation() throws DAIException {
		if (notValidRound != null) {
			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			logger.warn(errorMsg);
			logger.info("Rounds full info");

			List<ParentRound> parentRounds = new ArrayList<>();
			ParentRound parentRound = notValidRound.getParentRound();
			while (parentRound != null) {
				parentRounds.add(parentRound);
				parentRound = parentRound.getParent().getParentRound();
			}
			for (int i = parentRounds.size() - 1; i >= 0; i--) {
				parentRound = parentRounds.get(i);
				logger.info("Height: " + parentRound.getHeight());
				GameLoggingProcessor.logRoundFullInfo(parentRound.getParent(), false); // Still print if virtual
				logger.info("Play move with probability[" + parentRound.getProbability() + "], move[" + parentRound.getMove() + "]");
			}
			GameLoggingProcessor.logRoundFullInfo(notValidRound, false);  // Last(notValid) round

			logger.info(System.lineSeparator() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			throw new DAIException("probabilitiesSumIsNoEqualToOpponentTilesCount");
		}
	}
}
