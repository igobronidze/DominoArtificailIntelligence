package ge.ai.domino.service.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;

public interface GameService {

    Round startGame(GameProperties gameProperties, int gameId) throws DAIException;

    Round addTileForMe(Round round, int x, int y) throws DAIException;

    Round addTileForOpponent(Round round) throws DAIException;

    Round playForMe(Round round, Move move) throws DAIException;

    Round playForOpponent(Round round, Move move) throws DAIException;

    Round getLastPlayedRound(Round round) throws DAIException;

    Round addLeftTiles(Round round, int opponentTilesCount) throws DAIException;
}
