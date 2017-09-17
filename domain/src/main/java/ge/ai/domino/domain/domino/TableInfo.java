package ge.ai.domino.domain.domino;

public class TableInfo {

    private boolean withCenter;

    private PlayedTile top;

    private PlayedTile right;

    private PlayedTile bottom;

    private PlayedTile left;

    private boolean myTurn;

    private double himTilesCount;

    private double myTilesCount;

    private double bazaarTilesCount;

    private String lastPlayedUID;

    private boolean needToAddLeftTiles;

    public boolean isWithCenter() {
        return withCenter;
    }

    public void setWithCenter(boolean withCenter) {
        this.withCenter = withCenter;
    }

    public PlayedTile getTop() {
        return top;
    }

    public void setTop(PlayedTile top) {
        this.top = top;
    }

    public PlayedTile getRight() {
        return right;
    }

    public void setRight(PlayedTile right) {
        this.right = right;
    }

    public PlayedTile getBottom() {
        return bottom;
    }

    public void setBottom(PlayedTile bottom) {
        this.bottom = bottom;
    }

    public PlayedTile getLeft() {
        return left;
    }

    public void setLeft(PlayedTile left) {
        this.left = left;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public double getHimTilesCount() {
        return himTilesCount;
    }

    public void setHimTilesCount(double himTilesCount) {
        this.himTilesCount = himTilesCount;
    }

    public double getMyTilesCount() {
        return myTilesCount;
    }

    public void setMyTilesCount(double myTilesCount) {
        this.myTilesCount = myTilesCount;
    }

    public double getBazaarTilesCount() {
        return bazaarTilesCount;
    }

    public void setBazaarTilesCount(double bazaarTilesCount) {
        this.bazaarTilesCount = bazaarTilesCount;
    }

    public String getLastPlayedUID() {
        return lastPlayedUID;
    }

    public void setLastPlayedUID(String lastPlayedUID) {
        this.lastPlayedUID = lastPlayedUID;
    }

    public boolean isNeedToAddLeftTiles() {
        return needToAddLeftTiles;
    }

    public void setNeedToAddLeftTiles(boolean needToAddLeftTiles) {
        this.needToAddLeftTiles = needToAddLeftTiles;
    }
}
