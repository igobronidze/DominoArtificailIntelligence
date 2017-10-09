package ge.ai.domino.server.dao.domino.played;

import ge.ai.domino.domain.domino.played.GameHistory;
import ge.ai.domino.domain.domino.played.PlayedGame;
import ge.ai.domino.domain.domino.played.PlayedGameResult;
import ge.ai.domino.server.dao.DatabaseUtil;
import ge.ai.domino.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class PlayedGameDAOImpl implements PlayedGameDAO {

    private Logger logger = Logger.getLogger(PlayedGameDAOImpl.class);

    private PreparedStatement pstmt;

    @Override
    public void addPlayedGame(PlayedGame game) {
        try {
            logger.info("Start add played game method");
            String sql = "INSERT INTO played_game (version, result, date, time, myPoint, himPoint, pointForWin, opponentName," +
                    "website, gameHistory) VALUES (?,?,?,?,?,?,?,?,?,?);";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, game.getVersion());
            pstmt.setString(2, game.getResult().name());
            pstmt.setDate(3, new Date(game.getDate().getTime()));
            pstmt.setTime(4, new Time(game.getDate().getTime()));
            pstmt.setInt(5, game.getMyPoint());
            pstmt.setInt(6, game.getHimPoint());
            pstmt.setInt(7, game.getPointForWin());
            pstmt.setString(8, game.getOpponentName());
            pstmt.setString(9, game.getWebsite());
            pstmt.setString(10, game.getGameHistory().toString());
            pstmt.executeUpdate();
            logger.info("Added played game with id [" + game.getId() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while add played game", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public List<PlayedGame> getPlayedGames(Integer id, String version, PlayedGameResult result, Integer pointForWin, String opponentName,
                                           String website) {
        List<PlayedGame> games = new ArrayList<>();
        try {
            String sql = "SELECT (id, version, result, date, time, myPoint, himPoint, pointForWin, opponentName," +
                    "website) FROM played_game WHERE 1 = 1 ";
            if (id != null) {
                sql += "AND id = " + id + " ";
            }
            if (!StringUtil.isEmpty(version)) {
                sql += "AND version = '" + version + "' ";
            }
            if (result != null) {
                sql += "AND result = '" + result.name() + "' ";
            }
            if (pointForWin != null) {
                sql += "AND pointForWin = " + pointForWin + " ";
            }
            if (!StringUtil.isEmpty(opponentName)) {
                sql += "AND opponentName = '" + opponentName + "' ";
            }
            if (!StringUtil.isEmpty(website)) {
                sql += "AND website = '" + website + "' ";
            }
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                PlayedGame game = new PlayedGame();
                game.setId(rs.getInt("id"));
                game.setVersion(rs.getString("version"));
                game.setResult(PlayedGameResult.valueOf(rs.getString("result")));
                game.setDate(new java.util.Date(rs.getDate("date").getTime() + rs.getTime("time").getTime()));
                game.setMyPoint(rs.getInt("myPoint"));
                game.setHimPoint(rs.getInt("himPoint"));
                game.setPointForWin(rs.getInt("pointForWin"));
                game.setOpponentName(rs.getString("opponentName"));
                game.setWebsite(rs.getString("website"));
                games.add(game);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting played games", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return games;
    }

    @Override
    public GameHistory getGameHistory(int gameId) {
        try {
            String sql = "SELECT (gameHistory) FROM played_game WHERE id = " + gameId;
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                GameHistory gameHistory = new GameHistory();
                String history = rs.getString("gameHistory");
                return gameHistory;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game history", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return null;
    }
}
