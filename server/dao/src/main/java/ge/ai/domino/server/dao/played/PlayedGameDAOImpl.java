package ge.ai.domino.server.dao.played;

import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.domain.played.GameResult;
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
            logger.info("Start addGame method");
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
            logger.info("Added game id [" + id + "]");
            return id;
        } catch (SQLException ex) {
            logger.error("Error occurred while add game game", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return 0;
    }

    @Override
    public void updatePlayedGame(PlayedGame game) {
        try {
            logger.info("Start editGame method id[" + game.getId() + "]");
            String sql = "UPDATE played_game SET result = ?, date = ?, time = ?, my_point = ?, opponent_point = ?, game_history = ? WHERE id = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, game.getResult().name());
            pstmt.setDate(2, new Date(game.getEndDate().getTime()));
            pstmt.setTime(3, new Time(game.getEndDate().getTime()));
            pstmt.setInt(4, game.getMyPoint());
            pstmt.setInt(5, game.getOpponentPoint());
            pstmt.setString(6, GameHistoryMarshaller.getMarshalledHistory(game.getGameHistory()));
            pstmt.setInt(7, game.getId());
            pstmt.executeUpdate();
            logger.info("Updated game id[" + game.getId() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while edit game game", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, String website) {
        List<PlayedGame> games = new ArrayList<>();
        try {
            String sql = "SELECT id, version, result, date, time, my_point, opponent_point, point_for_win, opponent_name, website FROM played_game WHERE 1 = 1 ";
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
                game.setResult(GameResult.valueOf(rs.getString("result")));
                Date dateFromDB = rs.getDate("date");
                Time timeFromDB = rs.getTime("time");
                if (dateFromDB != null && timeFromDB != null) {
                    game.setEndDate(new java.util.Date(dateFromDB.getTime() + timeFromDB.getTime()));
                }
                game.setMyPoint(rs.getInt("my_point"));
                game.setOpponentPoint(rs.getInt("opponent_point"));
                game.setPointForWin(rs.getInt("point_for_win"));
                game.setOpponentName(rs.getString("opponent_name"));
                game.setWebsite(rs.getString("website"));
                games.add(game);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game games", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return games;
    }

    @Override
    public GameHistory getGameHistory(int gameId) {
        try {
            String sql = "SELECT game_history FROM played_game WHERE id = " + gameId;
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String history = rs.getString("game_history");
                return GameHistoryMarshaller.unmarshallGameHistory(history);
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

    @Override
    public void deletePlayedGame(int gameId) {
        try {
            logger.info("Start deleteGame method id[" + gameId + "]");
            String sql = "DELETE FROM played_game WHERE id = ?";
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
            logger.info("Deleted game id[" + gameId + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while delete game game", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin) {
        List<GroupedPlayedGame> games = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder("SELECT ");
            boolean first = true;
            if (groupByVersion) {
                sb.append("version");
                first = false;
            }
            if (groupByOpponentName) {
                if (first) {
                    sb.append("opponent_name");
                } else {
                    sb.append(", opponent_name");
                }
                first = false;
            }
            if (groupByWebsite) {
                if (first) {
                    sb.append("website");
                } else {
                    sb.append(", website");
                }
                first = false;
            }
            if (groupedByPointForWin) {
                if (first) {
                    sb.append("point_for_win");
                } else {
                    sb.append(", point_for_win");
                }
                first = false;
            }
            if (!first) {
                sb.append(", ");
            }
            sb.append(" sum(case when result = '").append(GameResult.I_WIN).append("' then 1 else 0 end) as win_me, sum(case when result = '").append(GameResult.OPPONENT_WIN)
                    .append("' then 1 else 0 end) as win_opponent, sum(case when result = '").append(GameResult.STOPPED).append("' then 1 else 0 end) as stopped FROM played_game ");
            first = true;
            if (groupByVersion) {
                sb.append("GROUP BY version");
                first = false;
            }
            if (groupByOpponentName) {
                if (!first) {
                    sb.append(", opponent_name");
                } else {
                    sb.append("GROUP BY opponent_name");
                }
                first = false;
            }
            if (groupByWebsite) {
                if (!first) {
                    sb.append(", website");
                } else {
                    sb.append("GROUP BY website");
                }
                first = false;
            }
            if (groupedByPointForWin) {
                if (!first) {
                    sb.append(", point_for_win");
                } else {
                    sb.append("GROUP BY point_for_win");
                }
            }
            sb.append(" ORDER BY version DESC;");
            pstmt = DatabaseUtil.getConnection().prepareStatement(sb.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                GroupedPlayedGame game = new GroupedPlayedGame();
                if (groupByVersion) {
                    game.setVersion(rs.getString("version"));
                }
                if (groupByOpponentName) {
                    game.setOpponentName(rs.getString("opponent_name"));
                }
                if (groupByWebsite) {
                    game.setWebsite(rs.getString("website"));
                }
                if (groupedByPointForWin) {
                    game.setPointForWin(rs.getInt("point_for_win"));
                }
                game.setWin(rs.getInt("win_me"));
                game.setLose(rs.getInt("win_opponent"));
                game.setStopped(rs.getInt("stopped"));
                games.add(game);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting grouped game games", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return games;
    }
}
