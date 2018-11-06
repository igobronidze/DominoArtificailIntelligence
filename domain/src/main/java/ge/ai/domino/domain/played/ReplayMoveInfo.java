package ge.ai.domino.domain.played;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.move.Move;

import java.util.List;

public class ReplayMoveInfo {

	private int gameId;

	private PlayedMove previousMove;

	private PlayedMove nextMove;

	private int moveIndex;

	private Move bestAiPrediction;

	private List<AiPrediction> aiPredictions;

	private Round round;

	private double heuristicValue;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public PlayedMove getPreviousMove() {
		return previousMove;
	}

	public void setPreviousMove(PlayedMove previousMove) {
		this.previousMove = previousMove;
	}

	public PlayedMove getNextMove() {
		return nextMove;
	}

	public void setNextMove(PlayedMove nextMove) {
		this.nextMove = nextMove;
	}

	public int getMoveIndex() {
		return moveIndex;
	}

	public void setMoveIndex(int moveIndex) {
		this.moveIndex = moveIndex;
	}

	public Move getBestAiPrediction() {
		return bestAiPrediction;
	}

	public void setBestAiPrediction(Move bestAiPrediction) {
		this.bestAiPrediction = bestAiPrediction;
	}

	public List<AiPrediction> getAiPredictions() {
		return aiPredictions;
	}

	public void setAiPredictions(List<AiPrediction> aiPredictions) {
		this.aiPredictions = aiPredictions;
	}

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

	public double getHeuristicValue() {
		return heuristicValue;
	}

	public void setHeuristicValue(double heuristicValue) {
		this.heuristicValue = heuristicValue;
	}
}
