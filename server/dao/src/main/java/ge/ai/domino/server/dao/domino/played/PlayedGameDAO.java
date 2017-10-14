package ge.ai.domino.server.dao.domino.played;

import ge.ai.domino.domain.domino.played.GameHistory;
import ge.ai.domino.domain.domino.played.GroupedPlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGameResult;

import java.util.List;

public interface PlayedGameDAO {

    int addPlayedGame(PlayedGame game);

    void updatePlayedGame(PlayedGame game);

    List<PlayedGame> getPlayedGames(String version, PlayedGameResult result, String opponentName, String website);

    GameHistory getGameHistory(int gameId);

    void deletePlayedGame(int gameId);

    List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin);
}
