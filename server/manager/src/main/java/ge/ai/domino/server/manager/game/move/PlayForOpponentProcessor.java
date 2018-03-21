package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.server.manager.game.helper.GameOperations;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.minmax.MinMax;
import ge.ai.domino.server.manager.game.validator.OpponentTilesValidator;

import java.util.Map;

public class PlayForOpponentProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move, boolean virtual) throws DAIException {
		if (virtual) {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
		} else {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
		}
		MoveDirection direction = move.getDirection();
		round.getTableInfo().setOmittedOpponent(false);
		int gameId = round.getGameInfo().getGameId();
		GameLoggingProcessor.logInfoAboutMove("Start playForOpponent method for tile [" + move.getLeft() + "-" + move.getRight() + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);

		// Not played twins case
		Map<Tile, Float> opponentTiles = round.getOpponentTiles();
		if (round.getTableInfo().isFirstRound() && round.getTableInfo().getLeft() == null) {
			float sum = GameOperations.makeTwinTilesAsBazaarAndReturnProbabilitiesSum(round.getOpponentTiles(), (move.getLeft() == move.getRight() ? move.getLeft() : -1));
			GameOperations.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		Tile tile = new Tile(move.getLeft(), move.getRight());
		float prob = opponentTiles.get(tile);
		round.getTableInfo().setLastPlayedProb(prob);
		opponentTiles.remove(tile);

		if (round.getTableInfo().getTilesFromBazaar() > 0) {
			GameOperations.updateProbabilitiesForLastPickedTiles(round, true);
		} else {
			if (prob != 1.0) {
				GameOperations.distributeProbabilitiesOpponentProportional(opponentTiles, prob - 1);
			}
		}

		// Play tile
		GameOperations.playTile(round, move);
		round.getTableInfo().setOpponentTilesCount(round.getTableInfo().getOpponentTilesCount() - 1);
		GameOperations.addLeftTiles(round.getGameInfo(), GameOperations.countScore(round), false, gameId, virtual);
		round.getTableInfo().setMyMove(true);


		if (round.getTableInfo().getOpponentTilesCount() == 0) {
			return GameOperations.finishedLastAndGetNewRound(round, false, true, virtual);
		}

		if (!virtual) {
//			MinMax minMax = new MinMax();
//			Move aiPrediction = minMax.minMax(round);
//			round.setAiPrediction(aiPrediction); TODO
		}

		GameLoggingProcessor.logInfoAboutMove("Played tile for opponent, gameId[" + gameId + "]", virtual);
		GameLoggingProcessor.logRoundFullInfo(round, virtual);

		OpponentTilesValidator.validateOpponentTiles(round, 0, "playForOpponent " + move);
		return round;
	}
}