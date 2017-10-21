package ge.ai.domino.domain.ai;

import ge.ai.domino.domain.move.MoveDirection;

public class AIPrediction {

    private int left;

    private int right;

    private MoveDirection direction;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public void setDirection(MoveDirection direction) {
        this.direction = direction;
    }
}
