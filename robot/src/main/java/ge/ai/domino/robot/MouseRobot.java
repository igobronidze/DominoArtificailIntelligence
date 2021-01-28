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

    public static void moveAndClick(int xPosition, int yPosition) throws Exception {
        MouseMotionFactory mouseMotionFactory = MouseMotionFactory.getDefault();
        MouseMotion mouseMotion = mouseMotionFactory.build(xPosition, yPosition);
        mouseMotion.move();

        Robot robot = new Robot();
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(RandomUtils.getRandomBetween(100, 150));
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
}
