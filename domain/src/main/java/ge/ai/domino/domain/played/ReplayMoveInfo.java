package ge.ai.domino.domain.played;

import ge.ai.domino.domain.move.Move;

public class ReplayMoveInfo {

	private int gameId;

	private PlayedMove previousMove;

	private PlayedMove nextMove;

	private int moveIndex;

	private Move aiPrediction;

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

	public Move getAiPrediction() {
		return aiPrediction;
	}

	public void setAiPrediction(Move aiPrediction) {
		this.aiPrediction = aiPrediction;
	}
}
