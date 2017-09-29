package ge.ai.domino.util.domino;

import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.server.manager.domino.DominoManager;

public class DominoServiceImpl implements DominoService {

    private static final DominoManager dominoManager = new DominoManager();

    @Override
    public Hand startGame(GameProperties gameProperties, int gameId) throws DAIException {
        return dominoManager.startGame(gameProperties, gameId);
    }

    @Override
    public Hand addTileForMe(Hand hand, int x, int y) throws DAIException {
        return dominoManager.addTileForMe(hand, x, y, false);
    }

    @Override
    public Hand addTileForHim(Hand hand) throws DAIException {
        return dominoManager.addTileForHim(hand, false);
    }

    @Override
    public Hand playForMe(Hand hand, int x, int y, PlayDirection direction) throws DAIException {
        return dominoManager.playForMe(hand, x, y, direction, false);
    }

    @Override
    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction) throws DAIException {
        return dominoManager.playForHim(hand, x, y, direction, false);
    }

    @Override
    public Hand getLastPlayedHand(Hand hand) throws DAIException {
        return dominoManager.getLastPlayedHand(hand);
    }

    @Override
    public Hand addLeftTiles(Hand hand, int himTilesCount) throws DAIException {
        return dominoManager.addLeftTilesForMe(hand, himTilesCount);
    }
}
