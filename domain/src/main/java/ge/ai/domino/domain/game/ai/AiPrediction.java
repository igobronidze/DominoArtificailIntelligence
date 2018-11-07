package ge.ai.domino.domain.game.ai;

import ge.ai.domino.domain.move.Move;

import java.io.Serializable;

public class AiPrediction implements Serializable {

    private Move move;

    private double heuristicValue;

    private double realHeuristic;

    private double moveProbability;

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public double getRealHeuristic() {
        return realHeuristic;
    }

    public void setRealHeuristic(double realHeuristic) {
        this.realHeuristic = realHeuristic;
    }

    public double getMoveProbability() {
        return moveProbability;
    }

    public void setMoveProbability(double moveProbability) {
        this.moveProbability = moveProbability;
    }

    @Override
    public String toString() {
        return "AiPrediction{" +
                "move=" + move +
                ", heuristicValue=" + heuristicValue +
                ", realHeuristic=" + realHeuristic +
                ", moveProbability=" + moveProbability +
                '}';
    }
}
