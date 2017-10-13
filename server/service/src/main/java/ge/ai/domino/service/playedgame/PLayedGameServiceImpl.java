package ge.ai.domino.service.playedgame;

import ge.ai.domino.domain.domino.played.GroupedPlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGameResult;
import ge.ai.domino.server.manager.playedgame.PlayedGameManager;

import java.util.List;

public class PlayedGameServiceImpl implements PlayedGameService {

    private final PlayedGameManager playedGameManager = new PlayedGameManager();

    @Override
    public List<PlayedGame> getPlayedGames(String version, PlayedGameResult result, String opponentName, String website) {
        return playedGameManager.getPlayedGames(version, result, opponentName, website);
    }

    @Override
    public void updatePlayedGame(PlayedGame playedGame) {
        playedGameManager.updatePlayedGame(playedGame);
    }

    @Override
    public List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin) {
        return playedGameManager.getGroupedPlayedGames(groupByVersion, groupByOpponentName, groupByWebsite, groupedByPointForWin);
    }
}
