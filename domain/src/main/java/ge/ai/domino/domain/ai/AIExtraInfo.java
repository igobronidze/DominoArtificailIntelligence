package ge.ai.domino.domain.ai;

public class AIExtraInfo {

    private int himPoints;

    private int myPoints;

    private double heuristicValue;

    public int getHimPoints() {
        return himPoints;
    }

    public void setHimPoints(int himPoints) {
        this.himPoints = himPoints;
    }

    public int getMyPoints() {
        return myPoints;
    }

    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }
}
