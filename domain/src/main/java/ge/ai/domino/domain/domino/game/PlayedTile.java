package ge.ai.domino.domain.domino.game;

public class PlayedTile {

    private int openSide;

    private boolean isDouble;

    private boolean countInSum;

    private boolean center;

    public PlayedTile() {
    }

    public PlayedTile(int openSide, boolean isDouble, boolean countInSum, boolean center) {
        this.openSide = openSide;
        this.isDouble = isDouble;
        this.countInSum = countInSum;
        this.center = center;
    }

    public int getOpenSide() {
        return openSide;
    }

    public void setOpenSide(int openSide) {
        this.openSide = openSide;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public void setDouble(boolean aDouble) {
        isDouble = aDouble;
    }

    public boolean isCountInSum() {
        return countInSum;
    }

    public void setCountInSum(boolean countInSum) {
        this.countInSum = countInSum;
    }

    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }
}
