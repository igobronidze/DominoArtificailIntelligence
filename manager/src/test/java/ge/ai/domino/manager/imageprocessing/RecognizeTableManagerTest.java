package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.Rectangle;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        Map<MoveDirection, Rectangle> rectanglesByDirections = recognizeTableManager.getRectanglesByDirections(rectangles, centerRectangle);

        Assert.assertEquals(rectangleTop, rectanglesByDirections.get(MoveDirection.TOP));
        Assert.assertEquals(rectangleLeft, rectanglesByDirections.get(MoveDirection.LEFT));
        Assert.assertEquals(rectangleRight, rectanglesByDirections.get(MoveDirection.RIGHT));
        Assert.assertEquals(rectangleBottom, rectanglesByDirections.get(MoveDirection.BOTTOM));
    }

    @Test
    public void testGetRelevantRectangle2() {
        Rectangle rectangleRight = new Rectangle(new Point(950, 260), new Point(978, 298));
        Rectangle rectangleTop = new Rectangle(new Point(619, 274), new Point(647, 314));
        Rectangle rectangleLeft = new Rectangle(new Point(350, 333), new Point(386, 362));
        List<Rectangle> rectangles = Arrays.asList(rectangleRight, rectangleTop, rectangleLeft);

        Rectangle centerRectangle = new Rectangle(new Point(618, 314), new Point(649, 379));

        RecognizeTableManager recognizeTableManager = new RecognizeTableManager();
        Map<MoveDirection, Rectangle> rectanglesByDirections = recognizeTableManager.getRectanglesByDirections(rectangles, centerRectangle);

        Assert.assertEquals(rectangleTop, rectanglesByDirections.get(MoveDirection.TOP));
        Assert.assertEquals(rectangleLeft, rectanglesByDirections.get(MoveDirection.LEFT));
        Assert.assertEquals(rectangleRight, rectanglesByDirections.get(MoveDirection.RIGHT));
    }

    @Test
    public void testGetRelevantRectangle3() {
        Rectangle rectangleRight = new Rectangle(new Point(950, 260), new Point(978, 298));
        Rectangle rectangleLeft = new Rectangle(new Point(350, 333), new Point(386, 362));
        List<Rectangle> rectangles = Arrays.asList(rectangleRight, rectangleLeft);

        RecognizeTableManager recognizeTableManager = new RecognizeTableManager();
        Map<MoveDirection, Rectangle> rectanglesByDirections = recognizeTableManager.getRectanglesByDirections(rectangles, null);

        Assert.assertEquals(rectangleLeft, rectanglesByDirections.get(MoveDirection.LEFT));
        Assert.assertEquals(rectangleRight, rectanglesByDirections.get(MoveDirection.RIGHT));
    }
}
