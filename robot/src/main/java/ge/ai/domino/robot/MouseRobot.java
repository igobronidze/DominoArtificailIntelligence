package ge.ai.domino.robot;

import com.github.joonasvali.naturalmouse.api.MouseMotion;
import com.github.joonasvali.naturalmouse.api.MouseMotionFactory;
import ge.ai.domino.util.random.RandomUtils;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseRobot {

    public static void moveAndClick(int xPosition, int yPosition) throws Exception {
        MouseMotionFactory mouseMotionFactory = MouseMotionFactory.getDefault();
        MouseMotion mouseMotion = mouseMotionFactory.build(xPosition, yPosition);
        mouseMotion.move();

        Robot robot = new Robot();
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(RandomUtils.getRandomBetween(70, 100));
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public static void moveDeltaPosition(int deltaX, int deltaY) throws Exception {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point point = pointerInfo.getLocation();
        int xPosition = (int) point.getX();
        int yPosition = (int) point.getY();

        MouseMotionFactory mouseMotionFactory = MouseMotionFactory.getDefault();
        MouseMotion mouseMotion = mouseMotionFactory.build(xPosition + deltaX, yPosition + deltaY);
        mouseMotion.move();
    }
}
