package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.imageprocessing.recognizer.PossMoveTileRecognizeParams;
import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.Rectangle;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RecognizeTableManagerTest {

    private static final PossMoveTileRecognizeParams params = new PossMoveTileRecognizeParams()
            .topLeft(new Point(0, 0))
            .bottomRight(new Point(1000, 1000));

    @Test
    public void testGetRelevantRectangle1() {
        Rectangle rectangleTop = new Rectangle(new Point(503, 165), new Point(531, 206));
        Rectangle rectangleLeft = new Rectangle(new Point(397, 291), new Point(435, 320));
        Rectangle rectangleRight = new Rectangle(new Point(929, 291), new Point(969, 320));
        Rectangle rectangleBottom = new Rectangle(new Point(503, 538), new Point(531, 576));
        List<Rectangle> rectangles = Arrays.asList(rectangleTop, rectangleLeft, rectangleRight, rectangleBottom);

        RecognizeTableManager recognizeTableManager = new RecognizeTableManager();
        Assert.assertEquals(rectangleTop, recognizeTableManager.getRelevantRectangle(rectangles, MoveDirection.TOP, params));
        Assert.assertEquals(rectangleLeft, recognizeTableManager.getRelevantRectangle(rectangles, MoveDirection.LEFT, params));
        Assert.assertEquals(rectangleRight, recognizeTableManager.getRelevantRectangle(rectangles, MoveDirection.RIGHT, params));
        Assert.assertEquals(rectangleBottom, recognizeTableManager.getRelevantRectangle(rectangles, MoveDirection.BOTTOM, params));
    }
}
