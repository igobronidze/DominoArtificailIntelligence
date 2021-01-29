package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.Rectangle;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RecognizeTableManagerTest {

    @Test
    public void testGetRelevantRectangle1() {
        Rectangle rectangleTop = new Rectangle(new Point(503, 165), new Point(531, 206));
        Rectangle rectangleLeft = new Rectangle(new Point(397, 291), new Point(435, 320));
        Rectangle rectangleRight = new Rectangle(new Point(929, 291), new Point(969, 320));
        Rectangle rectangleBottom = new Rectangle(new Point(503, 538), new Point(531, 576));
        List<Rectangle> rectangles = Arrays.asList(rectangleTop, rectangleLeft, rectangleRight, rectangleBottom);

        Rectangle centerRectangle = new Rectangle(new Point(502, 273), new Point(533, 337));

        RecognizeTableManager recognizeTableManager = new RecognizeTableManager();
        Assert.assertEquals(rectangleTop, recognizeTableManager.getRelevantRectangle(rectangles, centerRectangle, MoveDirection.TOP));
        Assert.assertEquals(rectangleLeft, recognizeTableManager.getRelevantRectangle(rectangles, centerRectangle, MoveDirection.LEFT));
        Assert.assertEquals(rectangleRight, recognizeTableManager.getRelevantRectangle(rectangles, centerRectangle, MoveDirection.RIGHT));
        Assert.assertEquals(rectangleBottom, recognizeTableManager.getRelevantRectangle(rectangles, centerRectangle, MoveDirection.BOTTOM));
    }
}
