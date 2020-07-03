package ge.ai.domino.service.played;

import ge.ai.domino.common.params.playedgames.GetGroupedPlayedGamesParams;
import ge.ai.domino.common.params.playedgames.GetPlayedGamesParams;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;

import java.util.List;

public interface PlayedGameService {

    List<PlayedGame> getPlayedGames(GetPlayedGamesParams params) throws DAIException;

    List<GroupedPlayedGame> getGroupedPlayedGames(GetGroupedPlayedGamesParams params) throws DAIException;

    void finishGame(int gameId, boolean saveGame, boolean saveOpponentPlays, boolean specifyWinner) throws DAIException;

    int getLastPlayedGameId() throws DAIException;

    List<GameInfo> getGameInfosBeforeId(long gameId) throws DAIException;
}
