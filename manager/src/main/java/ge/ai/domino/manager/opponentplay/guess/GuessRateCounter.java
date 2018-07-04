package ge.ai.domino.manager.opponentplay.guess;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;

public interface GuessRateCounter {

    double getGuessRate(OpponentPlay opponentPlay);
}
