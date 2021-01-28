package ge.ai.domino.imageprocessing.service.rectangle;

import ge.ai.domino.imageprocessing.service.Point;
import lombok.Getter;

@Getter
public class Rectangle {

    private Point topLeft;

    private Point bottomRight;

    public Rectangle(Point point) {
        topLeft = point;
        bottomRight = point;
    }

    public void addPoint(Point point) {
        if (point.getX() < topLeft.getX()) {
            topLeft.setX(point.getX());
        }
        if (point.getY() < topLeft.getY()) {
            topLeft.setY(point.getY());
        }
        if (point.getX() > bottomRight.getX()) {
            bottomRight.setX(point.getX());
        }
        if (point.getY() > bottomRight.getY()) {
            bottomRight.setY(point.getY());
        }
    }
}
