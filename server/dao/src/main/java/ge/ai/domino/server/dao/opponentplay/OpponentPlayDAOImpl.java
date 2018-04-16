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
import java.util.List;

public class OpponentPlayDAOImpl implements OpponentPlayDAO {

    private Logger logger = Logger.getLogger(OpponentPlayDAOImpl.class);

    private PreparedStatement pstmt;

    @Override
    public void addOpponentPlays(List<OpponentPlay> opponentPlays) {
        try {
            logger.info("Start addOpponentPlays method count[" + opponentPlays.size() + "]");
            for (OpponentPlay opponentPlay : opponentPlays) {
                String sql = "INSERT INTO opponent_play (game_id, version, move_type, tile, opponent_tiles) VALUES (?,?,?,?,?);";
                pstmt = DatabaseUtil.getConnection().prepareStatement(sql);
                pstmt.setInt(1, opponentPlay.getGameId());
                pstmt.setString(2, opponentPlay.getVersion());
                pstmt.setString(3, opponentPlay.getMoveType().name());
                pstmt.setString(4, opponentPlay.getTile().toString());
                pstmt.setString(5, OpponentTilesMarshaller.getMarshalledTiles(opponentPlay.getOpponentTiles()));
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
            String sql = "SELECT id, game_id, version, move_type, tile, opponent_tiles FROM opponent_play WHERE 1 = 1 ";
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
                opponentPlays.add(opponentPlay);
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while getting opponent plays", ex);
        } finally {
            DatabaseUtil.closeConnection();
        }
        return opponentPlays;
    }

    private Tile parseTile(String tileStr) {
        String[] parts = tileStr.split(Tile.DELIMITER);
        Tile tile = new Tile(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        return tile;
    }
}
