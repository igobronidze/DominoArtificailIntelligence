package ge.ai.domino.domain.domino;

public class Tile {

    private int x;

    private int y;

    private boolean played;

    private double me;

    private double him;

    private double bazaar;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public double getMe() {
        return me;
    }

    public void setMe(double me) {
        this.me = me;
    }

    public double getHim() {
        return him;
    }

    public void setHim(double him) {
        this.him = him;
    }

    public double getBazaar() {
        return bazaar;
    }

    public void setBazaar(double bazaar) {
        this.bazaar = bazaar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        return y == tile.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}