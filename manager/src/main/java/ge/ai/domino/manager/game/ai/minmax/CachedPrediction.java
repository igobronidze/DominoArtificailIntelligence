package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.move.Move;

import java.util.HashMap;
import java.util.Map;

public class CachedPrediction {

    private Map<Move, CachedPrediction> children = new HashMap<>();

    private Move move;

    private double heuristicValue;

    public Map<Move, CachedPrediction> getChildren() {
        return children;
    }

    public void setChildren(Map<Move, CachedPrediction> children) {
        this.children = children;
    }

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
}
