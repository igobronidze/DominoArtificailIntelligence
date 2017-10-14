package ge.ai.domino.service.playedgame;

import ge.ai.domino.domain.domino.played.GroupedPlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGameResult;

import java.util.List;

public interface PlayedGameService {

    List<PlayedGame> getPlayedGames(String version, PlayedGameResult result, String opponentName, String website);

    void updatePlayedGame(PlayedGame playedGame);

    List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin);

    void deletePlayedGame(int gameId);
}
