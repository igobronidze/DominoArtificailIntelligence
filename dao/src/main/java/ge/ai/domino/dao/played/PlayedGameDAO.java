package ge.ai.domino.dao.played;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.domain.played.GameResult;

import java.util.List;

public interface PlayedGameDAO {

    int addPlayedGame(PlayedGame game);

    void updatePlayedGame(PlayedGame game);

    List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, Integer channelId);

    GameHistory getGameHistory(int gameId);

    void deletePlayedGame(int gameId);

    List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByChannel, boolean groupedByPointForWin);

    int getLastPlayedGameId();

    List<GameInfo> getGameInfosBeforeId(long gameId);

    void updateGameInfo(GameInfo gameInfo);
}
