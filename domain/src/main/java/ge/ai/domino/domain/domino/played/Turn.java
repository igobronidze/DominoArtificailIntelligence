package ge.ai.domino.domain.domino.played;

import ge.ai.domino.domain.domino.game.PlayDirection;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Turn")
public class Turn {

    private TurnType type;

    private PlayDirection direction;

    private int x;

    private int y;

    public TurnType getType() {
        return type;
    }

    public void setType(TurnType type) {
        this.type = type;
    }

    public PlayDirection getDirection() {
        return direction;
    }

    public void setDirection(PlayDirection direction) {
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
}
