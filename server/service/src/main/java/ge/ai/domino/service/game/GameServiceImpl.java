package ge.ai.domino.service.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.server.manager.game.GameManager;

public class GameServiceImpl implements GameService {

    private final GameManager gameManager = new GameManager();

    @Override
    public Round startGame(GameProperties gameProperties, int gameId) throws DAIException {
        return gameManager.startGame(gameProperties, gameId);
    }

    @Override
    public Round addTileForMe(Round round, int left, int right) throws DAIException {
        return gameManager.addTileForMe(round, left, right, false);
    }

    @Override
    public Round addTileForOpponent(Round round) throws DAIException {
        return gameManager.addTileForOpponent(round, false);
    }

    @Override
    public Round playForMe(Round round, Move move) throws DAIException {
        return gameManager.playForMe(round, move, false);
    }

    @Override
    public Round playForOpponent(Round round, Move move) throws DAIException {
        return gameManager.playForOpponent(round, move, false);
    }

    @Override
    public Round getLastPlayedRound(Round round) throws DAIException {
        return gameManager.getLastPlayedRound(round);
    }

    @Override
    public Round addLeftTiles(Round round, int opponentTilesCount) throws DAIException {
        return gameManager.addLeftTilesForMe(round, opponentTilesCount);
    }
}
