package ge.ai.domino.domain.heuristic;

import ge.ai.domino.domain.game.Round;

public class Heuristic {

	private RoundHeuristicType type;

	private double value;

	private double aiValue;

	private Round round;

	public RoundHeuristicType getType() {
		return type;
	}

	public void setType(RoundHeuristicType type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getAiValue() {
		return aiValue;
	}

	public void setAiValue(double aiValue) {
		this.aiValue = aiValue;
	}

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}
}
