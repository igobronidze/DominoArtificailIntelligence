package ge.ai.domino.domain.domino;

import java.util.HashMap;
import java.util.Map;

public class Hand {

    private boolean myTurn;

    private Map<String, Tile> tiles = new HashMap<>();

    private int tilesInBazaar;

    private boolean hasCenter;

    private Integer top;

    private Integer right;

    private Integer bottom;

    private Integer left;

    private BestPrediction bestPrediction;

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public Map<String, Tile> getTiles() {
        return tiles;
    }

    public void setTiles(Map<String, Tile> tiles) {
        this.tiles = tiles;
    }

    public int getTilesInBazaar() {
        return tilesInBazaar;
    }

    public void setTilesInBazaar(int tilesInBazaar) {
        this.tilesInBazaar = tilesInBazaar;
    }

    public boolean isHasCenter() {
        return hasCenter;
    }

    public void setHasCenter(boolean hasCenter) {
        this.hasCenter = hasCenter;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    public Integer getBottom() {
        return bottom;
    }

    public void setBottom(Integer bottom) {
        this.bottom = bottom;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public BestPrediction getBestPrediction() {
        return bestPrediction;
    }

    public void setBestPrediction(BestPrediction bestPrediction) {
        this.bestPrediction = bestPrediction;
    }
}
