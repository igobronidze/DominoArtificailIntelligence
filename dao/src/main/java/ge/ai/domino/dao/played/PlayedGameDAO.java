package ge.ai.domino.dao.played;

import ge.ai.domino.common.params.playedgames.GetGroupedPlayedGamesParams;
import ge.ai.domino.common.params.playedgames.GetPlayedGamesParams;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;

import java.util.List;

public interface PlayedGameDAO {

    int addPlayedGame(PlayedGame game);

    void updatePlayedGame(PlayedGame game);

    List<PlayedGame> getPlayedGames(GetPlayedGamesParams params);

    GameHistory getGameHistory(int gameId) throws DAIException;

    void deletePlayedGame(int gameId);

    List<GroupedPlayedGame> getGroupedPlayedGames(GetGroupedPlayedGamesParams params);

    int getLastPlayedGameId();

    List<GameInfo> getGameInfosBeforeId(long gameId);

    void updateGameInfo(GameInfo gameInfo);

    GameProperties getGameProperties(int gameId) throws DAIException;

    List<Integer> getAllPlayedGame();
}
