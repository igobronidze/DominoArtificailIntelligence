package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.table.IPPossMovesAndCenter;
import ge.ai.domino.imageprocessing.service.table.IPRectangle;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class EBPossMoveTilesRecognizerTest {

    private static final String IMAGE_PATH_SUFFIX = "test_images/recognize/eb/";

    @Test
    public void testRecognizePossMoveTiles1() throws Exception {
        testPossMovesAndCenter("pmt1.png", getPossMoveTilesAndCenter1());
    }

    @Test
    public void testRecognizePossMoveTiles2() throws Exception {
        testPossMovesAndCenter("pmt2.png", getPossMoveTilesAndCenter2());
    }

    @Test
    public void testRecognizePossMoveTiles3() throws Exception {
        testPossMovesAndCenter("pmt3.png", getPossMoveTilesAndCenter3());
    }

    private void testPossMovesAndCenter(String imageName, IPPossMovesAndCenter expected) throws Exception {
        BufferedImage img = ImageIO.read(new File(IMAGE_PATH_SUFFIX + imageName));
        IPPossMovesAndCenter real = TableRecognizer.recognizePossMoveTiles(img, getPossMoveTileRecognizeParams());
        TilesDetectorHelper.assertPossMoveTilesAndCenter(expected, real);
    }

    private PossMoveTileRecognizeParams getPossMoveTileRecognizeParams() {
        return new PossMoveTileRecognizeParams()
                .topLeft(new Point(120, 150))
                .bottomRight(new Point(1220, 610))
                .contourMinArea(200);
    }

    private IPPossMovesAndCenter getPossMoveTilesAndCenter1() {
        return new IPPossMovesAndCenter(Arrays.asList(
                new IPRectangle(new Point(627, 357), new Point(665, 387)),
                new IPRectangle(new Point(700, 358), new Point(738, 387))),
                new IPRectangle(new Point(667, 340), new Point(698, 404)));
    }

    private IPPossMovesAndCenter getPossMoveTilesAndCenter2() {
        return new IPPossMovesAndCenter(Arrays.asList(
                new IPRectangle(new Point(503, 165), new Point(531, 206)),
                new IPRectangle(new Point(397, 291), new Point(435, 320)),
                new IPRectangle(new Point(929, 291), new Point(969, 320)),
                new IPRectangle(new Point(503, 538), new Point(531, 576))),
                new IPRectangle(new Point(502, 273), new Point(533, 337)));
    }

    private IPPossMovesAndCenter getPossMoveTilesAndCenter3() {
        return new IPPossMovesAndCenter(Collections.singletonList(
                new IPRectangle(new Point(500, 174), new Point(526, 195))),
                new IPRectangle(new Point(639, 334), new Point(659, 376)));
    }
}
