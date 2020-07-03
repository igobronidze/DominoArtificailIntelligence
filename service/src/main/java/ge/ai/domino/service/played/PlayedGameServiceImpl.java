package ge.ai.domino.service.played;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.manager.played.PlayedGameManager;

import java.util.List;

public class PlayedGameServiceImpl implements PlayedGameService {

    private final PlayedGameManager playedGameManager = new PlayedGameManager();

    @Override
    public List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, Integer channelId, String level) {
        return playedGameManager.getPlayedGames(version, result, opponentName, channelId, level);
    }

    @Override
    public List<GroupedPlayedGame> getGroupedPlayedGames(String version, boolean groupByVersion, boolean groupByChannel, boolean groupedByPointForWin, boolean groupByLevel) {
        return playedGameManager.getGroupedPlayedGames(version, groupByVersion, groupByChannel, groupedByPointForWin, groupByLevel);
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
