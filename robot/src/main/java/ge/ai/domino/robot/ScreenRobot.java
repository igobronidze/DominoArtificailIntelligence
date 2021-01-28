package ge.ai.domino.robot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class ScreenRobot {

    private static final int SLEEP_AFTER_CHANGE_SCREEN_MS = 500;

    public static BufferedImage getScreenCapture() throws Exception {
        Robot robot = new Robot();
        Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(capture);
    }

    public static void changeScreen() throws Exception {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_TAB);

        Thread.sleep(SLEEP_AFTER_CHANGE_SCREEN_MS);
    }
}
