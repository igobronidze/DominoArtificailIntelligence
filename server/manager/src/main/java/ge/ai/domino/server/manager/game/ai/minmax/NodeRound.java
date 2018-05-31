package ge.ai.domino.server.manager.game.ai.minmax;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedMove;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeRound {

	private Round round;

	private NodeRound parent;

	private List<NodeRound> children = new ArrayList<>();

	private NodeRound bazaarNodeRound;

	private int treeHeight;

	private Double heuristic;

	private PlayedMove lastPlayedMove;

	private double lastPlayedProbability;

	private Map<Tile, Double> opponentTilesClone;

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

	public void setChildren(List<NodeRound> children) {
		this.children = children;
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

	public NodeRound getBazaarNodeRound() {
		return bazaarNodeRound;
	}

	public void setBazaarNodeRound(NodeRound bazaarNodeRound) {
		this.bazaarNodeRound = bazaarNodeRound;
	}

	public Map<Tile, Double> getOpponentTilesClone() {
		return opponentTilesClone;
	}

	public void setOpponentTilesClone(Map<Tile, Double> opponentTilesClone) {
		this.opponentTilesClone = opponentTilesClone;
	}
}
