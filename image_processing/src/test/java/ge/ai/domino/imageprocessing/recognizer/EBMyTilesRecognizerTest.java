package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.table.IPTile;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EBMyTilesRecognizerTest {

    private static final String IMAGE_PATH_SUFFIX = "test_images/recognize/eb/";

    @Test
    public void testRecognizeMyTiles1() throws Exception {
        testMyTiles("mt1.png", getExpectedTiles1());
    }

    @Test
    public void testRecognizeMyTiles2() throws Exception {
        testMyTiles("mt2.png", getExpectedTiles2());
    }

    @Test
    public void testRecognizeMyTiles3() throws Exception {
        testMyTiles("mt3.png", getExpectedTiles3());
    }

    @Test
    public void testRecognizeMyTiles4() throws Exception {
        testMyTiles("mt4.png", getExpectedTiles4());
    }

    private void testMyTiles(String imageName, List<IPTile> expectedTiles) throws Exception {
        BufferedImage img = ImageIO.read(new File(IMAGE_PATH_SUFFIX + imageName));
        List<IPTile> realTiles = TableRecognizer.recognizeMyTiles(img, geMyTilesRecognizeParams());
        TilesDetectorHelper.assertIPTiles(expectedTiles, realTiles);
    }

    private MyTileRecognizeParams geMyTilesRecognizeParams() {
        return new MyTileRecognizeParams()
                .topLeft(new Point(310, 655))
                .bottomRight(new Point(1050, 725))
                .contourMinArea(200)
                .blurCoefficient(1)
                .combinedPoints(true);
    }

    private List<IPTile> getExpectedTiles1() {
        return Arrays.asList(
                new IPTile(5, 6, new Point(559, 657), new Point(590, 723)),
                new IPTile(6, 6, new Point(595, 657), new Point(626, 723)),
                new IPTile(4, 5, new Point(631, 657), new Point(662, 723)),
                new IPTile(0, 1, new Point(667, 657), new Point(698, 723)),
                new IPTile(3, 4, new Point(703, 657), new Point(734, 723)),
                new IPTile(1, 3, new Point(739, 657), new Point(770, 723)),
                new IPTile(2, 4, new Point(775, 657), new Point(806, 723)));
    }

    private List<IPTile> getExpectedTiles2() {
        return Arrays.asList(
                new IPTile(2, 2, new Point(559, 657), new Point(590, 723)),
                new IPTile(3, 3, new Point(595, 657), new Point(626, 723)),
                new IPTile(0, 1, new Point(631, 657), new Point(662, 723)),
                new IPTile(4, 6, new Point(667, 657), new Point(698, 723)),
                new IPTile(0, 2, new Point(703, 657), new Point(734, 723)),
                new IPTile(0, 4, new Point(739, 657), new Point(770, 723)),
                new IPTile(3, 4, new Point(775, 657), new Point(806, 723)));
    }

    private List<IPTile> getExpectedTiles3() {
        return Collections.singletonList(
                new IPTile(2, 4, new Point(665, 657), new Point(696, 723)));
    }

    private List<IPTile> getExpectedTiles4() {
        return Arrays.asList(
                new IPTile(0, 1, new Point(648, 657), new Point(679, 723)),
                new IPTile(0, 2, new Point(683, 657), new Point(715, 723)));
    }
}
