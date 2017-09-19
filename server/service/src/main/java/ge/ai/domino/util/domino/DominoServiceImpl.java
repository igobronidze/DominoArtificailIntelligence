package ge.ai.domino.util.domino;

import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.server.manager.domino.DominoManager;

public class DominoServiceImpl implements DominoService {

    private static final DominoManager dominoManager = new DominoManager();

    @Override
    public Hand startGame(GameProperties gameProperties) {
        return dominoManager.startGame(gameProperties);
    }

    @Override
    public Hand addTileForMe(Hand hand, int x, int y) {
        return dominoManager.addTileForMe(hand, x, y);
    }

    @Override
    public Hand addTileForHim(Hand hand) {
        return dominoManager.addTileForHim(hand);
    }

    @Override
    public Hand playForMe(Hand hand, int x, int y, PlayDirection direction) {
        return dominoManager.playForMe(hand, x, y, direction);
    }

    @Override
    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction) {
        return dominoManager.playForHim(hand, x, y, direction);
    }

    @Override
    public Hand getLastPlayedHand(Hand hand) {
        return dominoManager.getLastPlayedHand(hand);
    }

    @Override
    public Hand addLeftTilesForHim(Hand hand, int count) {
        return dominoManager.addLeftTilesForHim(hand, count);
    }
}
