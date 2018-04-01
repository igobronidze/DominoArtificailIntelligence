package ge.ai.domino.domain.game;

import ge.ai.domino.domain.played.PlayedMove;

public class ParentRound {

    private Round parent;

    private PlayedMove move;

    private float probability;

    private int height;

    public ParentRound() {
    }

    public ParentRound(Round parent, PlayedMove move, float probability, int height) {
        this.parent = parent;
        this.move = move;
        this.probability = probability;
        this.height = height;
    }

    public Round getParent() {
        return parent;
    }

    public void setParent(Round parent) {
        this.parent = parent;
    }

    public PlayedMove getMove() {
        return move;
    }

    public void setMove(PlayedMove move) {
        this.move = move;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
