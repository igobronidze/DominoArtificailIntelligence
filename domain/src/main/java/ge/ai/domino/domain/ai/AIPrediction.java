package ge.ai.domino.domain.ai;

import ge.ai.domino.domain.domino.game.PlayDirection;

public class AIPrediction {

    private int x;

    private int y;

    private PlayDirection direction;

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
}
