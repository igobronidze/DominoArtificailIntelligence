package ge.ai.domino.util.domino;

import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.exception.DAIException;

public interface DominoService {

    Hand startGame(GameProperties gameProperties) throws DAIException;

    Hand addTileForMe(Hand hand, int x, int y) throws DAIException;

    Hand addTileForHim(Hand hand) throws DAIException;

    Hand playForMe(Hand hand, int x, int y, PlayDirection direction) throws DAIException;

    Hand playForHim(Hand hand, int x, int y, PlayDirection direction) throws DAIException;

    Hand getLastPlayedHand(Hand hand) throws DAIException;

    Hand addLeftTilesForMe(Hand hand, int count) throws DAIException;
}
