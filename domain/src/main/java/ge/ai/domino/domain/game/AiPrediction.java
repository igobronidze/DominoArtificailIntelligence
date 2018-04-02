package ge.ai.domino.domain.game;

import ge.ai.domino.domain.move.Move;

public class AiPrediction {

    private Move move;

    private float heuristicValue;

    private boolean bestMove;

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public float getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(float heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public boolean isBestMove() {
        return bestMove;
    }

    public void setBestMove(boolean bestMove) {
        this.bestMove = bestMove;
    }
}
