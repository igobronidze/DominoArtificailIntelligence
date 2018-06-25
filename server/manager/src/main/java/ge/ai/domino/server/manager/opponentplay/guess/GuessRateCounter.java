package ge.ai.domino.server.manager.opponentplay.guess;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;

public interface GuessRateCounter {

    double getGuessRate(OpponentPlay opponentPlay);
}
