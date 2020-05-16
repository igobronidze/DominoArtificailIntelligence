package ge.ai.domino.domain.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Tile implements Serializable {

	public static final String DELIMITER = "-";

	private final int left;

	private final int right;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tile)) return false;

		Tile tile = (Tile) o;

		return left == tile.left && right == tile.right;

	}

	@Override
	public int hashCode() {
		int result = left;
		result = 7 * result + right;
		return result;
	}

	@Override
	public String toString() {
		return left + DELIMITER + right;
	}
}
