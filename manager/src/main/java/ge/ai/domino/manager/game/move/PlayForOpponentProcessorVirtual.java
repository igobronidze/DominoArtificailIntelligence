package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.helper.play.GameOperations;
import ge.ai.domino.manager.game.helper.play.ProbabilitiesDistributor;

import java.util.Map;

public class PlayForOpponentProcessorVirtual extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) {
		round.getTableInfo().getRoundBlockingInfo().setOmitOpponent(false);
		if (move.getRight() != move.getLeft()) {
			round.getTableInfo().getRoundBlockingInfo().setLastNotTwinPlayedTileMy(false);
		}

		Map<Tile, Double> opponentTiles = round.getOpponentTiles();

		Tile tile = new Tile(move.getLeft(), move.getRight());
		double prob = opponentTiles.get(tile);
		opponentTiles.remove(tile);

		if (round.getTableInfo().getTilesFromBazaar() > 0) {
			ProbabilitiesDistributor.updateProbabilitiesForLastPickedTiles(round, true, true);
		} else {
			if (prob != 1.0) {
				ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(opponentTiles, prob - 1);
			}
		}

		// Play tile
		GameOperations.playTile(round, move, false);
		round.getTableInfo().setOpponentTilesCount(round.getTableInfo().getOpponentTilesCount() - 1);
		round.getGameInfo().setOpponentPoint(round.getGameInfo().getOpponentPoint() + GameOperations.countScore(round));
		round.getTableInfo().setMyMove(true);

		if (round.getTableInfo().getOpponentTilesCount() == 0) {
			round = GameOperations.finishedLastAndGetNewRound(round, false, GameOperations.countLeftTiles(round, true, true), true);
		} else if (GameOperations.isRoundBlocked(round)) {
			logger.info("Round is blocked");
			round = GameOperations.blockRound(round, GameOperations.countLeftTiles(round, false, false), false);
		}

		return round;
	}
}
