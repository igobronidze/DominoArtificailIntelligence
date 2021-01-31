package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.table.IPRectangle;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EBMyBazaarTilesRecognizerTest {

    @Test
    public void testRecognizeMyTiles1() throws Exception {
        testTiles(4, getExpectedTiles1());
    }

    private void testTiles(int tilesCount, List<IPRectangle> expectedTiles) throws Exception {
        List<IPRectangle> realTiles = TableRecognizer.recognizeMyBazaarTiles(getMyBazaarTileRecognizeParams(tilesCount));
        TilesDetectorHelper.assetIPRectangles(expectedTiles, realTiles);
    }

    private MyBazaarTileRecognizeParams getMyBazaarTileRecognizeParams(int tilesCount) {
        return new MyBazaarTileRecognizeParams()
                .screenWidth(1366)
                .tilesCount(tilesCount)
                .tileWidth(32)
                .tilesSpacing(3)
                .tileTop(521)
                .tileBottom(580);
    }

    private List<IPRectangle> getExpectedTiles1() {
        return Arrays.asList(
                new IPRectangle(new Point(616, 521), new Point(648, 580)),
                new IPRectangle(new Point(651, 521), new Point(683, 580)),
                new IPRectangle(new Point(686, 521), new Point(718, 580)),
                new IPRectangle(new Point(721, 521), new Point(753, 580)));
    }
}
