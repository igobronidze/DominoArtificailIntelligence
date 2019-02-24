package ge.ai.domino.service.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;

public interface GameService {

    Round startGame(GameProperties gameProperties) throws DAIException;

    Round addTileForMe(int gameId, int left, int right) throws DAIException;

    Round addTileForOpponent(int gameId) throws DAIException;

    Round playForMe(int gameId, Move move) throws DAIException;

    Round playForOpponent(int gameId, Move move) throws DAIException;

    Round getLastPlayedRound(int gameId) throws DAIException;

    void specifyRoundBeginner(int gameId, boolean startMe);

    void specifyOpponentLeftTiles(int gameId, int leftTilesCount);

    Round skipRound(int gameId, int myPoint, int opponentPoint, int leftTiles, boolean startMe, boolean finishGame);

    Round detectAndAddNewTilesForMe(int gameId, boolean withSecondParams) throws DAIException;

    Round detectAndAddInitialTilesForMe(int gameId, Boolean startMe, boolean withSecondParams) throws DAIException;

    void editOpponentNameInCache(int gameId, String opponentName);

    String getCurrentRoundInfoInString(int gameId);

    boolean roundWillBeBlocked(int gameId, Move move);
}
