package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ABTilesDetectorTest extends TilesDetectorTest {

    private static final String IMAGE_PATH_SUFFIX = "test_images/detector/ab/domino_";

    @Before
    public void init() throws IOException {
        initExpectedTiles();
        initImagesMap(IMAGE_PATH_SUFFIX);
    }

    @Test
    public void testGetTiles() {
        super.innerTestGetTiles();
    }

    @Override
    public TilesDetectorParams getTilesDetectorParams() {
        return new TilesDetectorParams()
                .contourMinArea(200)
                .heightPercentage(10)
                .marginBottomPercentage(6)
                .marginLeftPercentage(17)
                .widthPercentage(61)
                .blurCoefficient(2)
                .combinedPoints(false);
    }

    private void initExpectedTiles() {
        int index = 1;
        initExpectedTiles1(index++);
        initExpectedTiles2(index++);
        initExpectedTiles3(index++);
        initExpectedTiles4(index++);
        initExpectedTiles5(index);
    }

    private void initExpectedTiles1(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(0, 2));
        expectedTiles.add(new Tile(1, 2));
        expectedTiles.add(new Tile(0, 5));
        expectedTiles.add(new Tile(2, 5));
        expectedTiles.add(new Tile(4, 6));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles2(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(1, 4));
        expectedTiles.add(new Tile(4, 4));
        expectedTiles.add(new Tile(1, 5));
        expectedTiles.add(new Tile(2, 5));
        expectedTiles.add(new Tile(1, 6));
        expectedTiles.add(new Tile(6, 6));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles3(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(1, 4));
        expectedTiles.add(new Tile(4, 4));
        expectedTiles.add(new Tile(1, 5));
        expectedTiles.add(new Tile(2, 5));
        expectedTiles.add(new Tile(5, 5));
        expectedTiles.add(new Tile(1, 6));
        expectedTiles.add(new Tile(4, 6));
        expectedTiles.add(new Tile(5, 6));
        expectedTiles.add(new Tile(6, 6));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles4(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(0, 5));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles5(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(1, 1));
        expectedTiles.add(new Tile(0, 2));
        expectedTiles.add(new Tile(2, 2));
        expectedTiles.add(new Tile(3, 3));
        expectedTiles.add(new Tile(2, 4));
        expectedTiles.add(new Tile(3, 5));
        expectedTiles.add(new Tile(5, 5));
        expectedTilesMap.put(index, expectedTiles);
    }
}
