package ge.ai.domino.domain.heuristic;

import ge.ai.domino.domain.game.Round;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Heuristic {

	private RoundHeuristicType type;

	private double value;

	private double aiValue;

	private Round round;
}
