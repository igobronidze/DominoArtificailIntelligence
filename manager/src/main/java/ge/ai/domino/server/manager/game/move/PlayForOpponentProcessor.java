package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.server.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.server.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.server.manager.game.ai.predictor.OpponentTilesPredictor;
import ge.ai.domino.server.manager.game.helper.game.GameOperations;
import ge.ai.domino.server.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;

import java.util.Map;

public class PlayForOpponentProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move, boolean virtual) throws DAIException {
		if (virtual) {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
		} else {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
		}
		boolean firstMove = round.getTableInfo().getLeft() == null;
		boolean playedFromBazaar = false;

		MoveDirection direction = move.getDirection();
		round.getTableInfo().getRoundBlockingInfo().setOmitOpponent(false);
		if (move.getRight() != move.getLeft()) {
			round.getTableInfo().getRoundBlockingInfo().setLastNotTwinPlayedTileMy(false);
		}
		int gameId = round.getGameInfo().getGameId();
		GameLoggingProcessor.logInfoAboutMove("Start playForOpponent method for tile [" + move.getLeft() + "-" + move.getRight() + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);

		// Not played twins case
		Map<Tile, Double> opponentTiles = round.getOpponentTiles();
		if (round.getTableInfo().isFirstRound() && round.getTableInfo().getLeft() == null) {
			double sum = GameOperations.makeTwinTilesAsBazaarAndReturnProbabilitiesSum(round.getOpponentTiles(), (move.getLeft() == move.getRight() ? move.getLeft() : -1));
			ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		Tile tile = new Tile(move.getLeft(), move.getRight());
		double prob = opponentTiles.get(tile);
		opponentTiles.remove(tile);

		if (round.getTableInfo().getTilesFromBazaar() > 0) {
			ProbabilitiesDistributor.updateProbabilitiesForLastPickedTiles(round, true, virtual);
			playedFromBazaar = true;
		} else {
			if (prob != 1.0) {
				ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTiles, prob - 1);
			}
		}

		// Play tile
		GameOperations.playTile(round, move);
		round.getTableInfo().setOpponentTilesCount(round.getTableInfo().getOpponentTilesCount() - 1);
		round.getGameInfo().setOpponentPoint(round.getGameInfo().getOpponentPoint() + GameOperations.countScore(round));
		round.getTableInfo().setMyMove(true);

		if (round.getTableInfo().getOpponentTilesCount() == 0) {
			round = GameOperations.finishedLastAndGetNewRound(round, false, GameOperations.countLeftTiles(round, true, virtual), virtual);
		} else if (!virtual) {
            OpponentTilesPredictor minMaxPredictor = new MinMaxPredictor();
            if (!playedFromBazaar && minMaxPredictor.usePredictor() && !firstMove) {
				minMaxPredictor.predict(round, move);
            }
            round.setAiPredictions(MinMaxFactory.getMinMax().solve(round));
		}

		GameLoggingProcessor.logInfoAboutMove("Played tile for opponent, gameId[" + gameId + "]", virtual);
		GameLoggingProcessor.logRoundFullInfo(round, virtual);

		return round;
	}
}
