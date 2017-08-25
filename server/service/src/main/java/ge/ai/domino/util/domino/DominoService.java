package ge.ai.domino.util.domino;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.PlayDirection;

public interface DominoService {

    Game startGame(GameProperties gameProperties);

    Game addTileForMe(Game game, int x, int y);

    Game addTileForHim(Game game);

    Game playForMe(Game game, int x, int y, PlayDirection direction);

    Game playForHim(Game game, int x, int y, PlayDirection direction);
}
