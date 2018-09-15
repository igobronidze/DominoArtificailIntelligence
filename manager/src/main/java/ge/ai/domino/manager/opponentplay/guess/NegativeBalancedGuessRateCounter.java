package ge.ai.domino.manager.opponentplay.guess;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.move.MoveType;

public class NegativeBalancedGuessRateCounter implements GuessRateCounter {

	@Override
	public double getGuessRate(OpponentPlay opponentPlay) {
		double opponentTilesCount = 0.0;
		for (OpponentTile opponentTile : opponentPlay.getOpponentTiles().getOpponentTiles()) {
			opponentTilesCount += opponentTile.getProbability();
		}
		double expectedProb = opponentTilesCount / opponentPlay.getOpponentTiles().getOpponentTiles().size();

		if (opponentPlay.getMoveType() == MoveType.ADD_FOR_OPPONENT) {
			double real = 1.0;
			double expected = 1.0;
			for (OpponentTile opponentTile : opponentPlay.getOpponentTiles().getOpponentTiles()) {
				if (opponentPlay.getPossiblePlayNumbers().contains(opponentTile.getLeft()) || opponentPlay.getPossiblePlayNumbers().contains(opponentTile.getRight())) {
					real *= (1 - opponentTile.getProbability());
					expected *= (1 - expectedProb);
				}
			}
			if (real < expected) {
				return -expected / real;
			} else {
				return real / expected;
			}
		} else if (opponentPlay.getMoveType() == MoveType.PLAY_FOR_OPPONENT) {
			for (OpponentTile opponentTile : opponentPlay.getOpponentTiles().getOpponentTiles()) {
				if (opponentTile.getLeft() == opponentPlay.getTile().getLeft() && opponentTile.getRight() == opponentPlay.getTile().getRight()) {
					if (opponentTile.getProbability() < expectedProb) {
						return -expectedProb / opponentTile.getProbability();
					} else {
						return opponentTile.getProbability() / expectedProb;
					}
				}
			}
		}
		return 0.0;
	}
}
