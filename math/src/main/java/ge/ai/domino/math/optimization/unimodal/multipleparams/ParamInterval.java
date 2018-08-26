package ge.ai.domino.math.optimization.unimodal.multipleparams;

public class ParamInterval {

    private double left;

    private double right;

    public ParamInterval(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }
}
