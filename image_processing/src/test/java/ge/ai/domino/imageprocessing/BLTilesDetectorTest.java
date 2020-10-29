package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BLTilesDetectorTest extends TilesDetectorTest {

    private static final String IMAGE_PATH_SUFFIX = "test_images/detector/bl/domino_";

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
                .heightPercentage(15)
                .marginBottomPercentage(5)
                .marginLeftPercentage(15)
                .widthPercentage(63)
                .blurCoefficient(3)
                .combinedPoints(true);
    }

    private void initExpectedTiles() {
        int index = 1;
        initExpectedTiles1(index++);
        initExpectedTiles2(index++);
        initExpectedTiles3(index++);
        initExpectedTiles4(index++);
        initExpectedTiles5(index++);
        initExpectedTiles6(index++);
        initExpectedTiles7(index);
    }

    private void initExpectedTiles1(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(5, 5));
        expectedTiles.add(new Tile(5, 1));
        expectedTiles.add(new Tile(5, 0));
        expectedTiles.add(new Tile(4, 4));
        expectedTiles.add(new Tile(4, 2));
        expectedTiles.add(new Tile(3, 3));
        expectedTiles.add(new Tile(0, 0));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles2(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(6, 3));
        expectedTiles.add(new Tile(5, 4));
        expectedTiles.add(new Tile(4, 1));
        expectedTiles.add(new Tile(3, 3));
        expectedTiles.add(new Tile(2, 2));
        expectedTiles.add(new Tile(2, 1));
        expectedTiles.add(new Tile(1, 0));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles3(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(5, 3));
        expectedTiles.add(new Tile(3, 2));
        expectedTiles.add(new Tile(6, 6));
        expectedTiles.add(new Tile(6, 5));
        expectedTiles.add(new Tile(6, 4));
        expectedTiles.add(new Tile(4, 2));
        expectedTiles.add(new Tile(4, 0));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles4(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(5, 1));
        expectedTiles.add(new Tile(6, 6));
        expectedTiles.add(new Tile(6, 3));
        expectedTiles.add(new Tile(5, 4));
        expectedTiles.add(new Tile(4, 4));
        expectedTiles.add(new Tile(4, 3));
        expectedTiles.add(new Tile(3, 3));
        expectedTiles.add(new Tile(3, 2));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles5(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(4, 2));
        expectedTiles.add(new Tile(6, 6));
        expectedTiles.add(new Tile(6, 5));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles6(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(5, 5));
        expectedTiles.add(new Tile(6, 1));
        expectedTiles.add(new Tile(4, 2));
        expectedTiles.add(new Tile(3, 3));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles7(int index) {
        List<Tile> expectedTiles = new ArrayList<>();
        expectedTiles.add(new Tile(5, 5));
        expectedTiles.add(new Tile(6, 2));
        expectedTiles.add(new Tile(4, 4));
        expectedTiles.add(new Tile(3, 0));
        expectedTilesMap.put(index, expectedTiles);
    }
}
