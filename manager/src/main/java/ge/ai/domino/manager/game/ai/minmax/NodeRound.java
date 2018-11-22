package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedMove;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

	public NodeRound getParent() {
		return parent;
	}

	public void setParent(NodeRound parent) {
		this.parent = parent;
	}

	public List<NodeRound> getChildren() {
		return children;
	}

	public List<NodeRound> getBazaarNodeRounds() {
		return bazaarNodeRounds;
	}

	public int getTreeHeight() {
		return treeHeight;
	}

	public void setTreeHeight(int treeHeight) {
		this.treeHeight = treeHeight;
	}

	public Double getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(Double heuristic) {
		this.heuristic = heuristic;
	}

	public PlayedMove getLastPlayedMove() {
		return lastPlayedMove;
	}

	public void setLastPlayedMove(PlayedMove lastPlayedMove) {
		this.lastPlayedMove = lastPlayedMove;
	}

	public double getLastPlayedProbability() {
		return lastPlayedProbability;
	}

	public void setLastPlayedProbability(double lastPlayedProbability) {
		this.lastPlayedProbability = lastPlayedProbability;
	}

	public Map<Tile, Double> getOpponentTilesClone() {
		return opponentTilesClone;
	}

	public void setOpponentTilesClone(Map<Tile, Double> opponentTilesClone) {
		this.opponentTilesClone = opponentTilesClone;
	}

	public int getDescendant() {
		return descendant;
	}

	public void addDescendant(int descendant) {
		this.descendant += descendant;
	}
}
