package ge.ai.domino.domain.game.ai;

import ge.ai.domino.domain.move.Move;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AiPrediction implements Serializable {

    private Move move;

    private double heuristicValue;

    private double realHeuristic;

    private double moveProbability;

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
