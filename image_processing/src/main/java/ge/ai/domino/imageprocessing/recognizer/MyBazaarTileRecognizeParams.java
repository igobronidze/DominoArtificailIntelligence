package ge.ai.domino.imageprocessing.recognizer;

import lombok.Getter;

@Getter
public class MyBazaarTileRecognizeParams {

    private int screenWidth;

    private int tilesCount;

    private int tileWidth;

    private int tilesSpacing;

    private int tileTop;

    private int tileBottom;

    public MyBazaarTileRecognizeParams screenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
        return this;
    }

    public MyBazaarTileRecognizeParams tilesCount(int tilesCount) {
        this.tilesCount = tilesCount;
        return this;
    }

    public MyBazaarTileRecognizeParams tileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
        return this;
    }

    public MyBazaarTileRecognizeParams tilesSpacing(int tilesSpacing) {
        this.tilesSpacing = tilesSpacing;
        return this;
    }

    public MyBazaarTileRecognizeParams tileTop(int tileTop) {
        this.tileTop = tileTop;
        return this;
    }

    public MyBazaarTileRecognizeParams tileBottom(int tileBottom) {
        this.tileBottom = tileBottom;
        return this;
    }
}
