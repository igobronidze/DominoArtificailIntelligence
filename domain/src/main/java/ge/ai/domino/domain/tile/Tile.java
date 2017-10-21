package ge.ai.domino.domain.tile;

public class Tile {

    private final int left;

    private final int right;

    public Tile(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;
        return left == tile.left && right == tile.right;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + right;
        return result;
    }

    @Override
    public String toString() {
        return left + "-" + right;
    }
}
