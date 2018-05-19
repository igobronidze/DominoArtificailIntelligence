package ge.ai.domino.server.dao.opponentplay;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.server.dao.DatabaseUtil;
import ge.ai.domino.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpponentPlayDAOImpl implements OpponentPlayDAO {

    private Logger logger = Logger.getLogger(OpponentPlayDAOImpl.class);

    private static final String POSSIBLE_PLAY_NUMBERS_DELIMITER = ",";

    private PreparedStatement pstmt;

    @Override
    public void addOpponentPlays(List<OpponentPlay> opponentPlays) {
        try {
            logger.info("Start addOpponentPlays method count[" + opponentPlays.size() + "]");
            for (OpponentPlay opponentPlay : opponentPlays) {
                String sql = "INSERT INTO opponent_play (game_id, version, move_type, tile, opponent_tiles, possible_play_numbers) VALUES (?,?,?,?,?,?);";
                pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
                pstmt.setInt(1, opponentPlay.getGameId());
                pstmt.setString(2, opponentPlay.getVersion());
                pstmt.setString(3, opponentPlay.getMoveType().name());
                pstmt.setString(4, opponentPlay.getTile().toString());
                pstmt.setString(5, OpponentTilesMarshaller.getMarshalledTiles(opponentPlay.getOpponentTiles()));
                pstmt.setString(6, joinPossiblePlayNumbers(opponentPlay.getPossiblePlayNumbers()));
                pstmt.executeUpdate();
            }
            logger.info("Added opponent plays");
        } catch (SQLException ex) {
            logger.error("Error occurred while add opponent plays", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
    }

    @Override
    public List<OpponentPlay> getOpponentPlays(String version, Integer gameId) {
        List<OpponentPlay> opponentPlays = new ArrayList<>();
        try {
            String sql = "SELECT * FROM opponent_play WHERE 1 = 1 ";
            if (!StringUtil.isEmpty(version)) {
                sql += "AND version = '" + version + "' ";
            }
            if (gameId != null) {
                sql += "AND game_id = '" + gameId + "' ";
            }
            pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OpponentPlay opponentPlay = new OpponentPlay();
                opponentPlay.setId(rs.getInt("id"));
                opponentPlay.setVersion(rs.getString("version"));
                opponentPlay.setGameId(rs.getInt("game_id"));
                opponentPlay.setMoveType(MoveType.valueOf(rs.getString("move_type")));
                opponentPlay.setTile(parseTile(rs.getString("tile")));
                opponentPlay.setOpponentTiles(OpponentTilesMarshaller.unmarshallOpponentTiles(rs.getString("opponent_tiles")));
                opponentPlay.setPossiblePlayNumbers(splitPossiblePlayNumbers(rs.getString("possible_play_numbers")));
                opponentPlays.add(opponentPlay);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting opponent plays", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return opponentPlays;
    }

    private String joinPossiblePlayNumbers(List<Integer> possiblePlayNumbers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < possiblePlayNumbers.size(); i++) {
            sb.append(possiblePlayNumbers.get(i));
            if (i != possiblePlayNumbers.size() - 1) {
                sb.append(POSSIBLE_PLAY_NUMBERS_DELIMITER);
            }
        }
        return sb.toString();
    }

    private List<Integer> splitPossiblePlayNumbers(String text) {
        List<Integer> result = new ArrayList<>();
        if (text != null && !text.isEmpty()) {
            for (String str : text.split(POSSIBLE_PLAY_NUMBERS_DELIMITER)) {
                result.add(Integer.parseInt(str));
            }
        }
        return result;
    }

    private Tile parseTile(String tileStr) {
        String[] parts = tileStr.split(Tile.DELIMITER);
        return new Tile(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}
