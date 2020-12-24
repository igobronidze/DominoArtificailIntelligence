package ge.ai.domino.robot;

import com.github.joonasvali.naturalmouse.api.MouseMotion;
import com.github.joonasvali.naturalmouse.api.MouseMotionFactory;
import ge.ai.domino.robot.exception.RobotException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

public class MouseRobot {

    public static void main(String[] args) throws RobotException {
        moveAndClick(500, 500);
    }

    public static void moveAndClick(int xPosition, int yPosition) throws RobotException {
        try {
            MouseMotionFactory mouseMotionFactory = MouseMotionFactory.getDefault();
            MouseMotion mouseMotion = mouseMotionFactory.build(xPosition, yPosition);
            mouseMotion.move();

            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_MASK);
            Thread.sleep(getRandomBetween(100, 150));
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (Exception ex) {
            throw new RobotException(ex.getMessage());
        }
    }

    private static int getRandomBetween(int from, int to) {
        return from + new Random().nextInt(to - from);
    }
}
