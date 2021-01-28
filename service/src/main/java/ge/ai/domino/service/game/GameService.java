package ge.ai.domino.service.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;

import java.util.Map;

public interface GameService {

    Round startGame(GameProperties gameProperties) throws DAIException;

    Round addTileForMe(int gameId, int left, int right) throws DAIException;

    Round addTileForOpponent(int gameId) throws DAIException;

    Round playForMe(int gameId, Move move) throws DAIException;

    Round playForOpponent(int gameId, Move move) throws DAIException;

    Round getLastPlayedRound(int gameId) throws DAIException;

    void specifyRoundBeginner(int gameId, boolean startMe) throws DAIException;

    void specifyOpponentLeftTiles(int gameId, int leftTilesCount) throws DAIException;

    Round skipRound(int gameId, int myPoint, int opponentPoint, int leftTiles, boolean startMe, boolean finishGame) throws DAIException;

    Round recognizeAndAddNewTilesForMe(int gameId) throws DAIException;

    Round recognizeAndAddInitialTilesForMe(int gameId, Boolean startMe) throws DAIException;

    void editOpponentNameInCache(int gameId, String opponentName) throws DAIException;

    String getCurrentRoundInfoInString(int gameId) throws DAIException;

    boolean roundWillBeBlocked(int gameId, Move move) throws DAIException;

    Map<Tile, Integer> getTilesOrder(int gameId);

    void simulatePlayMove(int gameId, int left, int right, MoveDirection direction) throws DAIException;
}
