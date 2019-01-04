package ge.ai.domino.manager;

import ge.ai.domino.domain.move.MoveDirection;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Move")
public class Move {

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

    @Override
    public String toString() {
        return left + "-" + right + " " + direction;
    }
}
