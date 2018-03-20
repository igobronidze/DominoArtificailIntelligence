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
    public Round addTileForMe(int gameId, int left, int right) throws DAIException {
        return gameManager.addTileForMe(gameId, left, right);
    }

    @Override
    public Round addTileForOpponent(int gameId) throws DAIException {
        return gameManager.addTileForOpponent(gameId);
    }

    @Override
    public Round playForMe(int gameId, Move move) throws DAIException {
        return gameManager.playForMe(gameId, move);
    }

    @Override
    public Round playForOpponent(int gameId, Move move) throws DAIException {
        return gameManager.playForOpponent(gameId, move);
    }

    @Override
    public Round getLastPlayedRound(int gameId) throws DAIException {
        return gameManager.getLastPlayedRound(gameId);
    }

    @Override
    public Round addLeftTiles(int gameId, int opponentTilesCount) throws DAIException {
        return gameManager.addLeftTilesForMe(gameId, opponentTilesCount);
    }

    @Override
    public Round specifyRoundBeginner(int gameId, boolean startMe) {
        return gameManager.specifyRoundBeginner(gameId, startMe);
    }
}
