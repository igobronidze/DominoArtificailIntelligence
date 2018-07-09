package ge.ai.domino.service.played;

import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.manager.played.PlayedGameManager;

import java.util.List;

public class PlayedGameServiceImpl implements PlayedGameService {

    private final PlayedGameManager playedGameManager = new PlayedGameManager();

    @Override
    public List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, String website) {
        return playedGameManager.getPlayedGames(version, result, opponentName, website);
    }

    @Override
    public List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin) {
        return playedGameManager.getGroupedPlayedGames(groupByVersion, groupByOpponentName, groupByWebsite, groupedByPointForWin);
    }

    @Override
    public void finishGame(int gameId, boolean saveGame, boolean saveOpponentPlays, boolean specifyWinner) {
        playedGameManager.finishGame(gameId, saveGame, saveOpponentPlays, specifyWinner);
    }
}
