package ge.ai.domino.service.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;

import java.util.List;

public interface GameService {

    Round startGame(GameProperties gameProperties, int gameId) throws DAIException;

    Round addTileForMe(int gameId, int left, int right) throws DAIException;

    Round addTileForOpponent(int gameId) throws DAIException;

    Round playForMe(int gameId, Move move) throws DAIException;

    Round playForOpponent(int gameId, Move move) throws DAIException;

    Round getLastPlayedRound(int gameId) throws DAIException;

    void specifyRoundBeginner(int gameId, boolean startMe);

    void specifyOpponentLeftTiles(int gameId, int leftTilesCount);

    Round skipRound(int gameId, int myPoint, int opponentPoint, int leftTiles, boolean startMe);

    Round addTilesForMe(int gameId, List<Tile> tiles) throws DAIException;
}
