package ge.ai.domino.manager.opponentplay.guess;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.move.MoveType;

public class SimpleGuessRateCounter implements GuessRateCounter {

    @Override
    public double getGuessRate(OpponentPlay opponentPlay) {
        if (opponentPlay.getMoveType() == MoveType.ADD_FOR_OPPONENT) {
            double prob = 1.0;
            for (OpponentTile opponentTile : opponentPlay.getOpponentTiles().getOpponentTiles()) {
                if (opponentPlay.getPossiblePlayNumbers().contains(opponentTile.getLeft()) || opponentPlay.getPossiblePlayNumbers().contains(opponentTile.getRight())) {
                    prob *= (1 - opponentTile.getProbability());
                }
            }
            return prob;
        } else if (opponentPlay.getMoveType() == MoveType.PLAY_FOR_OPPONENT) {
            for (OpponentTile opponentTile : opponentPlay.getOpponentTiles().getOpponentTiles()) {
                if (opponentTile.getLeft() == opponentPlay.getTile().getLeft() && opponentTile.getRight() == opponentPlay.getTile().getRight()) {
                    return opponentTile.getProbability();
                }
            }
        }
        return 0.0;
    }
}
