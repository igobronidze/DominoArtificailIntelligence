package ge.ai.domino.manager.played;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.dao.opponentplay.OpponentPlayDAO;
import ge.ai.domino.dao.opponentplay.OpponentPlayDAOImpl;
import ge.ai.domino.dao.played.PlayedGameDAO;
import ge.ai.domino.dao.played.PlayedGameDAOImpl;
import ge.ai.domino.manager.util.ProjectVersionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void finishGame(int gameId, boolean saveGame, boolean saveOpponentPlays, boolean specifyWinner) {
        if (saveGame) {
            Round round = CachedGames.getCurrentRound(gameId, true);
            GameInfo gameInfo = round.getGameInfo();
            PlayedGame playedGame = new PlayedGame();
            if (specifyWinner || ((round.getTableInfo().getOpponentTilesCount() == 0.0 || round.getMyTiles().isEmpty()) && round.getTableInfo().getLeft() != null)) {
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
            playedGame.setOpponentName(CachedGames.getGameProperties(gameId).getOpponentName());
            playedGameDAO.updatePlayedGame(playedGame);
        } else {
            playedGameDAO.deletePlayedGame(gameId);
        }
        if (saveOpponentPlays) {
            List<OpponentPlay> opponentPlays = removeExtraPlays(CachedGames.getOpponentPlays(gameId));
            if (!opponentPlays.isEmpty()) {
                opponentPlayDAO.addOpponentPlays(opponentPlays);
            }
        }
    }

    public int getLastPlayedGameId() {
        return playedGameDAO.getLastPlayedGameId();
    }

    public List<GameInfo> getGameInfosBeforeId(long gameId) {
        return playedGameDAO.getGameInfosBeforeId(gameId);
    }

    public void updateGameInfo(GameInfo gameInfo) {
        playedGameDAO.updateGameInfo(gameInfo);
    }

    private List<OpponentPlay> removeExtraPlays(List<OpponentPlay> opponentPlays) {
        List<OpponentPlay> result = new ArrayList<>();
        boolean lastAdd = false;
        for (OpponentPlay opponentPlay : opponentPlays) {
            if (!lastAdd) {
                result.add(opponentPlay);
            }
            lastAdd = opponentPlay.getMoveType() == MoveType.ADD_FOR_OPPONENT;
        }
        return result;
    }
}
