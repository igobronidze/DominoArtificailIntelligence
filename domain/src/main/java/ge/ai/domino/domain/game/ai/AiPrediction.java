package ge.ai.domino.domain.game.ai;

import ge.ai.domino.domain.move.Move;

import java.io.Serializable;

public class AiPrediction implements Serializable {

    private Move move;

    private double heuristicValue;

    private boolean bestMove;

    private double realHeuristic;

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

    public boolean isBestMove() {
        return bestMove;
    }

    public void setBestMove(boolean bestMove) {
        this.bestMove = bestMove;
    }

    public double getRealHeuristic() {
        return realHeuristic;
    }

    public void setRealHeuristic(double realHeuristic) {
        this.realHeuristic = realHeuristic;
    }

    @Override
    public String toString() {
        return "AiPrediction{" +
                "move=" + move +
                ", heuristicValue=" + heuristicValue +
                ", realHeuristic=" + realHeuristic +
                ", bestMove=" + bestMove +
                '}';
    }
}
