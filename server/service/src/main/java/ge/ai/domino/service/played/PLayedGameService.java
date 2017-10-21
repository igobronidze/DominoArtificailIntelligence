package ge.ai.domino.service.played;

import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.domain.played.GameResult;

import java.util.List;

public interface PlayedGameService {

    List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, String website);

    void updatePlayedGame(PlayedGame playedGame);

    List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin);

    void deletePlayedGame(int gameId);
}
