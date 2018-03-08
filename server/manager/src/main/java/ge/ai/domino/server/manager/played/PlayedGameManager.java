package ge.ai.domino.server.manager.played;

import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.dao.played.PlayedGameDAO;
import ge.ai.domino.server.dao.played.PlayedGameDAOImpl;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Properties;

public class PlayedGameManager {

    private static final Logger logger = Logger.getLogger(PlayedGameManager.class);

    private final PlayedGameDAO playedGameDAO = new PlayedGameDAOImpl();

    public int addPlayedGame(GameProperties gameProperties) {
        PlayedGame playedGame = new PlayedGame();
        playedGame.setOpponentName(gameProperties.getOpponentName());
        playedGame.setPointForWin(gameProperties.getPointsForWin());
        playedGame.setWebsite(gameProperties.getWebsite());
        playedGame.setVersion(getVersion());
        playedGame.setResult(GameResult.RUNS);
        return playedGameDAO.addPlayedGame(playedGame);
    }

    public List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, String website) {
        return playedGameDAO.getPlayedGames(version, result, opponentName, website);
    }

    public GameHistory getGameHistory(int gameId) {
        return playedGameDAO.getGameHistory(gameId);
    }

    public void updatePlayedGame(PlayedGame playedGame) {
        playedGame.setEndDate(new Date());
        playedGame.setGameHistory(CachedGames.getGameHistory(playedGame.getId()));
        playedGameDAO.updatePlayedGame(playedGame);
    }

    public List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin) {
        return playedGameDAO.getGroupedPlayedGames(groupByVersion, groupByOpponentName, groupByWebsite, groupedByPointForWin);
    }

    public void deletePlayedGame(int gameId) {
        playedGameDAO.deletePlayedGame(gameId);
    }

    private String getVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(PlayedGameManager.class.getResourceAsStream("/project.properties"));
            return properties.getProperty("version");
        } catch (Exception ex) {
            logger.error("Error occurred while reading project.properties", ex);
            return "";
        }
    }
}
