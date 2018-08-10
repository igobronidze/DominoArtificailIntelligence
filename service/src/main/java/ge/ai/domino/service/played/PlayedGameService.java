package ge.ai.domino.service.played;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.domain.played.GameResult;

import java.util.List;

public interface PlayedGameService {

    List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, Integer channelId);

    List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByChannel, boolean groupedByPointForWin);

    void finishGame(int gameId, boolean saveGame, boolean saveOpponentPlays, boolean specifyWinner);

    int getLastPlayedGameId();

    List<GameInfo> getGameInfosBeforeId(long gameId);
}
