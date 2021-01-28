package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.Point;
import lombok.Getter;

@Getter
public class MyTileRecognizeParams {

    private Point topLeft;

    private Point bottomRight;

    private int contourMinArea;

    private int blurCoefficient;

    private boolean combinedPoints;

    public MyTileRecognizeParams topLeft(Point myTilesTopLeft) {
        this.topLeft = myTilesTopLeft;
        return this;
    }

    public MyTileRecognizeParams bottomRight(Point myTilesBottomRight) {
        this.bottomRight = myTilesBottomRight;
        return this;
    }

    public MyTileRecognizeParams contourMinArea(int contourMinArea) {
        this.contourMinArea = contourMinArea;
        return this;
    }

    public MyTileRecognizeParams blurCoefficient(int blurCoefficient) {
        this.blurCoefficient = blurCoefficient;
        return this;
    }

    public MyTileRecognizeParams combinedPoints(boolean combinedPoints) {
        this.combinedPoints = combinedPoints;
        return this;
    }
}
