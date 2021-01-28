package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.table.IPPossMoveTile;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EBPossMoveTilesRecognizerTest {

    private static final String IMAGE_PATH_SUFFIX = "test_images/recognize/eb/";

    @Test
    public void testRecognizePossMoveTiles1() throws Exception {
        testMyTiles("pmt1.png", getPossMoveTiles1());
    }

    @Test
    public void testRecognizePossMoveTiles2() throws Exception {
        testMyTiles("pmt2.png", getPossMoveTiles2());
    }

    @Test
    public void testRecognizePossMoveTiles3() throws Exception {
        testMyTiles("pmt3.png", getPossMoveTiles3());
    }

    private void testMyTiles(String imageName, List<IPPossMoveTile> possMoveTiles) throws Exception {
        BufferedImage img = ImageIO.read(new File(IMAGE_PATH_SUFFIX + imageName));
        List<IPPossMoveTile> realTiles = TableRecognizer.recognizePossMoveTiles(img, getPossMoveTileRecognizeParams());
        TilesDetectorHelper.assertPossMoveTiles(possMoveTiles, realTiles);
    }

    private PossMoveTileRecognizeParams getPossMoveTileRecognizeParams() {
        return new PossMoveTileRecognizeParams()
                .topLeft(new Point(120, 150))
                .bottomRight(new Point(1220, 610))
                .contourMinArea(200);
    }

    private List<IPPossMoveTile> getPossMoveTiles1() {
        return Arrays.asList(
                new IPPossMoveTile(new Point(627, 357), new Point(665, 387)),
                new IPPossMoveTile(new Point(700, 358), new Point(738, 387)));
    }

    private List<IPPossMoveTile> getPossMoveTiles2() {
        return Arrays.asList(
                new IPPossMoveTile(new Point(503, 165), new Point(531, 206)),
                new IPPossMoveTile(new Point(397, 291), new Point(435, 320)),
                new IPPossMoveTile(new Point(929, 291), new Point(969, 320)),
                new IPPossMoveTile(new Point(503, 538), new Point(531, 576)));
    }

    private List<IPPossMoveTile> getPossMoveTiles3() {
        return Collections.singletonList(
                new IPPossMoveTile(new Point(500, 174), new Point(526, 195)));
    }
}
