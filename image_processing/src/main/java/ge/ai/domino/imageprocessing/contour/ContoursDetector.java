package ge.ai.domino.imageprocessing.contour;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ContoursDetector {

    private static final int WHITE_RGB = -1;

    private int width;

    private int height;

    private boolean[][] coloredMatrix;

    private boolean[][] checked;

    public List<Contour> detectContours(BufferedImage image, int minArea) {
        initSize(image);
        initMatrix(image);

        List<Contour> contours = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (coloredMatrix[i][j] && !checked[i][j]) {
                    Contour contour = getContour(new Point(i, j));
                    if (getArea(contour) >= minArea) {
                        contours.add(contour);
                    }
                }
            }
        }
        return contours;
    }

    private Contour getContour(Point point) {
        Contour contour = new Contour();
        Queue<Point> queue = new LinkedList<>();
        queue.add(point);
        checked[point.getX()][point.getY()] = true;

        while (!queue.isEmpty()) {
            Point curr = queue.remove();
            addPointInContour(curr, contour);
            queue.addAll(getConnectedPoints(curr.getX(), curr.getY()));
        }

        for (int i = contour.getTop(); i <= contour.getBottom(); i++) {
            for (int j = contour.getLeft(); j <= contour.getRight(); j++) {
                if (coloredMatrix[i][j] && !checked[i][j]) {
                    Contour child = getContour(new Point(i, j));
                    child.setParent(contour);
                    contour.getChildren().add(child);
                }
            }
        }

        return contour;
    }

    private List<Point> getConnectedPoints(int i, int j) {
        List<Point> connected = new ArrayList<>();
        for (short deltaI = -1; deltaI <= 1; deltaI++) {
            for (short deltaJ = -1; deltaJ <= 1; deltaJ++) {
                if (i + deltaI >=0 && i + deltaI < height && j + deltaJ >=0 && j + deltaJ < width) {
                    if (coloredMatrix[i + deltaI][j + deltaJ] && !checked[i + deltaI][j + deltaJ]) {
                        connected.add(new Point((short)(i + deltaI), (short)(j + deltaJ)));
                        checked[i + deltaI][j + deltaJ] = true;
                    }
                }
            }
        }
        return connected;
    }

    private void addPointInContour(Point point, Contour contour) {
        contour.setTop(Math.min(contour.getTop(), point.getX()));
        contour.setRight(Math.max(contour.getRight(), point.getY()));
        contour.setBottom(Math.max(contour.getBottom(), point.getX()));
        contour.setLeft(Math.min(contour.getLeft(), point.getY()));
    }

    private void initMatrix(BufferedImage image) {
        coloredMatrix = new boolean[height][width];
        for (short i = 0; i < height; i++) {
            for (short j = 0; j < width; j++) {
                coloredMatrix[i][j] = image.getRGB(j, i) == WHITE_RGB;
            }
        }

        checked = new boolean[height][width];
    }

    private void initSize(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    private int getArea(Contour contour) {
        int height = contour.getBottom() - contour.getTop() + 1;
        int width = contour.getRight() - contour.getLeft() + 1;
        return height * width;
    }
}
