package ge.ai.domino.domain.played;

public class ReplayMoveInfo {

	private int gameId;

	private PlayedMove previousMove;

	private PlayedMove nextMove;

	private int moveIndex;

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
}
