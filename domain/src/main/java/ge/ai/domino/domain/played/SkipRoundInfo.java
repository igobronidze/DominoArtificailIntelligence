package ge.ai.domino.domain.played;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SkipRoundInfo")
public class SkipRoundInfo {

	private int myPoint;

	private int opponentPoint;

	private int leftTiles;

	private boolean startMe;

	private boolean finishGame;

	public int getMyPoint() {
		return myPoint;
	}

	public void setMyPoint(int myPoint) {
		this.myPoint = myPoint;
	}

	public int getOpponentPoint() {
		return opponentPoint;
	}

	public void setOpponentPoint(int opponentPoint) {
		this.opponentPoint = opponentPoint;
	}

	public int getLeftTiles() {
		return leftTiles;
	}

	public void setLeftTiles(int leftTiles) {
		this.leftTiles = leftTiles;
	}

	public boolean isStartMe() {
		return startMe;
	}

	public void setStartMe(boolean startMe) {
		this.startMe = startMe;
	}

	public boolean isFinishGame() {
		return finishGame;
	}

	public void setFinishGame(boolean finishGame) {
		this.finishGame = finishGame;
	}

	@Override
	public String toString() {
		return "SkipRoundInfo{" +
				"myPoint=" + myPoint +
				", opponentPoint=" + opponentPoint +
				", leftTiles=" + leftTiles +
				", startMe=" + startMe +
				", finishGame=" + finishGame +
				'}';
	}
}
