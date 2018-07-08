package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TilesDetectorTest {

	private static final String IMAGE_PATH_SUFFIX = "test_images/src/domino_";

	private static final String IMAGE_EXTENSION = ".png";

	private static final int imageCount = 5;

	private static final Map<Integer, String> imagePathMap = new HashMap<>();

	private static final Map<Integer, List<Tile>> expectedTilesMap = new HashMap<>();

	private static final TilesDetector tilesDetector = new TilesDetector();

	@BeforeClass
	public static void init() {
		initPathMap();

		initExpectedTiles1();
		initExpectedTiles2();
		initExpectedTiles3();
		initExpectedTiles4();
		initExpectedTiles5();
	}

	@Test
	public void testGetTiles() {
		for (Map.Entry<Integer, String> entry : imagePathMap.entrySet()) {
			List<Tile> resultTiles = tilesDetector.getTiles(entry.getValue());
			List<Tile> expectedTiles = expectedTilesMap.get(entry.getKey());
			Assert.assertEquals(expectedTiles.size(), resultTiles.size());
			for (int i = 0; i < expectedTiles.size() ; i++) {
				Assert.assertEquals(expectedTiles.get(i), resultTiles.get(i));
			}
		}
	}

	private static void initPathMap() {
		for (int i = 1; i <= imageCount; i++) {
			imagePathMap.put(i, IMAGE_PATH_SUFFIX + i + IMAGE_EXTENSION);
		}
	}

	private static void initExpectedTiles1() {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 5));
		expectedTiles.add(new Tile(5, 1));
		expectedTiles.add(new Tile(5, 0));
		expectedTiles.add(new Tile(4, 4));
		expectedTiles.add(new Tile(4, 2));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(0, 0));
		expectedTilesMap.put(1, expectedTiles);
	}

	private static void initExpectedTiles2() {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(6, 3));
		expectedTiles.add(new Tile(5, 4));
		expectedTiles.add(new Tile(4, 1));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(2, 2));
		expectedTiles.add(new Tile(2, 1));
		expectedTiles.add(new Tile(1, 0));
		expectedTilesMap.put(2, expectedTiles);
	}

	private static void initExpectedTiles3() {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 3));
		expectedTiles.add(new Tile(3, 2));
		expectedTiles.add(new Tile(6, 6));
		expectedTiles.add(new Tile(6, 5));
		expectedTiles.add(new Tile(6, 4));
		expectedTiles.add(new Tile(4, 2));
		expectedTiles.add(new Tile(4, 0));
		expectedTilesMap.put(3, expectedTiles);
	}

	private static void initExpectedTiles4() {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 1));
		expectedTiles.add(new Tile(6, 6));
		expectedTiles.add(new Tile(6, 3));
		expectedTiles.add(new Tile(5, 4));
		expectedTiles.add(new Tile(4, 4));
		expectedTiles.add(new Tile(4, 3));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(3, 2));
		expectedTilesMap.put(4, expectedTiles);
	}

	private static void initExpectedTiles5() {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(4, 2));
		expectedTiles.add(new Tile(6, 6));
		expectedTiles.add(new Tile(6, 5));
		expectedTilesMap.put(5, expectedTiles);
	}
}
