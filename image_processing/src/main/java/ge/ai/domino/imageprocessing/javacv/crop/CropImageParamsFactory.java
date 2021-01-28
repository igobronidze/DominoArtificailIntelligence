package ge.ai.domino.imageprocessing.javacv.crop;

import ge.ai.domino.imageprocessing.service.Point;

public class CropImageParamsFactory {

    public static CropImageParams getCropImageParams(Point topLeft, Point bottomRight) {
        return new CropImageParams()
                .positionX(topLeft.getX())
                .positionY(topLeft.getY())
                .width(bottomRight.getX() - topLeft.getX())
                .height(bottomRight.getY() - topLeft.getY());
    }
}
