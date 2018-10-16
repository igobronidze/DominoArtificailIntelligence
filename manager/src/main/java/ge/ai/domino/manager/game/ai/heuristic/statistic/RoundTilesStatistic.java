package ge.ai.domino.manager.game.ai.heuristic.statistic;

import java.util.HashMap;
import java.util.Map;

public class RoundTilesStatistic {

	private Map<Integer, Double> onTable = new HashMap<>();

	private Map<Integer, Double> forMe = new HashMap<>();

	private Map<Integer, Double> forOpponent = new HashMap<>();

	private Map<Integer, Double> forBazaar = new HashMap<>();

	public Map<Integer, Double> getOnTable() {
		return onTable;
	}

	public void setOnTable(Map<Integer, Double> onTable) {
		this.onTable = onTable;
	}

	public Map<Integer, Double> getForMe() {
		return forMe;
	}

	public void setForMe(Map<Integer, Double> forMe) {
		this.forMe = forMe;
	}

	public Map<Integer, Double> getForOpponent() {
		return forOpponent;
	}

	public void setForOpponent(Map<Integer, Double> forOpponent) {
		this.forOpponent = forOpponent;
	}

	public Map<Integer, Double> getForBazaar() {
		return forBazaar;
	}

	public void setForBazaar(Map<Integer, Double> forBazaar) {
		this.forBazaar = forBazaar;
	}
}