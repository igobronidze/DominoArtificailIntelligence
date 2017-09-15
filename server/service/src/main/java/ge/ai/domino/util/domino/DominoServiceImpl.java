package ge.ai.domino.util.domino;

import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.server.processor.domino.DominoProcessor;

public class DominoServiceImpl implements DominoService {

    private static final DominoProcessor dominoProcessor = new DominoProcessor();

    @Override
    public Hand startGame(GameProperties gameProperties) {
        return dominoProcessor.startGame(gameProperties);
    }

    @Override
    public Hand addTileForMe(Hand hand, int x, int y) {
        return dominoProcessor.addTileForMe(hand, x, y);
    }

    @Override
    public Hand addTileForHim(Hand hand) {
        return dominoProcessor.addTileForHim(hand);
    }

    @Override
    public Hand playForMe(Hand hand, int x, int y, PlayDirection direction) {
        return dominoProcessor.playForMe(hand, x, y, direction);
    }

    @Override
    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction) {
        return dominoProcessor.playForHim(hand, x, y, direction);
    }
}
