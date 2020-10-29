package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EBTilesDetectorTest extends TilesDetectorTest {

    private static final String IMAGE_PATH_SUFFIX = "test_images/detector/eb/domino_";

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
                .heightPercentage(12)
                .marginBottomPercentage(5)
                .marginLeftPercentage(15)
                .widthPercentage(63)
                .blurCoefficient(1)
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
        expectedTiles.add(new Tile(2, 4));
        expectedTiles.add(new Tile(1, 6));
        expectedTiles.add(new Tile(0, 6));
        expectedTiles.add(new Tile(4, 4));
        expectedTiles.add(new Tile(0, 5));
        expectedTiles.add(new Tile(1, 2));
        expectedTiles.add(new Tile(1, 1));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles2(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(1, 2));
        expectedTiles.add(new Tile(4, 6));
        expectedTiles.add(new Tile(4, 5));
        expectedTiles.add(new Tile(6, 6));
        expectedTiles.add(new Tile(2, 5));
        expectedTiles.add(new Tile(0, 5));
        expectedTiles.add(new Tile(0, 4));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles3(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(0, 4));
        expectedTiles.add(new Tile(4, 4));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles4(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(0, 0));
        expectedTiles.add(new Tile(1, 1));
        expectedTiles.add(new Tile(2, 5));
        expectedTiles.add(new Tile(0, 5));
        expectedTiles.add(new Tile(2, 2));
        expectedTiles.add(new Tile(1, 5));
        expectedTiles.add(new Tile(6, 6));
        expectedTiles.add(new Tile(0, 6));
        expectedTiles.add(new Tile(4, 6));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles5(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(2, 4));
        expectedTiles.add(new Tile(5, 5));
        expectedTiles.add(new Tile(1, 6));
        expectedTiles.add(new Tile(2, 3));
        expectedTiles.add(new Tile(2, 2));
        expectedTiles.add(new Tile(5, 6));
        expectedTiles.add(new Tile(1, 5));
        expectedTilesMap.put(index, expectedTiles);
    }
}
