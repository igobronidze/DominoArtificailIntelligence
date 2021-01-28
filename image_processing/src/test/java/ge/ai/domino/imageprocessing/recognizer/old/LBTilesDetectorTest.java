//package ge.ai.domino.imageprocessing;
//
//import ge.ai.domino.domain.game.Tile;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class LBTilesDetectorTest extends TilesDetectorTest {
//
//    private static final String IMAGE_PATH_SUFFIX = "test_images/detector/lb/domino_";
//
//    @Before
//    public void init() throws IOException {
//        initExpectedTiles();
//        initImagesMap(IMAGE_PATH_SUFFIX);
//    }
//
//    @Test
//    public void testGetTiles() {
//        super.innerTestGetTiles();
//    }
//
//    @Override
//    public TilesDetectorParams getTilesDetectorParams() {
//        return new TilesDetectorParams()
//                .contourMinArea(200)
//                .heightPercentage(12)
//                .marginBottomPercentage(5)
//                .marginLeftPercentage(15)
//                .widthPercentage(63)
//                .blurCoefficient(1)
//                .combinedPoints(false);
//    }
//
//    private void initExpectedTiles() {
//        int index = 1;
//        initExpectedTiles1(index++);
//        initExpectedTiles2(index++);
//        initExpectedTiles3(index++);
//        initExpectedTiles4(index++);
//        initExpectedTiles5(index);
//    }
//
//    private void initExpectedTiles1(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(4, 6), 522, 640, 560, 715));
//        expectedTiles.add(new TileContour(new Tile(0, 6), 562, 640, 600, 715));
//        expectedTiles.add(new TileContour(new Tile(0, 4), 603, 640, 640, 715));
//        expectedTiles.add(new TileContour(new Tile(3, 5), 643, 640, 680, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 5), 683, 640, 720, 715));
//        expectedTiles.add(new TileContour(new Tile(3, 3), 723, 640, 760, 715));
//        expectedTiles.add(new TileContour(new Tile(1, 1), 763, 640, 800, 715));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles2(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(0, 0), 562, 640, 600, 715));
//        expectedTiles.add(new TileContour(new Tile(3, 3), 603, 640, 640, 715));
//        expectedTiles.add(new TileContour(new Tile(4, 4), 643, 640, 680, 715));
//        expectedTiles.add(new TileContour(new Tile(3, 4), 682, 640, 720, 715));
//        expectedTiles.add(new TileContour(new Tile(1, 3), 723, 640, 761, 715));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles3(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(0, 1), 522, 640, 559, 715));
//        expectedTiles.add(new TileContour(new Tile(1, 6), 562, 640, 600, 715));
//        expectedTiles.add(new TileContour(new Tile(5, 6), 603, 640, 640, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 4), 643, 640, 680, 715));
//        expectedTiles.add(new TileContour(new Tile(4, 4), 683, 640, 720, 715));
//        expectedTiles.add(new TileContour(new Tile(0, 4), 723, 640, 761, 715));
//        expectedTiles.add(new TileContour(new Tile(1, 3), 763, 640, 801, 715));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles4(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(0, 3), 522, 640, 560, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 6), 562, 640, 600, 715));
//        expectedTiles.add(new TileContour(new Tile(1, 3), 603, 640, 640, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 3), 642, 640, 680, 715));
//        expectedTiles.add(new TileContour(new Tile(5, 5), 682, 640, 720, 715));
//        expectedTiles.add(new TileContour(new Tile(3, 4), 723, 640, 760, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 2), 763, 640, 800, 715));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles5(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(0, 1), 443, 640, 480, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 3), 482, 640, 520, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 5), 523, 640, 560, 715));
//        expectedTiles.add(new TileContour(new Tile(3, 4), 562, 640, 600, 715));
//        expectedTiles.add(new TileContour(new Tile(3, 5), 603, 640, 641, 715));
//        expectedTiles.add(new TileContour(new Tile(0, 2), 643, 640, 680, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 6), 683, 640, 720, 715));
//        expectedTiles.add(new TileContour(new Tile(0, 4), 723, 640, 761, 715));
//        expectedTiles.add(new TileContour(new Tile(5, 5), 763, 640, 800, 715));
//        expectedTiles.add(new TileContour(new Tile(2, 4), 803, 640, 841, 715));
//        expectedTiles.add(new TileContour(new Tile(0, 6), 843, 640, 881, 715));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//}
