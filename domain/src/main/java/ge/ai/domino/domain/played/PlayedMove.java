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

    private SkipRoundInfo skipRoundInfo;

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

    public SkipRoundInfo getSkipRoundInfo() {
        return skipRoundInfo;
    }

    public void setSkipRoundInfo(SkipRoundInfo skipRoundInfo) {
        this.skipRoundInfo = skipRoundInfo;
    }

    @Override
    public String toString() {
        return  "type=" + (type == null ? "N" : type) +
                ", direction=" + (direction == null ? "N" : direction) +
                ", left=" + left +
                ", right=" + right +
                ((skipRoundInfo == null) ? "" : ", " + skipRoundInfo.toString());
    }
}
