package ge.ai.domino.serverutil;

import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedMove;
import ge.ai.domino.domain.played.PlayedTile;

public class TileAndMoveHelper {

	public static Move getMove(Tile tile, MoveDirection direction) {
		return new Move(tile.getLeft(), tile.getRight(), direction);
	}

	public static Move getMove(PlayedMove playedMove) {
		return new Move(playedMove.getLeft(), playedMove.getRight(), playedMove.getDirection());
	}

	public static int hashForPlayedTile(PlayedTile playedTile, boolean zeroTwinEqualNotTwin) {
		int p = 10;
		if (zeroTwinEqualNotTwin) {
			return playedTile.getOpenSide() * (playedTile.isTwin() ? p : 1);
		} else {
			return (playedTile.getOpenSide() + 1) * (playedTile.isTwin() ? p : 1);
		}
	}

	public static boolean equalWithHash(Move move1, Move move2, TableInfo tableInfo) {
		if (move1.getLeft() != move2.getLeft() || move1.getRight() != move2.getRight()) {
			return false;
		}
		if (move1.getDirection() == move2.getDirection()) {
			return true;
		}
		PlayedTile playedTile = getPlayedTile(tableInfo, move2.getDirection());
		PlayedTile mayBePlayedTile = getPlayedTile(tableInfo, move1.getDirection());
		return playedTile != null && mayBePlayedTile != null && hashForPlayedTile(playedTile, false) == hashForPlayedTile(mayBePlayedTile, false);
	}

	public static PlayedTile getPlayedTile(TableInfo tableInfo, MoveDirection moveDirection) {
		switch (moveDirection) {
			case LEFT:
				return tableInfo.getLeft();
			case RIGHT:
				return tableInfo.getRight();
			case TOP:
				return tableInfo.getTop();
			case BOTTOM:
				return tableInfo.getBottom();
			default:
				return null;
		}
	}
}
