package ge.ai.domino.domain.move;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Move implements Serializable {

    private final int left;

    private final int right;

    private final MoveDirection direction;

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
        if (direction != null) {
            result = 31 * result + direction.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return left + "-" + right + " " + direction;
    }
}
