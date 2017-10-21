package ge.ai.domino.domain.tile;

public class OpponentTile extends Tile {

    private double prob;

    public OpponentTile(int left, int right) {
        super(left, right);
    }

    public OpponentTile(int left, int right, double prob) {
        super(left, right);
        this.prob = prob;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }
}
