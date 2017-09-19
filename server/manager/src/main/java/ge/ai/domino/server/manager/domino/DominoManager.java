package ge.ai.domino.server.manager.domino;

import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.server.manager.domino.processor.GameProcessor;
import ge.ai.domino.server.manager.domino.processor.HimTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.MyTurnProcessor;
import ge.ai.domino.server.manager.domino.processor.TurnProcessor;

public class DominoManager {

    private static final GameProcessor gameProcessor = new GameProcessor();

    private static final TurnProcessor myTurnProcessor = new MyTurnProcessor();

    private static final TurnProcessor himTurnProcessor = new HimTurnProcessor();

    public Hand startGame(GameProperties gameProperties) {
        return gameProcessor.startGame(gameProperties);
    }

    public Hand addTileForMe(Hand hand, int x, int y) {
        return myTurnProcessor.addTile(hand, x, y);
    }

    public Hand addTileForHim(Hand hand) {
        return himTurnProcessor.addTile(hand, 0, 0);
    }

    public Hand playForMe(Hand hand, int x, int y, PlayDirection direction) {
        return myTurnProcessor.play(hand, x, y, direction);
    }

    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction) {
        return himTurnProcessor.play(hand, x, y, direction);
    }

    public Hand getLastPlayedHand(Hand hand) {
        return gameProcessor.getLastPlayedHand(hand);
    }

    public Hand addLeftTilesForHim(Hand hand, int count) {
        return gameProcessor.addLeftTilesForHim(hand, count);
    }
}
