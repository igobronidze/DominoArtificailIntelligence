package ge.ai.domino.dao.opponentplay;

import ge.ai.domino.dao.DAOTestUtil;
import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.game.opponentplay.OpponentTilesWrapper;
import ge.ai.domino.domain.move.MoveType;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpponentPlayDAOTest {

	private static final String OPPONENT_PLAY_TABLE_NAME = "opponent_play";

	private static final int GAME_ID = 1;

	private static final String VERSION = "testVersion";

	private static final MoveType MOVE_TYPE = MoveType.ADD_FOR_OPPONENT;

	private static final Tile TILE = new Tile(5, 3);

	private static final List<OpponentTile> OPPONENT_TILES = Arrays.asList(new OpponentTile(3, 1, 0.54), new OpponentTile(6, 0, 0.05));

	private static final List<Integer> POSSIBLE_PLAY_NUMBERS = Arrays.asList(5, 3, 1);

	private static final double ASSERT_EQUAL_DELTA = 0.0001;

	private static OpponentPlayDAO opponentPlayDAO;

	@BeforeClass
	public static void init() {
		opponentPlayDAO = new OpponentPlayDAOImpl();
		DAOTestUtil.initDAIPropertiesFilePath();
	}

	@Test
	public void testAddAndGetOpponentPlays() {
		List<OpponentPlay> expectedOpponentPlays = getMockOpponentPlays();
		opponentPlayDAO.addOpponentPlays(expectedOpponentPlays);

		List<OpponentPlay> realOpponentPlays = opponentPlayDAO.getOpponentPlays(null, null);

		Assert.assertEquals(expectedOpponentPlays.size(), realOpponentPlays.size());
		for (int i = 0; i < expectedOpponentPlays.size(); i++) {
			equalOpponentPlays(expectedOpponentPlays.get(i), realOpponentPlays.get(i));
		}
	}

	@After
	public void cleanUp() throws Exception {
		String sql = String.format("DELETE FROM %s", OPPONENT_PLAY_TABLE_NAME);
		PreparedStatement pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
		pstmt.executeUpdate();
	}

	private void equalOpponentPlays(OpponentPlay expected, OpponentPlay real) {
		Assert.assertEquals(expected.getGameId(), real.getGameId());
		Assert.assertEquals(expected.getVersion(), real.getVersion());
		Assert.assertEquals(expected.getMoveType(), real.getMoveType());
		Assert.assertEquals(expected.getTile(), real.getTile());
		Assert.assertEquals(expected.getOpponentTiles().getOpponentTiles().size(), real.getOpponentTiles().getOpponentTiles().size());
		for (int i = 0; i < expected.getOpponentTiles().getOpponentTiles().size(); i++) {
			OpponentTile expectedOpponentTile = expected.getOpponentTiles().getOpponentTiles().get(i);
			OpponentTile realOpponentTile = real.getOpponentTiles().getOpponentTiles().get(i);
			Assert.assertEquals(expectedOpponentTile.getLeft(), realOpponentTile.getLeft());
			Assert.assertEquals(expectedOpponentTile.getRight(), realOpponentTile.getRight());
			Assert.assertEquals(expectedOpponentTile.getProbability(), realOpponentTile.getProbability(), ASSERT_EQUAL_DELTA);
		}
		Assert.assertEquals(expected.getPossiblePlayNumbers(), real.getPossiblePlayNumbers());
		for (int i = 0; i < expected.getPossiblePlayNumbers().size(); i++) {
			Assert.assertEquals(expected.getPossiblePlayNumbers().get(i), real.getPossiblePlayNumbers().get(i));
		}
	}

	private List<OpponentPlay> getMockOpponentPlays() {
		OpponentPlay opponentPlay = new OpponentPlay();
		opponentPlay.setGameId(GAME_ID);
		opponentPlay.setVersion(VERSION);
		opponentPlay.setMoveType(MOVE_TYPE);
		opponentPlay.setTile(TILE);
		OpponentTilesWrapper opponentTilesWrapper = new OpponentTilesWrapper();
		opponentTilesWrapper.setOpponentTiles(OPPONENT_TILES);
		opponentPlay.setOpponentTiles(opponentTilesWrapper);
		opponentPlay.setPossiblePlayNumbers(POSSIBLE_PLAY_NUMBERS);

		List<OpponentPlay> opponentPlays = new ArrayList<>();
		opponentPlays.add(opponentPlay);
		return opponentPlays;
	}
}
