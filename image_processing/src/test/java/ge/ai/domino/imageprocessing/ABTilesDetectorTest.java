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
        List<TileContour> expectedTiles = new ArrayList<>();
        expectedTiles.add(new TileContour(new Tile(0, 2), 584, 648, 621, 718));
        expectedTiles.add(new TileContour(new Tile(1, 2), 625, 648, 661, 718));
        expectedTiles.add(new TileContour(new Tile(0, 5), 665, 648, 701, 718));
        expectedTiles.add(new TileContour(new Tile(2, 5), 705, 648, 741, 718));
        expectedTiles.add(new TileContour(new Tile(4, 6), 745, 648, 782, 718));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles2(int index) {
        List<TileContour> expectedTiles = new ArrayList<>();
        expectedTiles.add(new TileContour(new Tile(1, 4), 564, 648, 601, 718));
        expectedTiles.add(new TileContour(new Tile(4, 4), 604, 648, 641, 718));
        expectedTiles.add(new TileContour(new Tile(1, 5), 645, 648, 681, 718));
        expectedTiles.add(new TileContour(new Tile(2, 5), 685, 648, 721, 718));
        expectedTiles.add(new TileContour(new Tile(1, 6), 725, 648, 761, 718));
        expectedTiles.add(new TileContour(new Tile(6, 6), 765, 648, 802, 718));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles3(int index) {
        List<TileContour> expectedTiles = new ArrayList<>();
        expectedTiles.add(new TileContour(new Tile(1, 4), 504, 648, 541, 718));
        expectedTiles.add(new TileContour(new Tile(4, 4), 544, 648, 581, 718));
        expectedTiles.add(new TileContour(new Tile(1, 5), 584, 648, 621, 718));
        expectedTiles.add(new TileContour(new Tile(2, 5), 625, 648, 661, 718));
        expectedTiles.add(new TileContour(new Tile(5, 5), 665, 648, 701, 718));
        expectedTiles.add(new TileContour(new Tile(1, 6), 705, 648, 741, 718));
        expectedTiles.add(new TileContour(new Tile(4, 6), 745, 648, 782, 718));
        expectedTiles.add(new TileContour(new Tile(5, 6), 785, 648, 822, 718));
        expectedTiles.add(new TileContour(new Tile(6, 6), 825, 648, 862, 718));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles4(int index) {
        List<TileContour> expectedTiles = new ArrayList<>();
        expectedTiles.add(new TileContour(new Tile(1, 1), 544, 648, 581, 718));
        expectedTiles.add(new TileContour(new Tile(0, 2), 584, 648, 621, 718));
        expectedTiles.add(new TileContour(new Tile(2, 2), 625, 648, 661, 718));
        expectedTiles.add(new TileContour(new Tile(3, 3), 665, 648, 701, 718));
        expectedTiles.add(new TileContour(new Tile(2, 4), 705, 648, 741, 718));
        expectedTiles.add(new TileContour(new Tile(3, 5), 745, 648, 782, 718));
        expectedTiles.add(new TileContour(new Tile(5, 5), 785, 648, 822, 718));
        expectedTilesMap.put(index, expectedTiles);
    }

    private void initExpectedTiles5(int index) {
        List<TileContour> expectedTiles = new ArrayList<>();
        expectedTiles.add(new TileContour(new Tile(0, 0), 544, 648, 581, 718));
        expectedTiles.add(new TileContour(new Tile(1, 1), 584, 648, 621, 718));
        expectedTiles.add(new TileContour(new Tile(1, 5), 625, 648, 661, 718));
        expectedTiles.add(new TileContour(new Tile(2, 5), 665, 648, 701, 718));
        expectedTiles.add(new TileContour(new Tile(0, 6), 705, 648, 741, 718));
        expectedTiles.add(new TileContour(new Tile(2, 6), 745, 648, 782, 718));
        expectedTiles.add(new TileContour(new Tile(4, 6), 785, 648, 822, 718));
        expectedTilesMap.put(index, expectedTiles);
    }
}
