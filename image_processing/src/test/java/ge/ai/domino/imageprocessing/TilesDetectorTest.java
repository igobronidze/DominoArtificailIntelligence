package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TilesDetectorTest {

	private static final String SRC_IMAGE_PATH_1 = "test_images/src/domino_1.png";

	private static final String SRC_IMAGE_PATH_2 = "test_images/src/domino_2.png";

	private static final String SRC_IMAGE_PATH_3 = "test_images/src/domino_3.png";

	private static final List<Tile> EXPECTED_TILES_1 = new ArrayList<>();

	private static final List<Tile> EXPECTED_TILES_2 = new ArrayList<>();

	private static final List<Tile> EXPECTED_TILES_3 = new ArrayList<>();

	private static final TilesDetector tilesDetector = new TilesDetector();

	@BeforeClass
	public static void init() {
		EXPECTED_TILES_1.add(new Tile(5, 5));
		EXPECTED_TILES_1.add(new Tile(5, 1));
		EXPECTED_TILES_1.add(new Tile(5, 0));
		EXPECTED_TILES_1.add(new Tile(4, 4));
		EXPECTED_TILES_1.add(new Tile(4, 2));
		EXPECTED_TILES_1.add(new Tile(3, 3));
		EXPECTED_TILES_1.add(new Tile(0, 0));

		EXPECTED_TILES_2.add(new Tile(6, 3));
		EXPECTED_TILES_2.add(new Tile(5, 4));
		EXPECTED_TILES_2.add(new Tile(4, 1));
		EXPECTED_TILES_2.add(new Tile(3, 3));
		EXPECTED_TILES_2.add(new Tile(2, 2));
		EXPECTED_TILES_2.add(new Tile(2, 1));
		EXPECTED_TILES_2.add(new Tile(1, 0));

		EXPECTED_TILES_3.add(new Tile(5, 3));
		EXPECTED_TILES_3.add(new Tile(3, 2));
		EXPECTED_TILES_3.add(new Tile(6, 6));
		EXPECTED_TILES_3.add(new Tile(6, 5));
		EXPECTED_TILES_3.add(new Tile(6, 4));
		EXPECTED_TILES_3.add(new Tile(4, 2));
		EXPECTED_TILES_3.add(new Tile(4, 0));
	}

	@Test
	public void testGetTiles() {
		List<Tile> resultTiles1 = tilesDetector.getTiles(SRC_IMAGE_PATH_1);
		Assert.assertEquals(EXPECTED_TILES_1.size(), resultTiles1.size());
		for (int i = 0; i < EXPECTED_TILES_1.size() ; i++) {
			Assert.assertEquals(EXPECTED_TILES_1.get(i), resultTiles1.get(i));
		}

		List<Tile> resultTiles2 = tilesDetector.getTiles(SRC_IMAGE_PATH_2);
		Assert.assertEquals(EXPECTED_TILES_2.size(), resultTiles2.size());
		for (int i = 0; i < EXPECTED_TILES_2.size() ; i++) {
			Assert.assertEquals(EXPECTED_TILES_2.get(i), resultTiles2.get(i));
		}

		List<Tile> resultTiles3 = tilesDetector.getTiles(SRC_IMAGE_PATH_3);
		Assert.assertEquals(EXPECTED_TILES_3.size(), resultTiles3.size());
		for (int i = 0; i < EXPECTED_TILES_3.size() ; i++) {
			Assert.assertEquals(EXPECTED_TILES_3.get(i), resultTiles3.get(i));
		}
	}
}
