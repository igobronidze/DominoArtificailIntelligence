package ge.ai.domino.domain.tile;

public class PlayedTile {

    private int openSide;

    private boolean twin;

    private boolean considerInSum;

    private boolean center;

    public PlayedTile() {
    }

    public PlayedTile(int openSide, boolean twin, boolean considerInSum, boolean center) {
        this.openSide = openSide;
        this.twin = twin;
        this.considerInSum = considerInSum;
        this.center = center;
    }

    public int getOpenSide() {
        return openSide;
    }

    public void setOpenSide(int openSide) {
        this.openSide = openSide;
    }

    public boolean isTwin() {
        return twin;
    }

    public void setTwin(boolean twin) {
        this.twin = twin;
    }

    public boolean isConsiderInSum() {
        return considerInSum;
    }

    public void setConsiderInSum(boolean considerInSum) {
        this.considerInSum = considerInSum;
    }

    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }
}
