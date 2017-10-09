package ge.ai.domino.server.dao.domino.played;

import ge.ai.domino.domain.domino.played.GameHistory;
import ge.ai.domino.domain.domino.played.PlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGameResult;

import java.util.List;

public interface PlayedGameDAO {

    void addPlayedGame(PlayedGame game);

    List<PlayedGame> getPlayedGames(Integer id, String version, PlayedGameResult result, Integer pointForWin, String opponentName,
                                    String website);

    GameHistory getGameHistory(int gameId);
}
