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
//public class EBTilesDetectorTest extends TilesDetectorTest {
//
//    private static final String IMAGE_PATH_SUFFIX = "test_images/detector/eb/domino_";
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
//                .marginBottomPercentage(4)
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
//        initExpectedTiles4(index);
//    }
//
//    private void initExpectedTiles1(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(2, 4), 767, 932, 816, 1034));
//        expectedTiles.add(new TileContour(new Tile(1, 6), 823, 932, 872, 1034));
//        expectedTiles.add(new TileContour(new Tile(0, 6), 879, 932, 928, 1034));
//        expectedTiles.add(new TileContour(new Tile(4, 4), 935, 932, 984, 1034));
//        expectedTiles.add(new TileContour(new Tile(0, 5), 991, 932, 1040, 1034));
//        expectedTiles.add(new TileContour(new Tile(1, 2), 1047, 932, 1096, 1034));
//        expectedTiles.add(new TileContour(new Tile(1, 1), 1103, 932, 1152, 1034));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles2(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(1, 2), 767, 932, 816, 1034));
//        expectedTiles.add(new TileContour(new Tile(4, 6), 823, 932, 872, 1034));
//        expectedTiles.add(new TileContour(new Tile(4, 5), 879, 932, 928, 1034));
//        expectedTiles.add(new TileContour(new Tile(6, 6), 935, 932, 984, 1034));
//        expectedTiles.add(new TileContour(new Tile(2, 5), 991, 932, 1040, 1034));
//        expectedTiles.add(new TileContour(new Tile(0, 5), 1047, 932, 1096, 1034));
//        expectedTiles.add(new TileContour(new Tile(0, 4), 1103, 932, 1152, 1034));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles3(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(0, 4), 905, 932, 953, 1034));
//        expectedTiles.add(new TileContour(new Tile(4, 4), 961, 932, 1009, 1034));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles4(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(0, 0), 710, 932, 759, 1034));
//        expectedTiles.add(new TileContour(new Tile(1, 1), 766, 932, 815, 1034));
//        expectedTiles.add(new TileContour(new Tile(2, 5), 822, 932, 871, 1034));
//        expectedTiles.add(new TileContour(new Tile(0, 5), 878, 932, 927, 1034));
//        expectedTiles.add(new TileContour(new Tile(2, 2), 934, 932, 983, 1034));
//        expectedTiles.add(new TileContour(new Tile(1, 5), 990, 932, 1039, 1034));
//        expectedTiles.add(new TileContour(new Tile(6, 6), 1046, 932, 1095, 1034));
//        expectedTiles.add(new TileContour(new Tile(0, 6), 1102, 932, 1151, 1034));
//        expectedTiles.add(new TileContour(new Tile(4, 6), 1158, 932, 1207, 1034));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//}
