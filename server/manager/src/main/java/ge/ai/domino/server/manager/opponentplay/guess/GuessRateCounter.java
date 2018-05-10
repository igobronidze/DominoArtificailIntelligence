package ge.ai.domino.server.manager.opponentplay.guess;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.move.MoveType;

import java.util.List;

public interface GuessRateCounter {

    double getGuessRate(OpponentPlay opponentPlay);
}
