package ge.ai.domino.dao.played;

import ge.ai.domino.common.params.playedgames.GetGroupedPlayedGamesParams;
import ge.ai.domino.common.params.playedgames.GetPlayedGamesParams;
import ge.ai.domino.dao.channel.ChannelDAO;
import ge.ai.domino.dao.channel.ChannelDAOImpl;
import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.dao.query.FilterCondition;
import ge.ai.domino.dao.query.QueryUtil;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.GroupedPlayedGame;
import ge.ai.domino.domain.played.PlayedGame;
import ge.ai.domino.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class PlayedGameDAOImpl implements PlayedGameDAO {

    private static final Logger logger = Logger.getLogger(PlayedGameDAOImpl.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String PLAYED_GAME_TABLE_NAME = "played_game";

    private static final String ID_COLUMN_NAME = "id";

    private static final String VERSION_COLUMN_NAME = "version";

    private static final String POINT_FOR_WIN_COLUMN_NAME = "point_for_win";

    private static final String OPPONENT_NAME_COLUMN_NAME = "opponent_name";

    private static final String OPPONENT_POINT_COLUMN_NAME = "opponent_point";

    private static final String CHANNEL_ID_COLUMN_NAME = "channel_id";

    private static final String RESULT_COLUMN_NAME = "result";

    private static final String GAME_HISTORY_COLUMN_NAME = "game_history";

    private static final String START_DATE_COLUMN_NAME = "start_date";

    private static final String START_TIME_COLUMN_NAME = "start_time";

    private static final String DATE_COLUMN_NAME = "date";

    private static final String TIME_COLUMN_NAME = "time";

    private static final String MY_POINT_COLUMN_NAME = "my_point";

    private static final String WIN_ME = "win_me";

    private static final String WIN_OPPONENT = "win_opponent";

    private static final String STOPPED = "stopped";

    private static final String LEVEL_COLUMN_NAME = "level";

    private final ChannelDAO channelDAO = new ChannelDAOImpl();

    private PreparedStatement pstmt;

    @Override
    public int addPlayedGame(PlayedGame game) {
        try {
            logger.info("Start addGame method");
            String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) VALUES (?,?,?,?,?,?,?,?);",
                    PLAYED_GAME_TABLE_NAME, VERSION_COLUMN_NAME, POINT_FOR_WIN_COLUMN_NAME, OPPONENT_NAME_COLUMN_NAME, CHANNEL_ID_COLUMN_NAME, RESULT_COLUMN_NAME, LEVEL_COLUMN_NAME, START_DATE_COLUMN_NAME, START_TIME_COLUMN_NAME);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
            pstmt.setString(1, game.getVersion());
            pstmt.setInt(2, game.getPointForWin());
            pstmt.setString(3, game.getOpponentName());
            pstmt.setInt(4, game.getChannel().getId());
            pstmt.setString(5, game.getResult().name());
            pstmt.setDouble(6, game.getLevel());
            pstmt.setDate(7, new Date(game.getStartDate().getTime()));
            pstmt.setTime(8, new Time(game.getStartDate().getTime()));
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
            logger.error("Error occurred while add game", ex);
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
    public List<PlayedGame> getPlayedGames(GetPlayedGamesParams params) {
        List<Channel> channels = channelDAO.getChannels();
        Map<Integer, Channel> channelsMap = channels.stream().collect(Collectors.toMap(Channel::getId, channel -> channel));

        List<PlayedGame> games = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE 1 = 1 ",
                    ID_COLUMN_NAME, VERSION_COLUMN_NAME, RESULT_COLUMN_NAME, DATE_COLUMN_NAME, TIME_COLUMN_NAME, START_DATE_COLUMN_NAME, START_TIME_COLUMN_NAME, MY_POINT_COLUMN_NAME,
                    OPPONENT_POINT_COLUMN_NAME, POINT_FOR_WIN_COLUMN_NAME, OPPONENT_NAME_COLUMN_NAME, CHANNEL_ID_COLUMN_NAME, LEVEL_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            if (!StringUtil.isEmpty(params.getVersion())) {
                QueryUtil.addFilter(sql, VERSION_COLUMN_NAME, params.getVersion(), FilterCondition.EQUAL, true);
            }
            if (params.getResult() != null) {
                QueryUtil.addFilter(sql, RESULT_COLUMN_NAME, params.getResult().name(), FilterCondition.EQUAL, true);
            }
            if (!StringUtil.isEmpty(params.getOpponentName())) {
                QueryUtil.addFilter(sql, OPPONENT_NAME_COLUMN_NAME, params.getOpponentName(), FilterCondition.EQUAL, true);
            }
            if (params.getChannelId() != null) {
                QueryUtil.addFilter(sql, CHANNEL_ID_COLUMN_NAME, String.valueOf(params.getChannelId()), FilterCondition.EQUAL, false);
            }
            if (params.getLevel() != null) {
                try {
                    int levelIntValue = Integer.parseInt(params.getLevel());
                    QueryUtil.addFilter(sql, LEVEL_COLUMN_NAME, String.valueOf(levelIntValue), FilterCondition.EQUAL, false);
                } catch (NumberFormatException ex) {
                    logger.warn("Can't parse level[" + params.getLevel() + "]");
                }
            }
            sql.append(" ORDER BY " + ID_COLUMN_NAME + " DESC");
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                PlayedGame game = new PlayedGame();
                game.setId(rs.getInt(ID_COLUMN_NAME));
                game.setVersion(rs.getString(VERSION_COLUMN_NAME));
                game.setResult(GameResult.valueOf(rs.getString(RESULT_COLUMN_NAME)));
                Date endDateFromDB = rs.getDate(DATE_COLUMN_NAME);
                Time endTimeFromDB = rs.getTime(TIME_COLUMN_NAME);
                Date startDateFromDB = rs.getDate(START_DATE_COLUMN_NAME);
                Time startTimeFromDB = rs.getTime(START_TIME_COLUMN_NAME);
                TimeZone tz = TimeZone.getDefault();
                if (endDateFromDB != null && endTimeFromDB != null) {
                    game.setEndDate(new java.util.Date(endDateFromDB.getTime() + endTimeFromDB.getTime() + tz.getOffset(new java.util.Date().getTime())));
                }
                if (startDateFromDB != null && startTimeFromDB != null) {
                    game.setStartDate(new java.util.Date(startDateFromDB.getTime() + startTimeFromDB.getTime() + tz.getOffset(new java.util.Date().getTime())));
                }
                game.setMyPoint(rs.getInt(MY_POINT_COLUMN_NAME));
                game.setOpponentPoint(rs.getInt(OPPONENT_POINT_COLUMN_NAME));
                game.setPointForWin(rs.getInt(POINT_FOR_WIN_COLUMN_NAME));
                game.setOpponentName(rs.getString(OPPONENT_NAME_COLUMN_NAME));
                game.setChannel(channelsMap.get(rs.getInt(CHANNEL_ID_COLUMN_NAME)));
                game.setLevel(rs.getDouble(LEVEL_COLUMN_NAME));
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
    public GameHistory getGameHistory(int gameId) throws DAIException {
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT %s FROM %s WHERE 1 = 1 ", GAME_HISTORY_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            QueryUtil.addFilter(sql, ID_COLUMN_NAME, String.valueOf(gameId), FilterCondition.EQUAL, false);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String history = rs.getString(GAME_HISTORY_COLUMN_NAME);
                return GameHistoryMarshaller.unmarshallGameHistory(history);
            } else {
                logger.error(String.format("There is no played game gameId[%s]", gameId));
                throw new DAIException("noPlayedGame");
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game history id[" + gameId + "]", ex);
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
    public List<GroupedPlayedGame> getGroupedPlayedGames(GetGroupedPlayedGamesParams params) {
        List<Channel> channels = channelDAO.getChannels();
        Map<Integer, Channel> channelsMap = channels.stream().collect(Collectors.toMap(Channel::getId, channel -> channel));

        List<GroupedPlayedGame> games = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder("SELECT ");
            boolean first = true;
            if (params.isGroupByVersion()) {
                sb.append(VERSION_COLUMN_NAME);
                first = false;
            }
            if (params.isGroupByChannel()) {
                QueryUtil.addParameter(sb, CHANNEL_ID_COLUMN_NAME, !first);
                first = false;
            }
            if (params.isGroupedByPointForWin()) {
                QueryUtil.addParameter(sb, POINT_FOR_WIN_COLUMN_NAME, !first);
                first = false;
            }
            if (params.isGroupByLevel()) {
                QueryUtil.addParameter(sb, LEVEL_COLUMN_NAME, !first);
                first = false;
            }
            if (params.isGroupByDate()) {
                QueryUtil.addParameter(sb, DATE_COLUMN_NAME, !first);
                first = false;
            }
            if (!first) {
                sb.append(", ");
            }

            sb.append(" sum(case when " + RESULT_COLUMN_NAME + " = '")
                    .append(GameResult.I_WIN).append("' then 1 else 0 end) as " + WIN_ME + ", sum(case when " + RESULT_COLUMN_NAME + " = '")
                    .append(GameResult.OPPONENT_WIN)
                    .append("' then 1 else 0 end) as " + WIN_OPPONENT + ", sum(case when " + RESULT_COLUMN_NAME + " = '")
                    .append(GameResult.STOPPED)
                    .append("' then 1 else 0 end) as " + STOPPED + " FROM " + PLAYED_GAME_TABLE_NAME + " ");

            sb.append(" WHERE 1 = 1 ");
            if (!StringUtil.isEmpty(params.getVersion())) {
                QueryUtil.addFilter(sb, VERSION_COLUMN_NAME, params.getVersion(), FilterCondition.EQUAL, true);
            }
            if (params.getChannelId() != null) {
                QueryUtil.addFilter(sb, CHANNEL_ID_COLUMN_NAME, String.valueOf(params.getChannelId()), FilterCondition.EQUAL, false);
            }
            if (params.getPointForWin() != null) {
                QueryUtil.addFilter(sb, POINT_FOR_WIN_COLUMN_NAME, String.valueOf(params.getPointForWin()), FilterCondition.EQUAL, false);
            }
            if (params.getLevel() != null) {
                QueryUtil.addFilter(sb, LEVEL_COLUMN_NAME, String.valueOf(params.getLevel()), FilterCondition.EQUAL, false);
            }
            if (params.getFromDate() != null) {
                QueryUtil.addFilter(sb, DATE_COLUMN_NAME, params.getFromDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), FilterCondition.GREAT_OR_EQUAL, true);
            }
            if (params.getToDate() != null) {
                QueryUtil.addFilter(sb, DATE_COLUMN_NAME, params.getToDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), FilterCondition.LESS_OR_EQUAL, true);
            }

            first = true;
            if (params.isGroupByVersion()) {
                sb.append("GROUP BY ").append(VERSION_COLUMN_NAME);
                first = false;
            }
            if (params.isGroupByChannel()) {
                if (!first) {
                    sb.append(", ").append(CHANNEL_ID_COLUMN_NAME);
                } else {
                    sb.append("GROUP BY ").append(CHANNEL_ID_COLUMN_NAME);
                }
                first = false;
            }
            if (params.isGroupedByPointForWin()) {
                if (!first) {
                    sb.append(", ").append(POINT_FOR_WIN_COLUMN_NAME);
                } else {
                    sb.append("GROUP BY ").append(POINT_FOR_WIN_COLUMN_NAME);
                }
            }
            if (params.isGroupByLevel()) {
                if (!first) {
                    sb.append(", ").append(LEVEL_COLUMN_NAME);
                } else {
                    sb.append("GROUP BY ").append(LEVEL_COLUMN_NAME);
                }
            }
            if (params.isGroupByDate()) {
                if (!first) {
                    sb.append(", ").append(DATE_COLUMN_NAME);
                } else {
                    sb.append("GROUP BY ").append(DATE_COLUMN_NAME);
                }
            }

            pstmt = ConnectionUtil.getConnection().prepareStatement(sb.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                GroupedPlayedGame game = new GroupedPlayedGame();
                if (params.isGroupByVersion()) {
                    game.setVersion(rs.getString(VERSION_COLUMN_NAME));
                }
                if (params.isGroupByChannel()) {
                    game.setChannel(channelsMap.get(rs.getInt(CHANNEL_ID_COLUMN_NAME)));
                }
                if (params.isGroupedByPointForWin()) {
                    game.setPointForWin(rs.getInt(POINT_FOR_WIN_COLUMN_NAME));
                }
                if (params.isGroupByLevel()) {
                    game.setLevel(rs.getDouble(LEVEL_COLUMN_NAME));
                }
                if (params.isGroupByDate()) {
                    game.setDate(rs.getDate(DATE_COLUMN_NAME));
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

    @Override
    public GameProperties getGameProperties(int gameId) throws DAIException {
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT %s, %s FROM %s WHERE 1 = 1 ", POINT_FOR_WIN_COLUMN_NAME, CHANNEL_ID_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            QueryUtil.addFilter(sql, ID_COLUMN_NAME, String.valueOf(gameId), FilterCondition.EQUAL, false);
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                GameProperties gameProperties = new GameProperties();
                gameProperties.setPointsForWin(rs.getInt(POINT_FOR_WIN_COLUMN_NAME));

                List<Channel> channels = channelDAO.getChannels();
                Map<Integer, Channel> channelsMap = channels.stream().collect(Collectors.toMap(Channel::getId, channel -> channel));
                gameProperties.setChannel(channelsMap.get(rs.getInt(CHANNEL_ID_COLUMN_NAME)));

                return gameProperties;
            } else {
                logger.error(String.format("There is no played game gameId[%s]", gameId));
                throw new DAIException("noPlayedGame");
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game property id[" + gameId + "]", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return null;
    }

    @Override
    public List<Integer> getAllPlayedGame() {
        List<Integer> result = new ArrayList<>();
        try {
            pstmt = ConnectionUtil.getConnection().prepareStatement(String.format("SELECT %s FROM %s WHERE 1 = 1 ", ID_COLUMN_NAME, PLAYED_GAME_TABLE_NAME));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt(ID_COLUMN_NAME));
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting all played game", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return result;
    }
}
