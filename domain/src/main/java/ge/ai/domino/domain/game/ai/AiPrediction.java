package ge.ai.domino.domain.game.ai;

import ge.ai.domino.domain.move.Move;

import java.io.Serializable;

public class AiPrediction implements Serializable {

    private Move move;

    private double heuristicValue;

    private boolean bestMove;

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

    @Override
    public String toString() {
        return "AiPrediction{" +
                "move=" + move +
                ", heuristicValue=" + heuristicValue +
                ", bestMove=" + bestMove +
                '}';
    }
}
