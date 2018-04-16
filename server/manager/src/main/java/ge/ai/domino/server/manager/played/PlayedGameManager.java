package ge.ai.domino.server.manager.played;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.dao.opponentplay.OpponentPlayDAO;
import ge.ai.domino.server.dao.opponentplay.OpponentPlayDAOImpl;
import ge.ai.domino.server.dao.played.PlayedGameDAO;
import ge.ai.domino.server.dao.played.PlayedGameDAOImpl;
import ge.ai.domino.server.manager.util.ProjectVersionUtil;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Properties;

public class PlayedGameManager {

    private final PlayedGameDAO playedGameDAO = new PlayedGameDAOImpl();

    private final OpponentPlayDAO opponentPlayDAO = new OpponentPlayDAOImpl();

    public int addPlayedGame(GameProperties gameProperties) {
        PlayedGame playedGame = new PlayedGame();
        playedGame.setOpponentName(gameProperties.getOpponentName());
        playedGame.setPointForWin(gameProperties.getPointsForWin());
        playedGame.setWebsite(gameProperties.getWebsite());
        playedGame.setVersion(ProjectVersionUtil.getVersion());
        playedGame.setResult(GameResult.RUNS);
        return playedGameDAO.addPlayedGame(playedGame);
    }

    public List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, String website) {
        return playedGameDAO.getPlayedGames(version, result, opponentName, website);
    }

    public GameHistory getGameHistory(int gameId) {
        return playedGameDAO.getGameHistory(gameId);
    }

    public List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin) {
        return playedGameDAO.getGroupedPlayedGames(groupByVersion, groupByOpponentName, groupByWebsite, groupedByPointForWin);
    }

    public void finishGame(int gameId, boolean saveGame, boolean saveOpponentPlays) {
        if (saveGame) {
            Round round = CachedGames.getCurrentRound(gameId, true);
            GameInfo gameInfo = round.getGameInfo();
            PlayedGame playedGame = new PlayedGame();
            if ((round.getTableInfo().getOpponentTilesCount() == 0.0 || round.getMyTiles().isEmpty()) && round.getTableInfo().getLeft() != null) {
                if (gameInfo.getMyPoint() > gameInfo.getOpponentPoint()) {
                    playedGame.setResult(GameResult.I_WIN);
                } else {
                    playedGame.setResult(GameResult.OPPONENT_WIN);
                }
            } else {
                playedGame.setResult(GameResult.STOPPED);
            }
            playedGame.setMyPoint(gameInfo.getMyPoint());
            playedGame.setOpponentPoint(gameInfo.getOpponentPoint());
            playedGame.setId(gameInfo.getGameId());
            playedGame.setEndDate(new Date());
            playedGame.setGameHistory(CachedGames.getGameHistory(playedGame.getId()));
            playedGameDAO.updatePlayedGame(playedGame);
        } else {
            playedGameDAO.deletePlayedGame(gameId);
        }
        if (saveOpponentPlays) {
            List<OpponentPlay> opponentPlays = CachedGames.getOpponentPlays(gameId);
            if (!opponentPlays.isEmpty()) {
                opponentPlayDAO.addOpponentPlays(CachedGames.getOpponentPlays(gameId));
            }
        }
    }
}
