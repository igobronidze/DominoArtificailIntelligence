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

	private static final String BETLIVE_IMAGE_PATH_SUFFIX = "test_images/src/betlive/domino_";

	private static final String LIDERBET_IMAGE_PATH_SUFFIX = "test_images/src/liderbet/domino_";

	private static final String IMAGE_EXTENSION = ".png";

	private static final int betliveImageCount = 7;

	private static final int liderbetImageCount = 5;

	private static final Map<Integer, String> betliveImagePathMap = new HashMap<>();

	private static final Map<Integer, String> liderbetImagePathMap = new HashMap<>();

	private static final Map<Integer, List<Tile>> betliveExpectedTilesMap = new HashMap<>();

	private static final Map<Integer, List<Tile>> liderbetExpectedTilesMap = new HashMap<>();

	private static final TilesDetector tilesDetector = new TilesDetector();

	@BeforeClass
	public static void init() {
		initBetlivePathMap();
		initLiderbetPathMap();

		initBetliveExpectedTiles();
		initLiderbetExpectedTiles();
	}

	@Test
	public void testGetTilesForBetlive() {
		for (Map.Entry<Integer, String> entry : betliveImagePathMap.entrySet()) {
			List<Tile> resultTiles = tilesDetector.getTiles(entry.getValue(), getTilesDetectorParamsForBetlive());
			List<Tile> expectedTiles = betliveExpectedTilesMap.get(entry.getKey());
			Assert.assertEquals(expectedTiles.size(), resultTiles.size());
			for (int i = 0; i < expectedTiles.size() ; i++) {
				Assert.assertEquals(expectedTiles.get(i), resultTiles.get(i));
			}
		}
	}

	@Test
	public void testGetTilesForLiderbet() {
		for (Map.Entry<Integer, String> entry : liderbetImagePathMap.entrySet()) {
			List<Tile> resultTiles = tilesDetector.getTiles(entry.getValue(), getTilesDetectorParamsForLiderbet());
			List<Tile> expectedTiles = liderbetExpectedTilesMap.get(entry.getKey());
			Assert.assertEquals(expectedTiles.size(), resultTiles.size());
			for (int i = 0; i < expectedTiles.size() ; i++) {
				Assert.assertEquals(expectedTiles.get(i), resultTiles.get(i));
			}
		}
	}

	private TilesDetectorParams getTilesDetectorParamsForBetlive() {
		return new TilesDetectorParams()
				.contourMinArea(200)
				.heightPercentage(15)
				.marginBottomPercentage(5)
				.marginLeftPercentage(15)
				.widthPercentage(70)
				.blurCoefficient(3);
	}

	private TilesDetectorParams getTilesDetectorParamsForLiderbet() {
		return new TilesDetectorParams()
				.contourMinArea(200)
				.heightPercentage(12)
				.marginBottomPercentage(5)
				.marginLeftPercentage(15)
				.widthPercentage(75)
				.blurCoefficient(1);
	}

	private static void initBetlivePathMap() {
		for (int i = 1; i <= betliveImageCount; i++) {
			betliveImagePathMap.put(i, BETLIVE_IMAGE_PATH_SUFFIX + i + IMAGE_EXTENSION);
		}
	}

	private static void initLiderbetPathMap() {
		for (int i = 1; i <= liderbetImageCount; i++) {
			liderbetImagePathMap.put(i, LIDERBET_IMAGE_PATH_SUFFIX + i + IMAGE_EXTENSION);
		}
	}

	private static void initBetliveExpectedTiles() {
		int index = 1;
		initBetliveExpectedTiles1(index++);
		initBetliveExpectedTiles2(index++);
		initBetliveExpectedTiles3(index++);
		initBetliveExpectedTiles4(index++);
		initBetliveExpectedTiles5(index++);
		initBetliveExpectedTiles6(index++);
		initBetliveExpectedTiles7(index++);
	}

	private static void initBetliveExpectedTiles1(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 5));
		expectedTiles.add(new Tile(5, 1));
		expectedTiles.add(new Tile(5, 0));
		expectedTiles.add(new Tile(4, 4));
		expectedTiles.add(new Tile(4, 2));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(0, 0));
		betliveExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initBetliveExpectedTiles2(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(6, 3));
		expectedTiles.add(new Tile(5, 4));
		expectedTiles.add(new Tile(4, 1));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(2, 2));
		expectedTiles.add(new Tile(2, 1));
		expectedTiles.add(new Tile(1, 0));
		betliveExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initBetliveExpectedTiles3(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 3));
		expectedTiles.add(new Tile(3, 2));
		expectedTiles.add(new Tile(6, 6));
		expectedTiles.add(new Tile(6, 5));
		expectedTiles.add(new Tile(6, 4));
		expectedTiles.add(new Tile(4, 2));
		expectedTiles.add(new Tile(4, 0));
		betliveExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initBetliveExpectedTiles4(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 1));
		expectedTiles.add(new Tile(6, 6));
		expectedTiles.add(new Tile(6, 3));
		expectedTiles.add(new Tile(5, 4));
		expectedTiles.add(new Tile(4, 4));
		expectedTiles.add(new Tile(4, 3));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(3, 2));
		betliveExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initBetliveExpectedTiles5(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(4, 2));
		expectedTiles.add(new Tile(6, 6));
		expectedTiles.add(new Tile(6, 5));
		betliveExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initBetliveExpectedTiles6(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 5));
		expectedTiles.add(new Tile(6, 1));
		expectedTiles.add(new Tile(4, 2));
		expectedTiles.add(new Tile(3, 3));
		betliveExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initBetliveExpectedTiles7(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(5, 5));
		expectedTiles.add(new Tile(6, 2));
		expectedTiles.add(new Tile(4, 4));
		expectedTiles.add(new Tile(3, 0));
		betliveExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initLiderbetExpectedTiles() {
		int index = 1;
		initLiderbetExpectedTiles1(index++);
		initLiderbetExpectedTiles2(index++);
		initLiderbetExpectedTiles3(index++);
		initLiderbetExpectedTiles4(index++);
		initLiderbetExpectedTiles5(index++);
	}

	private static void initLiderbetExpectedTiles1(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(4, 6));
		expectedTiles.add(new Tile(0, 6));
		expectedTiles.add(new Tile(0, 4));
		expectedTiles.add(new Tile(3, 5));
		expectedTiles.add(new Tile(2, 5));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(1, 1));
		liderbetExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initLiderbetExpectedTiles2(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(0, 0));
		expectedTiles.add(new Tile(3, 3));
		expectedTiles.add(new Tile(4, 4));
		expectedTiles.add(new Tile(3, 4));
		expectedTiles.add(new Tile(1, 3));
		liderbetExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initLiderbetExpectedTiles3(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(0, 1));
		expectedTiles.add(new Tile(1, 6));
		expectedTiles.add(new Tile(5, 6));
		expectedTiles.add(new Tile(2, 4));
		expectedTiles.add(new Tile(4, 4));
		expectedTiles.add(new Tile(0, 4));
		expectedTiles.add(new Tile(1, 3));
		liderbetExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initLiderbetExpectedTiles4(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(0, 3));
		expectedTiles.add(new Tile(2, 6));
		expectedTiles.add(new Tile(1, 3));
		expectedTiles.add(new Tile(2, 3));
		expectedTiles.add(new Tile(5, 5));
		expectedTiles.add(new Tile(3, 4));
		expectedTiles.add(new Tile(2, 2));
		liderbetExpectedTilesMap.put(index, expectedTiles);
	}

	private static void initLiderbetExpectedTiles5(int index) {
		List<Tile> expectedTiles = new ArrayList<>();
		expectedTiles.add(new Tile(3, 6));
		expectedTiles.add(new Tile(4, 6));
		expectedTiles.add(new Tile(0, 4));
		expectedTiles.add(new Tile(1, 5));
		expectedTiles.add(new Tile(3, 4));
		expectedTiles.add(new Tile(3, 5));
		expectedTiles.add(new Tile(1, 1));
		liderbetExpectedTilesMap.put(index, expectedTiles);
	}
}
