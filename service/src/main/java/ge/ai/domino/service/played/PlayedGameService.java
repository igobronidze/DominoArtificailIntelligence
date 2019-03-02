package ge.ai.domino.service.played;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;

import java.util.List;

public interface PlayedGameService {

    List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, Integer channelId, String level) throws DAIException;

    List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByChannel,
                                                  boolean groupedByPointForWin, boolean groupByLevel) throws DAIException;

    void finishGame(int gameId, boolean saveGame, boolean saveOpponentPlays, boolean specifyWinner) throws DAIException;

    int getLastPlayedGameId() throws DAIException;

    List<GameInfo> getGameInfosBeforeId(long gameId) throws DAIException;
}
