package ge.ai.domino.console.debug.operation.db;

import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.played.GameResult;
import ge.ai.domino.domain.played.PlayedGame;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

public class PlayedGameInsertOperation implements GameDebuggerOperation {

    private static Logger logger = Logger.getLogger(PlayedGameInsertOperation.class);

    private static PreparedStatement pstmt;

    @Override
    public void process(Scanner scanner) {
        List<PlayedGame> playedGames = getPlayedGames();
        insetGames(playedGames);
    }

    private void insetGames(List<PlayedGame> games) {
        for (PlayedGame game : games) {
            try {
                String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        "played_game",
                        "id", "version", "result", "date", "time", "my_point", "opponent_point", "point_for_win", "opponent_name", "game_history", "channel_id", "level");

                pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
                pstmt.setInt(1, game.getId());
                pstmt.setString(2, game.getVersion());
                pstmt.setString(3, game.getResult().name());
                pstmt.setDate(4, new Date(game.getEndDate().getTime()));
                pstmt.setTime(5, new Time(game.getEndDate().getTime()));
                pstmt.setInt(6, game.getMyPoint());
                pstmt.setInt(7, game.getOpponentPoint());
                pstmt.setInt(8, game.getPointForWin());
                pstmt.setString(9, game.getOpponentName());
                pstmt.setString(10, game.getMarshaledGameHistory());
                pstmt.setInt(11, game.getChannel().getId());
                pstmt.setDouble(12, game.getLevel());
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                logger.error("Error occurred while getting game games", ex);
            } finally {
                ConnectionUtil.closeConnection();
            }
        }
    }

    private List<PlayedGame> getPlayedGames() {
        List<PlayedGame> games = new ArrayList<>();

        StringBuilder sql = new StringBuilder(String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE 1 = 1 ",
                "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10", "c11", "domino_prod_public_played_game"));

        try {
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                PlayedGame game = new PlayedGame();
                game.setId(rs.getInt("c1") - 331);
                game.setVersion(rs.getString("c2"));
                game.setResult(GameResult.valueOf(rs.getString("c3")));
                Date dateFromDB = rs.getDate("c4");
                Time timeFromDB = rs.getTime("c5");
                if (dateFromDB != null && timeFromDB != null) {
                    TimeZone tz = TimeZone.getDefault();
                    game.setEndDate(new java.util.Date(dateFromDB.getTime() + timeFromDB.getTime() + tz.getOffset(new java.util.Date().getTime())));
                }
                game.setMyPoint(rs.getInt("c6"));
                game.setOpponentPoint(rs.getInt("c7"));
                game.setPointForWin(rs.getInt("c8"));
                game.setOpponentName(rs.getString("c9"));
                game.setMarshaledGameHistory(rs.getString("c10"));
                Channel channel = new Channel();
                channel.setId(rs.getInt("c11"));
                game.setChannel(channel);
                game.setLevel(0.0);

                games.add(game);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting game games", ex);
        } finally {
            ConnectionUtil.closeConnection();
        }
        return games;
    }
}
