package ge.ai.domino.robot;

import com.github.joonasvali.naturalmouse.api.MouseMotion;
import com.github.joonasvali.naturalmouse.api.MouseMotionFactory;
import ge.ai.domino.util.random.RandomUtils;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseRobot {

    public static void move(int xPosition, int yPosition) throws Exception {
        MouseMotionFactory mouseMotionFactory = MouseMotionFactory.getDefault();
        MouseMotion mouseMotion = mouseMotionFactory.build(xPosition, yPosition);
        mouseMotion.move();
    }

    public static void click() throws Exception {
        Robot robot = new Robot();
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(RandomUtils.getRandomBetween(70, 100));
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void drag(int xPosition, int yPosition) throws Exception {
        Robot robot = new Robot();
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        MouseMotionFactory mouseMotionFactory = MouseMotionFactory.getDefault();
        MouseMotion mouseMotion = mouseMotionFactory.build(xPosition, yPosition);
        mouseMotion.move();
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void moveDeltaPosition(int deltaX, int deltaY) throws Exception {
        Point point = MouseInfo.getPointerInfo().getLocation();
        int xPosition = (int) point.getX();
        int yPosition = (int) point.getY();

        MouseMotionFactory mouseMotionFactory = MouseMotionFactory.getDefault();
        MouseMotion mouseMotion = mouseMotionFactory.build(xPosition + deltaX, yPosition + deltaY);
        mouseMotion.move();
    }

    public static boolean isCursorOnRectangle(int left, int top, int right, int bottom) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        int xPosition = (int) point.getX();
        int yPosition = (int) point.getY();
        return xPosition >= left && xPosition <= right && yPosition >= top && yPosition <= bottom;
    }
}
