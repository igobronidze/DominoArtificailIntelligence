package ge.ai.domino.util.domino;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.server.processor.domino.DominoProcessor;

public class DominoServiceImpl implements DominoService {

    private static final DominoProcessor dominoProcessor = new DominoProcessor();

    @Override
    public Game startGame(GameProperties gameProperties) {
        return dominoProcessor.startGame(gameProperties);
    }

    @Override
    public Game addTileForMe(Game game, int x, int y) {
        return dominoProcessor.addTileForMe(game, x, y);
    }

    @Override
    public Game addTileForHim(Game game) {
        return dominoProcessor.addTileForHim(game);
    }

    @Override
    public Game playForMe(Game game, int x, int y, PlayDirection direction) {
        return dominoProcessor.playForMe(game, x, y, direction);
    }

    @Override
    public Game playForHim(Game game, int x, int y, PlayDirection direction) {
        return dominoProcessor.playForHim(game, x, y, direction);
    }
}
