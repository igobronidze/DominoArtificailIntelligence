package ge.ai.domino.util.domino;

import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;

public interface DominoService {

    Hand startGame(GameProperties gameProperties);

    Hand addTileForMe(Hand hand, int x, int y);

    Hand addTileForHim(Hand hand);

    Hand playForMe(Hand hand, int x, int y, PlayDirection direction);

    Hand playForHim(Hand hand, int x, int y, PlayDirection direction);
}
