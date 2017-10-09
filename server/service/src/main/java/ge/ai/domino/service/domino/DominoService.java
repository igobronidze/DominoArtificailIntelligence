package ge.ai.domino.service.domino;

import ge.ai.domino.domain.domino.game.GameProperties;
import ge.ai.domino.domain.domino.game.Hand;
import ge.ai.domino.domain.domino.game.PlayDirection;
import ge.ai.domino.domain.exception.DAIException;

public interface DominoService {

    Hand startGame(GameProperties gameProperties, int gameId) throws DAIException;

    Hand addTileForMe(Hand hand, int x, int y) throws DAIException;

    Hand addTileForHim(Hand hand) throws DAIException;

    Hand playForMe(Hand hand, int x, int y, PlayDirection direction) throws DAIException;

    Hand playForHim(Hand hand, int x, int y, PlayDirection direction) throws DAIException;

    Hand getLastPlayedHand(Hand hand) throws DAIException;

    Hand addLeftTiles(Hand hand, int himTilesCount) throws DAIException;
}
