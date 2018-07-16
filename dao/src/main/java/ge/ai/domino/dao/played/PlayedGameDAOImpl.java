package ge.ai.domino.dao.played;

import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.dao.query.FilterCondition;
import ge.ai.domino.dao.query.QueryUtil;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class PlayedGameDAOImpl implements PlayedGameDAO {

    private Logger logger = Logger.getLogger(PlayedGameDAOImpl.class);

    private static final String PLAYED_GAME_TABLE_NAME = "played_game";

    private static final String ID_COLUMN_NAME = "id";

    private static final String VERSION_COLUMN_NAME = "version";

    private static final String POINT_FOR_WIN_COLUMN_NAME = "point_for_win";

    private static final String OPPONENT_NAME_COLUMN_NAME = "opponent_name";

    private static final String OPPONENT_POINT_COLUMN_NAME = "opponent_point";

    private static final String WEBSITE_COLUMN_NAME = "website";

    private static final String RESULT_COLUMN_NAME = "result";

    private static final String GAME_HISTORY_COLUMN_NAME = "game_history";

    private static final String DATE_COLUMN_NAME = "date";

    private static final String TIME_COLUMN_NAME = "time";

    private static final String MY_POINT_COLUMN_NAME = "my_point";

    private static final String WIN_ME = "win_me";

    private static final String WIN_OPPONENT = "win_opponent";

    private static final String STOPPED = "stopped";

    private PreparedStatement pstmt;

    @Override
    public int addPlayedGame(PlayedGame game) {
        try {
            logger.info("Start addGame method");
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?,?,?,?,?);",
                    PLAYED_GAME_TABLE_NAME, VERSION_COLUMN_NAME, POINT_FOR_WIN_COLUMN_NAME, OPPONENT_NAME_COLUMN_NAME, WEBSITE_COLUMN_NAME, RESULT_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, game.getVersion());
            pstmt.setInt(2, game.getPointForWin());
            pstmt.setString(3, game.getOpponentName());
            pstmt.setString(4, game.getWebsite());
            pstmt.setString(5, game.getResult().name());
            pstmt.executeUpdate();

            String maxId = "max_id";
            String idSql = String.format("SELECT MAX(%s) AS %s FROM %s", ID_COLUMN_NAME, maxId, PLAYED_GAME_TABLE_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(idSql);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int id = rs.getInt(maxId);
            logger.info("Added game id [" + id + "]");
            return id;
        } catch (SQLException ex) {
            logger.error("Error occurred while add game game", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return 0;
    }

    @Override
    public void updatePlayedGame(PlayedGame game) {
        try {
            logger.info("Start editGame method id[" + game.getId() + "]");
            String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                    PLAYED_GAME_TABLE_NAME, RESULT_COLUMN_NAME, DATE_COLUMN_NAME, TIME_COLUMN_NAME, MY_POINT_COLUMN_NAME, OPPONENT_POINT_COLUMN_NAME, GAME_HISTORY_COLUMN_NAME, OPPONENT_NAME_COLUMN_NAME, ID_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, game.getResult().name());
            pstmt.setDate(2, new Date(game.getEndDate().getTime()));
            pstmt.setTime(3, new Time(game.getEndDate().getTime()));
            pstmt.setInt(4, game.getMyPoint());
            pstmt.setInt(5, game.getOpponentPoint());
            pstmt.setString(6, GameHistoryMarshaller.getMarshalledHistory(game.getGameHistory()));
            pstmt.setString(7, game.getOpponentName());
            pstmt.setInt(8, game.getId());
            pstmt.executeUpdate();
            logger.info("Updated game id[" + game.getId() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while edit game", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
    }

    @Override
    public List<PlayedGame> getPlayedGames(String version, GameResult result, String opponentName, String website) {
        List<PlayedGame> games = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE 1 = 1 ",
                    ID_COLUMN_NAME, VERSION_COLUMN_NAME, RESULT_COLUMN_NAME, DATE_COLUMN_NAME, TIME_COLUMN_NAME, MY_POINT_COLUMN_NAME, OPPONENT_POINT_COLUMN_NAME,
                    POINT_FOR_WIN_COLUMN_NAME, OPPONENT_NAME_COLUMN_NAME, WEBSITE_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            if (!StringUtil.isEmpty(version)) {
                QueryUtil.addFilter(sql, VERSION_COLUMN_NAME, version, FilterCondition.EQUAL, true);
            }
            if (result != null) {
                QueryUtil.addFilter(sql, RESULT_COLUMN_NAME, result.name(), FilterCondition.EQUAL, true);
            }
            if (!StringUtil.isEmpty(opponentName)) {
                QueryUtil.addFilter(sql, OPPONENT_NAME_COLUMN_NAME, opponentName, FilterCondition.EQUAL, true);
            }
            if (!StringUtil.isEmpty(website)) {
                QueryUtil.addFilter(sql, WEBSITE_COLUMN_NAME, website, FilterCondition.EQUAL, true);
            }
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                PlayedGame game = new PlayedGame();
                game.setId(rs.getInt(ID_COLUMN_NAME));
                game.setVersion(rs.getString(VERSION_COLUMN_NAME));
                game.setResult(GameResult.valueOf(rs.getString(RESULT_COLUMN_NAME)));
                Date dateFromDB = rs.getDate(DATE_COLUMN_NAME);
                Time timeFromDB = rs.getTime(TIME_COLUMN_NAME);
                if (dateFromDB != null && timeFromDB != null) {
                    TimeZone tz = TimeZone.getDefault();
                    game.setEndDate(new java.util.Date(dateFromDB.getTime() + timeFromDB.getTime() + tz.getOffset(new java.util.Date().getTime())));
                }
                game.setMyPoint(rs.getInt(MY_POINT_COLUMN_NAME));
                game.setOpponentPoint(rs.getInt(OPPONENT_POINT_COLUMN_NAME));
                game.setPointForWin(rs.getInt(POINT_FOR_WIN_COLUMN_NAME));
                game.setOpponentName(rs.getString(OPPONENT_NAME_COLUMN_NAME));
                game.setWebsite(rs.getString(WEBSITE_COLUMN_NAME));
                games.add(game);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game games", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return games;
    }

    @Override
    public GameHistory getGameHistory(int gameId) {
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT %s FROM %s WHERE 1 = 1 ", GAME_HISTORY_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            QueryUtil.addFilter(sql, ID_COLUMN_NAME, String.valueOf(gameId), FilterCondition.EQUAL, false);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String history = rs.getString(GAME_HISTORY_COLUMN_NAME);
                return GameHistoryMarshaller.unmarshallGameHistory(history);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game history", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return null;
    }

    @Override
    public void deletePlayedGame(int gameId) {
        try {
            logger.info("Start deleteGame method id[" + gameId + "]");
            String sql = String.format("DELETE FROM %s WHERE %s = ?", PLAYED_GAME_TABLE_NAME, ID_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
            logger.info("Deleted game id[" + gameId + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while delete game game", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<GroupedPlayedGame> getGroupedPlayedGames(boolean groupByVersion, boolean groupByOpponentName, boolean groupByWebsite, boolean groupedByPointForWin) {
        List<GroupedPlayedGame> games = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder("SELECT ");
            boolean first = true;
            if (groupByVersion) {
                sb.append(VERSION_COLUMN_NAME);
                first = false;
            }
            if (groupByOpponentName) {
                QueryUtil.addParameter(sb, OPPONENT_NAME_COLUMN_NAME, !first);
                first = false;
            }
            if (groupByWebsite) {
                QueryUtil.addParameter(sb, WEBSITE_COLUMN_NAME, !first);
                first = false;
            }
            if (groupedByPointForWin) {
                QueryUtil.addParameter(sb, POINT_FOR_WIN_COLUMN_NAME, !first);
                first = false;
            }
            if (!first) {
                sb.append(", ");
            }
            sb.append(" sum(case when " + RESULT_COLUMN_NAME + " = '").append(GameResult.I_WIN).append("' then 1 else 0 end) as " + WIN_ME + ", sum(case when " + RESULT_COLUMN_NAME + " = '")
                    .append(GameResult.OPPONENT_WIN).append("' then 1 else 0 end) as " + WIN_OPPONENT + ", sum(case when " + RESULT_COLUMN_NAME + " = '").append(GameResult.STOPPED)
                    .append("' then 1 else 0 end) as " + STOPPED + " FROM " + PLAYED_GAME_TABLE_NAME + " ");
            first = true;
            if (groupByVersion) {
                sb.append("GROUP BY ").append(VERSION_COLUMN_NAME);
                first = false;
            }
            if (groupByOpponentName) {
                if (!first) {
                    sb.append(", ").append(OPPONENT_NAME_COLUMN_NAME);
                } else {
                    sb.append("GROUP BY ").append(OPPONENT_NAME_COLUMN_NAME);
                }
                first = false;
            }
            if (groupByWebsite) {
                if (!first) {
                    sb.append(", ").append(WEBSITE_COLUMN_NAME);
                } else {
                    sb.append("GROUP BY ").append(WEBSITE_COLUMN_NAME);
                }
                first = false;
            }
            if (groupedByPointForWin) {
                if (!first) {
                    sb.append(", ").append(POINT_FOR_WIN_COLUMN_NAME);
                } else {
                    sb.append("GROUP BY ").append(POINT_FOR_WIN_COLUMN_NAME);
                }
            }
            pstmt = ConnectionUtil.getConnection().prepareStatement(sb.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                GroupedPlayedGame game = new GroupedPlayedGame();
                if (groupByVersion) {
                    game.setVersion(rs.getString(VERSION_COLUMN_NAME));
                }
                if (groupByOpponentName) {
                    game.setOpponentName(rs.getString(OPPONENT_NAME_COLUMN_NAME));
                }
                if (groupByWebsite) {
                    game.setWebsite(rs.getString(WEBSITE_COLUMN_NAME));
                }
                if (groupedByPointForWin) {
                    game.setPointForWin(rs.getInt(POINT_FOR_WIN_COLUMN_NAME));
                }
                game.setWin(rs.getInt(WIN_ME));
                game.setLose(rs.getInt(WIN_OPPONENT));
                game.setStopped(rs.getInt(STOPPED));
                games.add(game);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting grouped game games", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return games;
    }

    @Override
    public int getLastPlayedGameId() {
        try {
            pstmt = ConnectionUtil.getConnection().prepareStatement(String.format("SELECT max(%s) FROM %s", ID_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting last played game id", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return 0;
    }

    @Override
    public List<GameInfo> getGameInfosBeforeId(long gameId) {
        List<GameInfo> gameInfos = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT %s, %s, %s FROM %s WHERE 1 = 1 ",
                    ID_COLUMN_NAME, MY_POINT_COLUMN_NAME, OPPONENT_POINT_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            QueryUtil.addFilter(sql, ID_COLUMN_NAME, String.valueOf(gameId), FilterCondition.GREAT, false);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                GameInfo gameInfo = new GameInfo();
                gameInfo.setGameId(rs.getInt(ID_COLUMN_NAME));
                gameInfo.setMyPoint(rs.getInt(MY_POINT_COLUMN_NAME));
                gameInfo.setOpponentPoint(rs.getInt(OPPONENT_POINT_COLUMN_NAME));
                gameInfos.add(gameInfo);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game infos before id", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return gameInfos;
    }

    @Override
    public void updateGameInfo(GameInfo gameInfo) {
        try {
            logger.info("Start updateGameInfo method id[" + gameInfo.getGameId() + "]");
            String sql = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?",
                    PLAYED_GAME_TABLE_NAME, MY_POINT_COLUMN_NAME, OPPONENT_POINT_COLUMN_NAME, ID_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
            pstmt.setInt(1, gameInfo.getMyPoint());
            pstmt.setInt(2, gameInfo.getOpponentPoint());
            pstmt.setInt(3, gameInfo.getGameId());
            pstmt.executeUpdate();
            logger.info("Updated game info id[" + gameInfo.getGameId() + "]");
        } catch (SQLException ex) {
            logger.error("Error occurred while edit game info", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
    }
}
