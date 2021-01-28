package ge.ai.domino.imageprocessing.service.rectangle;

import ge.ai.domino.imageprocessing.service.Point;

import java.awt.image.BufferedImage;
import java.util.*;

public class SameColorRectangleDetector {

    private int width;
    private int height;

    boolean[][] coloredMatrix;
    boolean[][] checked;

    public List<Rectangle> getSameColorRectangles(BufferedImage image, Color color) {
        Set<Point> points = new HashSet<>();

        width = image.getWidth();
        height = image.getHeight();
        coloredMatrix = new boolean[height][width];
        checked = new boolean[height][width];

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int clr = image.getRGB(j, i);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                if (red >= color.getRedFrom() && red < color.getRedTo() &&
                        green >= color.getGreenFrom() && green < color.getGreenTo() &&
                        blue >= color.getBlueFrom() && blue < color.getBlueTo()) {
                    points.add(new Point(j, i));
                    coloredMatrix[i][j] = true;
                }
            }
        }

        List<Rectangle> rectangles = new ArrayList<>();

        for (int i = 0; i < coloredMatrix.length; i++) {
            for (int j = 0; j < coloredMatrix[i].length; j++) {
                if (!checked[i][j] && coloredMatrix[i][j]) {
                    rectangles.add(getRectangle(new Point(i, j)));
                }
            }
        }


        return null;
    }

    private Rectangle getRectangle(Point point) {
        Rectangle rectangle = new Rectangle(point);

        Queue<Point> queue = new LinkedList<>();
        queue.add(point);
        checked[point.getX()][point.getY()] = true;

        while (!queue.isEmpty()) {
            Point curr = queue.remove();
            rectangle.addPoint(curr);
            queue.addAll(getConnectedPoints(curr.getX(), curr.getY()));
        }

        return rectangle;
    }

    private List<Point> getConnectedPoints(int i, int j) {
        List<Point> connected = new ArrayList<>();
        for (short deltaI = -1; deltaI <= 1; deltaI++) {
            for (short deltaJ = -1; deltaJ <= 1; deltaJ++) {
                if (i + deltaI >= 0 && i + deltaI < height && j + deltaJ >= 0 && j + deltaJ < width) {
                    if (coloredMatrix[i + deltaI][j + deltaJ] && !checked[i + deltaI][j + deltaJ]) {
                        connected.add(new Point((short) (i + deltaI), (short) (j + deltaJ)));
                        checked[i + deltaI][j + deltaJ] = true;
                    }
                }
            }
        }
        return connected;
    }
}
