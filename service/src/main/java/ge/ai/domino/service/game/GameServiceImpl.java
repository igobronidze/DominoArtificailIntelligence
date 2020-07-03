package ge.ai.domino.service.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.GameManager;

import java.util.Map;

public class GameServiceImpl implements GameService {

    private final GameManager gameManager = new GameManager();

    @Override
    public Round startGame(GameProperties gameProperties) {
        return gameManager.startGame(gameProperties);
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
    public void specifyRoundBeginner(int gameId, boolean startMe) {
        gameManager.specifyRoundBeginner(gameId, startMe);
    }

    @Override
    public void specifyOpponentLeftTiles(int gameId, int leftTilesCount) {
        gameManager.specifyOpponentLeftTiles(gameId, leftTilesCount);
    }

    @Override
    public Round skipRound(int gameId, int myPoint, int opponentPoint, int leftTiles, boolean startMe, boolean   finishGame) {
        return gameManager.skipRound(gameId, myPoint, opponentPoint, leftTiles, startMe, finishGame);
    }

    @Override
    public Round detectAndAddNewTilesForMe(int gameId, boolean withSecondParams) throws DAIException {
        return gameManager.detectAndAddNewTilesForMe(gameId, withSecondParams);
    }

    @Override
    public Round detectAndAddInitialTilesForMe(int gameId, Boolean startMe, boolean withSecondParams) throws DAIException {
        return gameManager.detectAndAddInitialTilesForMe(gameId, startMe, withSecondParams);
    }

    @Override
    public void editOpponentNameInCache(int gameId, String opponentName) {
        gameManager.editOpponentNameInCache(gameId, opponentName);
    }

    @Override
    public String getCurrentRoundInfoInString(int gameId) {
        return gameManager.getCurrentRoundInfoInString(gameId);
    }

    @Override
    public boolean roundWillBeBlocked(int gameId, Move move) {
        return gameManager.roundWillBeBlocked(gameId, move);
    }

    @Override
    public Map<Tile, Integer> getTilesOrder(int gameId) {
        return gameManager.getTilesOrder(gameId);
    }
}
