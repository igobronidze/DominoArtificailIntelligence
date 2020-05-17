package ge.ai.domino.manager.game.ai.heuristic.statistic;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RoundTilesStatistic {

	private Map<Integer, Double> onTable = new HashMap<>();

	private Map<Integer, Double> forMe = new HashMap<>();

	private Map<Integer, Double> forOpponent = new HashMap<>();

	private Map<Integer, Double> forBazaar = new HashMap<>();
}