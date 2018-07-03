package ge.ai.domino.server.dao.opponentplay;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.server.dao.connection.ConnectionUtil;
import ge.ai.domino.server.dao.query.FilterCondition;
import ge.ai.domino.server.dao.query.QueryUtil;
import ge.ai.domino.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OpponentPlayDAOImpl implements OpponentPlayDAO {

    private Logger logger = Logger.getLogger(OpponentPlayDAOImpl.class);

    private static final String POSSIBLE_PLAY_NUMBERS_DELIMITER = ",";

    private static final String OPPONENT_PLAY_TABLE_NAME = "opponent_play";

    private static final String ID_COLUMN_NAME = "id";

    private static final String GAME_ID_COLUMN_NAME = "game_id";

    private static final String VERSION_COLUMN_NAME = "version";

    private static final String MOVE_TYPE_COLUMN_NAME = "move_type";

    private static final String TILE_COLUMN_NAME = "tile";

    private static final String OPPONENT_TILES_COLUMN_NAME = "opponent_tiles";

    private static final String POSSIBLE_PLAY_NUMBERS_COLUMN_NAME = "possible_play_numbers";

    private PreparedStatement pstmt;

    @Override
    public void addOpponentPlays(List<OpponentPlay> opponentPlays) {
        try {
            logger.info("Start addOpponentPlays method count[" + opponentPlays.size() + "]");
            for (OpponentPlay opponentPlay : opponentPlays) {
                String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?,?,?,?,?,?);",
                        OPPONENT_PLAY_TABLE_NAME, GAME_ID_COLUMN_NAME, VERSION_COLUMN_NAME, MOVE_TYPE_COLUMN_NAME, TILE_COLUMN_NAME, OPPONENT_TILES_COLUMN_NAME, POSSIBLE_PLAY_NUMBERS_COLUMN_NAME);
                pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
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
            ConnectionUtil.closeConnection();
        }
    }

    @Override
    public List<OpponentPlay> getOpponentPlays(String version, Integer gameId) {
        List<OpponentPlay> opponentPlays = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(String.format("SELECT * FROM %s WHERE 1 = 1 ", OPPONENT_PLAY_TABLE_NAME));
            if (!StringUtil.isEmpty(version)) {
                QueryUtil.addFilter(sql, VERSION_COLUMN_NAME, version, FilterCondition.EQUAL, true);
            }
            if (gameId != null) {
                QueryUtil.addFilter(sql, GAME_ID_COLUMN_NAME, String.valueOf(gameId), FilterCondition.EQUAL, true);
            }
            pstmt = ConnectionUtil.getConnection().prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OpponentPlay opponentPlay = new OpponentPlay();
                opponentPlay.setId(rs.getInt(ID_COLUMN_NAME));
                opponentPlay.setVersion(rs.getString(VERSION_COLUMN_NAME));
                opponentPlay.setGameId(rs.getInt(GAME_ID_COLUMN_NAME));
                opponentPlay.setMoveType(MoveType.valueOf(rs.getString(MOVE_TYPE_COLUMN_NAME)));
                opponentPlay.setTile(parseTile(rs.getString(TILE_COLUMN_NAME)));
                opponentPlay.setOpponentTiles(OpponentTilesMarshaller.unmarshallOpponentTiles(rs.getString(OPPONENT_TILES_COLUMN_NAME)));
                opponentPlay.setPossiblePlayNumbers(splitPossiblePlayNumbers(rs.getString(POSSIBLE_PLAY_NUMBERS_COLUMN_NAME)));
                opponentPlays.add(opponentPlay);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting opponent plays", ex);
        } finally {
            ConnectionUtil.closeConnection();
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
