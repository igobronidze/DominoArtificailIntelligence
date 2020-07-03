package ge.ai.domino.service.played;

import ge.ai.domino.common.params.playedgames.GetGroupedPlayedGamesParams;
import ge.ai.domino.common.params.playedgames.GetPlayedGamesParams;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.manager.played.PlayedGameManager;

import java.util.List;

public class PlayedGameServiceImpl implements PlayedGameService {

    private final PlayedGameManager playedGameManager = new PlayedGameManager();

    @Override
    public List<PlayedGame> getPlayedGames(GetPlayedGamesParams params) {
        return playedGameManager.getPlayedGames(params);
    }

    @Override
    public List<GroupedPlayedGame> getGroupedPlayedGames(GetGroupedPlayedGamesParams params) {
        return playedGameManager.getGroupedPlayedGames(params);
    }

    @Override
    public void finishGame(int gameId, boolean saveGame, boolean saveOpponentPlays, boolean specifyWinner) {
        playedGameManager.finishGame(gameId, saveGame, saveOpponentPlays, specifyWinner);
    }

    @Override
    public int getLastPlayedGameId() {
        return playedGameManager.getLastPlayedGameId();
    }

    @Override
    public List<GameInfo> getGameInfosBeforeId(long gameId) {
        return playedGameManager.getGameInfosBeforeId(gameId);
    }
}
