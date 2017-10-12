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
    public int addPlayedGame(PlayedGame game) {
        try {
            logger.info("Start add played game method");
            String sql = "INSERT INTO played_game (version, point_for_win, opponent_name, website, result) VALUES (?,?,?,?,?);";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, game.getVersion());
            pstmt.setInt(2, game.getPointForWin());
            pstmt.setString(3, game.getOpponentName());
            pstmt.setString(4, game.getWebsite());
            pstmt.setString(5, game.getResult().name());
            pstmt.executeUpdate();
            String idSql = "SELECT MAX(id) AS max_id FROM played_game";
            pstmt = DatabaseUtil.getConnection().prepareStatement(idSql);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int id = rs.getInt("max_id");
            logger.info("Added played game with id [" + id + "]");
            return id;
        } catch (SQLException ex) {
            logger.error("Error occurred while add played game", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return 0;
    }

    @Override
    public void updatePlayedGame(PlayedGame game) {
        try {
            logger.info("Start edit played game method id[" + game.getId() + "]");
            String sql = "UPDATE played_game SET result = ?, date = ?, time = ?, my_point = ?, him_point = ? WHERE id = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, game.getResult().name());
            pstmt.setDate(2, new Date(game.getDate().getTime()));
            pstmt.setTime(3, new Time(game.getDate().getTime()));
            pstmt.setInt(4, game.getMyPoint());
            pstmt.setInt(5, game.getHimPoint());
            pstmt.setInt(6, game.getId());
            pstmt.executeUpdate();
            logger.info("Updated played game id[" + game.getId() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while edit played game", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public List<PlayedGame> getPlayedGames(String version, PlayedGameResult result, String opponentName, String website) {
        List<PlayedGame> games = new ArrayList<>();
        try {
            String sql = "SELECT id, version, result, date, time, my_point, him_point, point_for_win, opponent_name, website FROM played_game WHERE 1 = 1 ";
            if (!StringUtil.isEmpty(version)) {
                sql += "AND version = '" + version + "' ";
            }
            if (result != null) {
                sql += "AND result = '" + result.name() + "' ";
            }
            if (!StringUtil.isEmpty(opponentName)) {
                sql += "AND opponent_name = '" + opponentName + "' ";
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
                Date dateFromDB = rs.getDate("date");
                Time timeFromDB = rs.getTime("time");
                if (dateFromDB != null && timeFromDB != null) {
                    game.setDate(new java.util.Date(dateFromDB.getTime() + timeFromDB.getTime()));
                }
                game.setMyPoint(rs.getInt("my_point"));
                game.setHimPoint(rs.getInt("him_point"));
                game.setPointForWin(rs.getInt("point_for_win"));
                game.setOpponentName(rs.getString("opponent_name"));
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
            String sql = "SELECT (game_history) FROM played_game WHERE id = " + gameId;
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
