package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.ai.predictor.OpponentTilesPredictor;
import ge.ai.domino.manager.game.ai.predictor.OpponentTilesPredictorFactory;
import ge.ai.domino.manager.game.helper.play.GameOperations;
import ge.ai.domino.manager.game.helper.play.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.serverutil.CloneUtil;

import java.util.Map;

public class PlayForOpponentProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) throws DAIException {
		Round cloneRound = CloneUtil.getClone(round);

		boolean firstMove = round.getTableInfo().getLeft() == null;
		boolean playedFromBazaar = false;

		round.getTableInfo().getRoundBlockingInfo().setOmitOpponent(false);
		if (move.getRight() != move.getLeft()) {
			round.getTableInfo().getRoundBlockingInfo().setLastNotTwinPlayedTileMy(false);
		}
		logger.info("Start playForOpponent method for tile [" + move.getLeft() + "-" + move.getRight() + "] direction ["
				+ move.getDirection().name() + "], gameId[" + round.getGameInfo().getGameId() + "]");

		Map<Tile, Double> opponentTiles = round.getOpponentTiles();
		Tile tile = new Tile(move.getLeft(), move.getRight());

		if (round.getTableInfo().getLeft() == null) {
			double sum;
			if (round.getTableInfo().isFirstRound()) {
				sum = GameOperations.makeTwinTilesAsBazaarAndReturnProbabilitiesSum(round.getOpponentTiles(), (move.getLeft() == move.getRight() ? move.getLeft() : -1));
			} else {
				sum = GameOperations.analyzeFirstOpponentTileAndReturnProbabilitiesSum(round.getOpponentTiles(), tile);
			}
			ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		double prob = opponentTiles.get(tile);
		opponentTiles.remove(tile);

		if (round.getTableInfo().getTilesFromBazaar() > 0) {
			ProbabilitiesDistributor.updateProbabilitiesForLastPickedTiles(round, true, false);
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
			round = GameOperations.finishedLastAndGetNewRound(round, false, GameOperations.countLeftTiles(round, true, false), false);
		} else {
            OpponentTilesPredictor minMaxPredictor = OpponentTilesPredictorFactory.getOpponentTilesPredictor(false);
            if (!playedFromBazaar && !firstMove) {
				minMaxPredictor.predict(round, cloneRound, move);
            }
            round.setAiPredictions(MinMaxFactory.getMinMax(true).solve(round));
		}

		logger.info("Played tile for opponent, gameId[" + round.getGameInfo().getGameId() + "]");
		RoundLogger.logRoundFullInfo(round);

		return round;
	}
}
