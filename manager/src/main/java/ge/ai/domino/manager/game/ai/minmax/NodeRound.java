package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedMove;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class NodeRound {

	private int id;

	private Round round;

	private NodeRound parent;

	private List<NodeRound> children = new ArrayList<>();

	private List<NodeRound> bazaarNodeRounds = new ArrayList<>();

	private int treeHeight;

	private Double heuristic;

	private PlayedMove lastPlayedMove;

	private double lastPlayedProbability;

	private Map<Tile, Double> opponentTilesClone;

	private int descendant;

	public void addDescendant(int descendant) {
		this.descendant += descendant;
	}
}
