package ge.ai.domino.domain.played;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@XmlType(name = "SkipRoundInfo")
public class SkipRoundInfo {

	private int myPoint;

	private int opponentPoint;

	private int leftTiles;

	private boolean startMe;

	private boolean finishGame;

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
