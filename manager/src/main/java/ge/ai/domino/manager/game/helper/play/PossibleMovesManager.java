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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PossibleMovesManager {

	private static final String MOVE_PRIORITY_KEY = "movePriority";

	private static final String MOVE_PRIORITY_DELIMITER = ",";

	private static final String MOVE_PRIORITY_DEFAULT_VALUE = "LEFT,RIGHT,TOP,BOTTOM";

	private static final Map<Integer, List<MoveDirection>> cachedMoveDirections = new HashMap<>();

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
					addPossibleMovesForTile(round, tile, left, right, top, bottom, moves, allMove);
				}
			} else {
				round.getOpponentTiles().entrySet().stream().filter(entry -> entry.getValue() > 0.0).forEach(
						entry -> addPossibleMovesForTile(round, entry.getKey(), left, right, top, bottom, moves, allMove));
			}
		}
		return moves;
	}

	public static List<MoveDirection> getMovePriority(int gameId) {
		if (!cachedMoveDirections.containsKey(gameId)) {
			Map<String, String> params = CachedGames.getGameProperties(gameId).getChannel().getParams();
			String movePriority = params.getOrDefault(MOVE_PRIORITY_KEY, MOVE_PRIORITY_DEFAULT_VALUE);

			List<MoveDirection> moveDirections = new ArrayList<>();
			for (String direction : movePriority.split(MOVE_PRIORITY_DELIMITER)) {
				moveDirections.add(MoveDirection.valueOf(direction));
			}
			cachedMoveDirections.put(gameId, moveDirections);
		}
		return cachedMoveDirections.get(gameId);
	}

	private static void addPossibleMovesForTile(Round round, Tile tile, PlayedTile left, PlayedTile right, PlayedTile top, PlayedTile bottom, List<Move> moves, boolean allMove) {
		Set<Integer> played = new HashSet<>();

		for (MoveDirection moveDirection : getMovePriority(round.getGameInfo().getGameId())) {
			switch (moveDirection) {
				case LEFT:
					addLeftPossibleMove(tile, left, moves, played, allMove, round.getTableInfo().isWithCenter());
					break;
				case RIGHT:
					addRightPossibleMove(tile, right, moves, played, allMove, round.getTableInfo().isWithCenter());
					break;
				case TOP:
					addTopPossibleMove(tile, top, left, right, moves, played, allMove, round.getTableInfo().isWithCenter());
					break;
				case BOTTOM:
					addBottomPossibleMove(tile, bottom, left, right, moves,played, allMove, round.getTableInfo().isWithCenter());
					break;
			}
		}
	}

	private static void addLeftPossibleMove(Tile tile, PlayedTile left, List<Move> moves, Set<Integer> played, boolean allMove, boolean withCenter) {
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(left, withCenter))) {
			if (left.getOpenSide() == tile.getLeft() || left.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.LEFT));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(left, withCenter));
				}
			}
		}
	}

	private static void addRightPossibleMove(Tile tile, PlayedTile right, List<Move> moves, Set<Integer> played, boolean allMove, boolean withCenter) {
		if (!played.contains(TileAndMoveHelper.hashForPlayedTile(right, withCenter))) {
			if (right.getOpenSide() == tile.getLeft() || right.getOpenSide() == tile.getRight()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.RIGHT));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(right, withCenter));
				}
			}
		}
	}

	private static void addTopPossibleMove(Tile tile, PlayedTile top, PlayedTile left, PlayedTile right, List<Move> moves, Set<Integer> played, boolean allMove, boolean withCenter) {
		if (top != null && !played.contains(TileAndMoveHelper.hashForPlayedTile(top, withCenter))) {
			if ((top.getOpenSide() == tile.getLeft() || top.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.TOP));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(top, withCenter));
				}
			}
		}
	}

	private static void addBottomPossibleMove(Tile tile, PlayedTile bottom, PlayedTile left, PlayedTile right, List<Move> moves, Set<Integer> played, boolean allMove, boolean withCenter) {
		if (bottom != null && !played.contains(TileAndMoveHelper.hashForPlayedTile(bottom, withCenter))) {
			if ((bottom.getOpenSide() == tile.getLeft() || bottom.getOpenSide() == tile.getRight()) && !left.isCenter() && !right.isCenter()) {
				moves.add(TileAndMoveHelper.getMove(tile, MoveDirection.BOTTOM));
				if (!allMove) {
					played.add(TileAndMoveHelper.hashForPlayedTile(bottom, withCenter));
				}
			}
		}
	}
}
