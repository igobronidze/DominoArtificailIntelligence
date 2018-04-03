package ge.ai.domino.server.manager.game.ai.minmax;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.played.PlayedMove;

import java.util.ArrayList;
import java.util.List;

public class NodeRound {

	private Round round;

	private NodeRound parent;

	private List<NodeRound> children = new ArrayList<>();

	private int treeHeight;

	private float heuristic;

	private PlayedMove lastPlayedMove;

	private float lastPlayedProbability;

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

	public float getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(float heuristic) {
		this.heuristic = heuristic;
	}

	public PlayedMove getLastPlayedMove() {
		return lastPlayedMove;
	}

	public void setLastPlayedMove(PlayedMove lastPlayedMove) {
		this.lastPlayedMove = lastPlayedMove;
	}

	public float getLastPlayedProbability() {
		return lastPlayedProbability;
	}

	public void setLastPlayedProbability(float lastPlayedProbability) {
		this.lastPlayedProbability = lastPlayedProbability;
	}
}
