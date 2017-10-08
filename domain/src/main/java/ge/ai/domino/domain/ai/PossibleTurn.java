package ge.ai.domino.domain.ai;

import ge.ai.domino.domain.domino.PlayDirection;

public class PossibleTurn {

    private int x;

    private int y;

    private PlayDirection direction;

    public PossibleTurn() {}

    public PossibleTurn(int x, int y, PlayDirection direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

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

    public PlayDirection getDirection() {
        return direction;
    }

    public void setDirection(PlayDirection direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PossibleTurn that = (PossibleTurn) o;

        return x == that.x && y == that.y && direction == that.direction;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + direction.hashCode();
        return result;
    }
}
