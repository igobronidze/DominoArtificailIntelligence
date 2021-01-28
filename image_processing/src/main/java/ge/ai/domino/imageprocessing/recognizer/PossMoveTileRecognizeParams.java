package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.Point;
import lombok.Getter;

@Getter
public class PossMoveTileRecognizeParams {

    private Point topLeft;

    private Point bottomRight;

    private int contourMinArea;

    public PossMoveTileRecognizeParams topLeft(Point playedContentTopLeft) {
        this.topLeft = playedContentTopLeft;
        return this;
    }

    public PossMoveTileRecognizeParams bottomRight(Point playedContentBottomRight) {
        this.bottomRight = playedContentBottomRight;
        return this;
    }

    public PossMoveTileRecognizeParams contourMinArea(int contourMinArea) {
        this.contourMinArea = contourMinArea;
        return this;
    }
}
