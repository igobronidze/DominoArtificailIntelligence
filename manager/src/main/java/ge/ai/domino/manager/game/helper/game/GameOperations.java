package ge.ai.domino.manager.game.helper.game;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.helper.filter.OpponentTilesFilter;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameOperations {

	protected static final Logger logger = Logger.getLogger(GameOperations.class);

	private static final String MOVE_PRIORITY_KEY = "movePriority";

	private static final String MOVE_PRIORITY_DELIMITER = ",";

	private static final String MOVE_PRIORITY_DEFAULT_VALUE = "LEFT,RIGHT,TOP,BOTTOM";

	private static final String FIRST_NOT_TWIN_TILE_DIRECTION_KEY = "firstNotTwinTileDirection";

	private static final String FIRST_NOT_TWIN_TILE_DIRECTION_DEFAULT_VALUE = "left";

	public static int countLeftTiles(Round round, boolean countMine, boolean virtual) {
		int gameId = round.getGameInfo().getGameId();

		double count = 0;
		if (countMine) {
			for (Tile tile : round.getMyTiles()) {
				count += tile.getLeft() + tile.getRight();
			}
		} else {
			if (virtual) {
				for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
					count += entry.getValue() * (entry.getKey().getLeft() + entry.getKey().getRight());
				}
			} else {
				count = CachedGames.getOpponentLeftTilesCount(gameId);
			}
		}
		if (count == 0) {
			count = 10;
		}
		if (!virtual) {
			logger.info("Left tiles count is " + count + ", gameId[" + gameId + "]");
		}
		return (int)count;
	}

	private static void addLeftTiles(GameInfo gameInfo, int count, boolean forMe, int gameId, boolean virtual) {
		int countFromLastRound = virtual ? 0 : CachedGames.getLeftTilesCountFromLastRound(gameId);
		if (count % 5 != 0) {
			count = normalizeLeftTilesCount(count);
		}
		if (forMe) {
			gameInfo.setMyPoint(gameInfo.getMyPoint() + count + countFromLastRound);
		} else {
			gameInfo.setOpponentPoint(gameInfo.getOpponentPoint() + count + countFromLastRound);
		}
		if (!virtual) {
			CachedGames.setLeftTilesCountFromLastRound(gameId, 0);
		}
	}

	public static Round finishedLastAndGetNewRound(Round round, boolean addForMe, int leftTilesCount, boolean virtual) {
		GameInfo gameInfo = round.getGameInfo();
		int gameId = gameInfo.getGameId();
		addLeftTiles(gameInfo, leftTilesCount, addForMe, gameId, virtual);
		int scoreForWin = CachedGames.getGameProperties(gameId).getPointsForWin();
		if (gameInfo.getMyPoint() >= scoreForWin || gameInfo.getOpponentPoint() >= scoreForWin) {
			if (gameInfo.getMyPoint() > gameInfo.getOpponentPoint()) {
				round.getGameInfo().setFinished(true);
				if (!virtual) {
					logger.info("I won the game");
				}
				return round;
			} else if (gameInfo.getOpponentPoint() > gameInfo.getMyPoint()) {
				round.getGameInfo().setFinished(true);
				if (!virtual) {
					logger.info("He won the game");
				}
				return round;
			}
		}

		Round newRound = InitialUtil.getInitialRound(0, false);
		newRound.setGameInfo(round.getGameInfo());
		if (virtual) {
			newRound.getTableInfo().setMyMove(addForMe);
		} else {
			newRound.getTableInfo().setMyMove(true); // For pick up new tiles
			if (addForMe) {
				CachedGames.changeNextRoundBeginner(gameId, true);
			} else {
				CachedGames.changeNextRoundBeginner(gameId, false);
			}
		}

		if (!virtual) {
			logger.info("Finished round and start new one, gameId[" + gameId + "]");
		}
		return newRound;
	}

	public static Round blockRound(Round round, int opponentLeftTilesCount, boolean virtual) {
		int gameId = round.getGameInfo().getGameId();

		int myLeftTilesCount = countLeftTiles(round, true, virtual);
		if (myLeftTilesCount < opponentLeftTilesCount) {
			addLeftTiles(round.getGameInfo(), opponentLeftTilesCount, true, gameId, virtual);
		} else if (myLeftTilesCount > opponentLeftTilesCount) {
			addLeftTiles(round.getGameInfo(), myLeftTilesCount, false, gameId, virtual);
		} else {
			if (!virtual) {
				CachedGames.setLeftTilesCountFromLastRound(gameId, CachedGames.getLeftTilesCountFromLastRound(gameId) + myLeftTilesCount);
			}
		}

		Round newRound = InitialUtil.getInitialRound(0, false);
		newRound.getTableInfo().setMyMove(true); // For pick up new tiles
		newRound.setGameInfo(round.getGameInfo());
		if (virtual) {
			newRound.getTableInfo().setMyMove(round.getTableInfo().getRoundBlockingInfo().isLastNotTwinPlayedTileMy());
		} else if (round.getTableInfo().getRoundBlockingInfo().isLastNotTwinPlayedTileMy()) {
			CachedGames.changeNextRoundBeginner(gameId, true);
		} else {
			CachedGames.changeNextRoundBeginner(gameId, false);
		}

		if (!virtual) {
			logger.info("Finished(blocked) round and start new one, gameId[" + gameId + "]");
		}
		return newRound;
	}

	public static double makeTilesAsBazaarAndReturnProbabilitiesSum(Round round) {
		Set<Integer> possiblePlayNumbers = getPossiblePlayNumbers(round.getTableInfo());

		OpponentTilesFilter opponentTilesFilter = new OpponentTilesFilter()
				.notBazaar(true)
				.mustUsedNumbers(possiblePlayNumbers);

		double sum = 0.0;
		for (Map.Entry<Tile, Double> entry : round.getOpponentTiles().entrySet()) {
			if (opponentTilesFilter.filter(entry)) {
				sum += entry.getValue();
				entry.setValue(0.0);
			}
		}
		return sum;
	}

	public static double makeTwinTilesAsBazaarAndReturnProbabilitiesSum(Map<Tile, Double> opponentTiles, int twinNumber) {
		OpponentTilesFilter opponentTilesFilter = new OpponentTilesFilter()
				.notBazaar(true)
				.twin(true)
				.leftMoreThan(twinNumber);

		double sum = 0.0;
		for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
			if (opponentTilesFilter.filter(entry)) {
				sum += entry.getValue();
				entry.setValue(0.0);
			}
		}
		return sum;
	}

	public static void playTile(Round round, Move move) {
		TableInfo tableInfo = round.getTableInfo();
		int left = move.getLeft();
		int right = move.getRight();

		if (tableInfo.getLeft() == null) { // First move
			if (left == right) {  // Twin
				tableInfo.setTop(new PlayedTile(left, true, false, true));
				tableInfo.setBottom(new PlayedTile(left, true, false, true));
				tableInfo.setLeft(new PlayedTile(left, true, true, true));
				tableInfo.setRight(new PlayedTile(left, true, true, true));
			} else {
				Map<String, String> params = CachedGames.getGameProperties(round.getGameInfo().getGameId()).getChannel().getParams();
				String firstNotTwinTileDirection = params.getOrDefault(FIRST_NOT_TWIN_TILE_DIRECTION_KEY, FIRST_NOT_TWIN_TILE_DIRECTION_DEFAULT_VALUE);
				if (firstNotTwinTileDirection.equals("left")) {
					tableInfo.setLeft(new PlayedTile(left, false, true, false));
					tableInfo.setRight(new PlayedTile(right, false, true, false));
				} else {
					tableInfo.setLeft(new PlayedTile(right, false, true, false));
					tableInfo.setRight(new PlayedTile(left, false, true, false));
				}
			}
		} else {
			switch (move.getDirection()) {
				case TOP:
					tableInfo.setTop(new PlayedTile(tableInfo.getTop().getOpenSide() == left ? right : left, left == right, true, false));
					break;
				case RIGHT:
					if (!tableInfo.isWithCenter()) {   // Check if become center
						PlayedTile rightTile = tableInfo.getRight();
						if (rightTile.isTwin()) {
							tableInfo.setTop(new PlayedTile(rightTile.getOpenSide(), true, false, true));
							tableInfo.setBottom(new PlayedTile(rightTile.getOpenSide(), true, false, true));
							tableInfo.setWithCenter(true);
						}
					}
					tableInfo.setRight(new PlayedTile(tableInfo.getRight().getOpenSide() == left ? right : left, left == right, true, false));
					break;
				case BOTTOM:
					tableInfo.setBottom(new PlayedTile(tableInfo.getBottom().getOpenSide() == left ? right : left, left == right, true, false));
					break;
				case LEFT:
					if (!tableInfo.isWithCenter()) {   // Check if become center
						PlayedTile leftTile = tableInfo.getLeft();
						if (leftTile.isTwin()) {
							tableInfo.setTop(new PlayedTile(leftTile.getOpenSide(), true, false, true));
							tableInfo.setBottom(new PlayedTile(leftTile.getOpenSide(), true, false, true));
							tableInfo.setWithCenter(true);
						}
					}
					tableInfo.setLeft(new PlayedTile(tableInfo.getLeft().getOpenSide() == left ? right : left, left == right, true, false));
					break;
			}
		}
	}

	public static int countScore(Round round) {
		int count = 0;
		TableInfo tableInfo = round.getTableInfo();
		if (tableInfo.getLeft().getOpenSide() == tableInfo.getRight().getOpenSide() && tableInfo.getLeft().isTwin() && tableInfo.getRight().isTwin()) {
			count = tableInfo.getLeft().getOpenSide() * 2;  // First move, 5X5 case
		} else if (round.getMyTiles().size() + tableInfo.getOpponentTilesCount() == 13 && tableInfo.getBazaarTilesCount() == 14) {
			return 0;   // First move, not 5X5
		} else {
			count += tableInfo.getLeft().isTwin() ? (tableInfo.getLeft().getOpenSide() * 2) : tableInfo.getLeft().getOpenSide();
			count += tableInfo.getRight().isTwin() ? (tableInfo.getRight().getOpenSide() * 2) : tableInfo.getRight().getOpenSide();
			if (tableInfo.getTop() != null && tableInfo.getTop().isConsiderInSum()) {
				count += tableInfo.getTop().isTwin() ? (tableInfo.getTop().getOpenSide() * 2) : tableInfo.getTop().getOpenSide();
			}
			if (tableInfo.getBottom() != null && tableInfo.getBottom().isConsiderInSum()) {
				count += tableInfo.getBottom().isTwin() ? (tableInfo.getBottom().getOpenSide() * 2) : tableInfo.getBottom().getOpenSide();
			}
		}
		if (count > 0 && count % 5 == 0) {
			return count;
		} else {
			return 0;
		}
	}

	public static Set<Integer> getPossiblePlayNumbers(TableInfo tableInfo) {
		Set<Integer> possiblePlayNumbers = new HashSet<>();
		if (tableInfo.getTop() != null) {
			possiblePlayNumbers.add(tableInfo.getTop().getOpenSide());
		}
		if (tableInfo.getRight() != null) {
			possiblePlayNumbers.add(tableInfo.getRight().getOpenSide());
		}
		if (tableInfo.getBottom() != null) {
			possiblePlayNumbers.add(tableInfo.getBottom().getOpenSide());
		}
		if (tableInfo.getLeft() != null) {
			possiblePlayNumbers.add(tableInfo.getLeft().getOpenSide());
		}
		return possiblePlayNumbers;
	}

	public static CachedPrediction fillCachedPrediction(Round round, CachedPrediction cachedPrediction) {
		List<Move> moves = getPossibleMoves(round, true);
		for (Move move : moves) {
			if (!cachedPrediction.getChildren().containsKey(move)) {
				for (CachedPrediction child : cachedPrediction.getChildren().values()) {
					if (child.getMove() != null && TileAndMoveHelper.equalWithHash(child.getMove(), move, round.getTableInfo())) {
						CachedPrediction newCachedPrediction = new CachedPrediction();
						newCachedPrediction.setMove(move);
						newCachedPrediction.setHeuristicValue(child.getHeuristicValue());
						cachedPrediction.getChildren().put(move, newCachedPrediction);
						break;
					}
				}
			}
		}
		return cachedPrediction;
	}

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

	private static int normalizeLeftTilesCount(double count) {
		for (int i = 5; ; i += 5) {
			if (i >= count) {
				return i;
			}
		}
	}
}
