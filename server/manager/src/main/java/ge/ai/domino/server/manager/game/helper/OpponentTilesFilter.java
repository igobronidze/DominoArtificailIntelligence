package ge.ai.domino.server.manager.game.helper;

import ge.ai.domino.domain.game.Tile;

import java.util.Map;
import java.util.Set;

public class OpponentTilesFilter {

	private boolean opponent;

	private boolean notOpponent;

	private boolean bazaar;

	private boolean notBazaar;

	private boolean twin;

	private Set<Integer> notUsedNumbers;

	private Set<Integer> mustUsedNumbers;

	private Integer leftMoreThan;

	public OpponentTilesFilter opponent(boolean opponent) {
		this.opponent = opponent;
		return this;
	}

	public OpponentTilesFilter notOpponent(boolean notOpponent) {
		this.notOpponent = notOpponent;
		return this;
	}

	public OpponentTilesFilter bazaar(boolean bazaar) {
		this.bazaar = bazaar;
		return this;
	}

	public OpponentTilesFilter notBazaar(boolean notBazaar) {
		this.notBazaar = notBazaar;
		return this;
	}

	public OpponentTilesFilter twin(boolean twin) {
		this.twin = twin;
		return this;
	}

	public OpponentTilesFilter notUsedNumber(Set<Integer> notUsedNumbers) {
		this.notUsedNumbers = notUsedNumbers;
		return this;
	}

	public OpponentTilesFilter mustUsedNumbers(Set<Integer> mustUsedNumbers) {
		this.mustUsedNumbers = mustUsedNumbers;
		return this;
	}

	public OpponentTilesFilter leftMoreThan(Integer leftMoreThan) {
		this.leftMoreThan = leftMoreThan;
		return this;
	}

	public boolean filter(Map.Entry<Tile, Float> entry) {
		Float prob = entry.getValue();
		Tile tile = entry.getKey();
		if (opponent && prob != 1.0F) {
			return false;
		}
		if (notOpponent && prob == 1.0F) {
			return false;
		}
		if (bazaar && prob != 0.0F) {
			return false;
		}
		if (notBazaar && prob == 0.0F) {
			return false;
		}
		if (twin && tile.getLeft() != tile.getRight()) {
			return false;
		}
		if (notUsedNumbers != null) {
			if (notUsedNumbers.contains(tile.getLeft()) || notUsedNumbers.contains(tile.getRight())) {
				return false;
			}
		}
		if (mustUsedNumbers != null) {
			if (!mustUsedNumbers.contains(tile.getLeft()) && !mustUsedNumbers.contains(tile.getRight())) {
				return false;
			}
		}
		if (leftMoreThan != null) {
			if (tile.getLeft() <= leftMoreThan) {
				return false;
			}
		}
		return true;
	}
}