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
//public class BLTilesDetectorTest extends TilesDetectorTest {
//
//    private static final String IMAGE_PATH_SUFFIX = "test_images/detector/bl/domino_";
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
//                .heightPercentage(15)
//                .marginBottomPercentage(5)
//                .marginLeftPercentage(15)
//                .widthPercentage(63)
//                .blurCoefficient(3)
//                .combinedPoints(true);
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
//        expectedTiles.add(new TileContour(new Tile(5, 5), 337, 602, 372, 669));
//        expectedTiles.add(new TileContour(new Tile(5, 1), 379, 602, 414, 669));
//        expectedTiles.add(new TileContour(new Tile(5, 0), 421, 602, 455, 669));
//        expectedTiles.add(new TileContour(new Tile(4, 4), 463, 602, 497, 669));
//        expectedTiles.add(new TileContour(new Tile(4, 2), 505, 602, 539, 669));
//        expectedTiles.add(new TileContour(new Tile(3, 3), 546, 602, 581, 669));
//        expectedTiles.add(new TileContour(new Tile(0, 0), 588, 602, 623, 669));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles2(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(6, 3), 341, 631, 376, 698));
//        expectedTiles.add(new TileContour(new Tile(5, 4), 383, 631, 418, 698));
//        expectedTiles.add(new TileContour(new Tile(4, 1), 425, 631, 459, 698));
//        expectedTiles.add(new TileContour(new Tile(3, 3), 467, 631, 501, 698));
//        expectedTiles.add(new TileContour(new Tile(2, 2), 509, 631, 543, 698));
//        expectedTiles.add(new TileContour(new Tile(2, 1), 550, 631, 585, 698));
//        expectedTiles.add(new TileContour(new Tile(1, 0), 592, 631, 627, 698));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles3(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(5, 3), 341, 631, 376, 698));
//        expectedTiles.add(new TileContour(new Tile(3, 2), 383, 631, 417, 698));
//        expectedTiles.add(new TileContour(new Tile(6, 6), 425, 631, 459, 698));
//        expectedTiles.add(new TileContour(new Tile(6, 5), 467, 631, 501, 698));
//        expectedTiles.add(new TileContour(new Tile(6, 4), 509, 631, 543, 698));
//        expectedTiles.add(new TileContour(new Tile(4, 2), 550, 631, 585, 698));
//        expectedTiles.add(new TileContour(new Tile(4, 0), 592, 631, 627, 698));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//
//    private void initExpectedTiles4(int index) {
//        List<TileContour> expectedTiles = new ArrayList<>();
//        expectedTiles.add(new TileContour(new Tile(5, 1), 311, 635, 343, 699));
//        expectedTiles.add(new TileContour(new Tile(6, 6), 351, 635, 383, 699));
//        expectedTiles.add(new TileContour(new Tile(6, 3), 391, 635, 424, 699));
//        expectedTiles.add(new TileContour(new Tile(5, 4), 431, 635, 464, 699));
//        expectedTiles.add(new TileContour(new Tile(4, 4), 471, 635, 504, 699));
//        expectedTiles.add(new TileContour(new Tile(4, 3), 511, 635, 544, 699));
//        expectedTiles.add(new TileContour(new Tile(3, 3), 551, 635, 584, 699));
//        expectedTiles.add(new TileContour(new Tile(3, 2), 591, 635, 624, 699));
//        expectedTilesMap.put(index, expectedTiles);
//    }
//}
