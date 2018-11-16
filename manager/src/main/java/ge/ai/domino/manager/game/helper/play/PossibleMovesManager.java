package ge.ai.domino.manager.game.helper.play;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.serverutil.TileAndMoveHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PossibleMovesManager {

	private static final String MOVE_PRIORITY_KEY = "movePriority";

	private static final String MOVE_PRIORITY_DELIMITER = ",";

	private static final String MOVE_PRIORITY_DEFAULT_VALUE = "LEFT,RIGHT,TOP,BOTTOM";

	public static List<Move> getPossibleMoves(Round round, boolean allMove) {
		List<Move> moves = new ArrayList<>();
		TableInfo tableInfo = round.getTableInfo();
		PlayedTile left = tableInfo.getLeft();
		PlayedTile right = tableInfo.getRight();
		PlayedTile top = tableInfo.getTop();
		PlayedTile bottom = tableInfo.getBottom();
		// First move
		if (tableInfo.getLeft() == null) {
			moves.addAll(round.getMyTiles().stream().map(tile -> TileAndMoveHelper.getMove(tile, MoveDirection.LEFT)).collect(Collectors.toList()));
		} else {
			if (round.getTableInfo().isMyMove()) {
				for (Tile tile : round.getMyTiles()) {
					addPossibleMovesForTile(round.getGameInfo().getGameId(), tile, left, right, top, bottom, moves, allMove);
				}
			} else {
				round.getOpponentTiles().entrySet().stream().filter(entry -> entry.getValue() > 0.0).forEach(
						entry -> addPossibleMovesForTile(round.getGameInfo().getGameId(), entry.getKey(), left, right, top, bottom, moves, allMove));
			}
		}
		return moves;
	}

	private static void addPossibleMovesForTile(int gameId, Tile tile, PlayedTile left, PlayedTile right, PlayedTile top, PlayedTile bottom, List<Move> moves, boolean allMove) {
		Set<Integer> played = new HashSet<>();

		for (MoveDirection moveDirection : getMovePriority(gameId)) {
			switch (moveDirection) {
				case LEFT:
					addLeftPossibleMove(tile, left, moves, played, allMove);
					break;
				case RIGHT:
					addRightPossibleMove(tile, right, moves, played, allMove);
					break;
				case TOP:
					addTopPossibleMove(tile, top, left, right, moves, played, allMove);
					break;
				case BOTTOM:
					addBottomPossibleMove(tile, bottom, left, right, moves,played, allMove);
					break;
			}
		}
	}

	private static List<MoveDirection> getMovePriority(int gameId) {
		Map<String, String> params = CachedGames.getGameProperties(gameId).getChannel().getParams();
		String movePriority = params.getOrDefault(MOVE_PRIORITY_KEY, MOVE_PRIORITY_DEFAULT_VALUE);

		List<MoveDirection> moveDirections = new ArrayList<>();
		for (String direction : movePriority.split(MOVE_PRIORITY_DELIMITER)) {
			moveDirections.add(MoveDirection.valueOf(direction));
		}
		return moveDirections;
	}

	private static void addLeftPossibleMove(Tile tile, PlayedTile left, List<Move> moves, Set<Integer> played, boolean allMove) {
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(left))) {
			if (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.LEFT));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(left));
				}
			}
		}
	}

	private static void addRightPossibleMove(Tile tile, PlayedTile right, List<Move> moves, Set<Integer> played, boolean allMove) {
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(right))) {
			if (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.RIGHT));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(right));
				}
			}
		}
	}

	private static void addTopPossibleMove(Tile tile, PlayedTile top, PlayedTile left, PlayedTile right, List<Move> moves, Set<Integer> played, boolean allMove) {
		if (top != null && !played.contains(TileAndMoveHelper.hashForPlayedTile(top))) {
			if ((top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.TOP));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(top));
				}
			}
		}
	}

	private static void addBottomPossibleMove(Tile tile, PlayedTile bottom, PlayedTile left, PlayedTile right, List<Move> moves, Set<Integer> played, boolean allMove) {
		if (bottom != null && !played.contains(TileAndMoveHelper.hashForPlayedTile(bottom))) {
			if ((bottom.getOpenSide() == tile.getLeft() || bottom.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.BOTTOM));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(bottom));
				}
			}
		}
	}
}
