package ge.ai.domino.domain.game.opponentplay;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "OpponentTile")
public class OpponentTile {

    private int left;

    private int right;

    private double probability;

    public OpponentTile() {
    }

    public OpponentTile(int left, int right, double probability) {
        this.left = left;
        this.right = right;
        this.probability = probability;
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

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
