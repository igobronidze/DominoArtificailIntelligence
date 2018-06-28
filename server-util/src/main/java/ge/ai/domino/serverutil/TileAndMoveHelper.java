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

	public static int hashForPlayedTile(PlayedTile playedTile) {
		int p = 10;
		return (playedTile.getOpenSide() + 1) * (playedTile.isTwin() ? p : 1);
	}

	public static boolean equalWithHash(Move move, PlayedMove playedMove, TableInfo tableInfo) {
		if (move.getLeft() != playedMove.getLeft() || move.getRight() != playedMove.getRight()) {
			return false;
		}
		if (move.getDirection() == playedMove.getDirection()) {
			return true;
		}
		PlayedTile playedTile = getPlayedTile(tableInfo, playedMove.getDirection());
		PlayedTile mayBePlayedTile = getPlayedTile(tableInfo, move.getDirection());
		return playedTile != null && mayBePlayedTile != null && hashForPlayedTile(playedTile) == hashForPlayedTile(mayBePlayedTile);
	}

	private static PlayedTile getPlayedTile(TableInfo tableInfo, MoveDirection moveDirection) {
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
