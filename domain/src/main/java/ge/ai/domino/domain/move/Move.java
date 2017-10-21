package ge.ai.domino.domain.move;

public class Move {

    private final int left;

    private final int right;

    private final MoveDirection direction;

    public Move(int left, int right, MoveDirection direction) {
        this.left = left;
        this.right = right;
        this.direction = direction;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move that = (Move) o;
        return left == that.left && right == that.right && direction == that.direction;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + right;
        result = 31 * result + direction.hashCode();
        return result;
    }
}
