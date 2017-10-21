package ge.ai.domino.domain.played;

import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PlayedMove")
public class PlayedMove {

    private MoveType type;

    private MoveDirection direction;

    private int left;

    private int right;

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public void setDirection(MoveDirection direction) {
        this.direction = direction;
    }

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
}
