package ge.ai.domino.manager.game.helper.play;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.helper.filter.OpponentTilesFilter;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameOperations {

	protected static final Logger logger = Logger.getLogger(GameOperations.class);

	private static final String FIRST_NOT_TWIN_TILE_DIRECTION_KEY = "firstNotTwinTileDirection";

	private static final String FIRST_NOT_TWIN_TILE_DIRECTION_DEFAULT_VALUE = "left";

	private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private static final SysParam analyzeFirstTwinTileRate = new SysParam("analyzeFirstTwinTileRate", "0.10");

	private static final SysParam analyzeFirstNotTwinTileRate = new SysParam("analyzeFirstNotTwinTileRate", "0.10");

	private static final SysParam analyzeFirstNotTwinTileTwinsSubtractionRate = new SysParam("analyzeFirstNotTwinTileTwinsSubtractionRate", "0.07");

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

	public static double analyzeFirstOpponentTileAndReturnProbabilitiesSum(Map<Tile, Double> opponentTiles, Tile playedTile) {
		double sum = 0.0;
		Set<Integer> usedNumbers = new HashSet<>();

		if (playedTile.getLeft() == playedTile.getRight()) {
			usedNumbers.add(playedTile.getLeft());
			OpponentTilesFilter opponentTilesFilter = new OpponentTilesFilter()
					.mustUsedNumbers(usedNumbers);
			for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
				if (opponentTilesFilter.filter(entry)) {
					double addition = entry.getValue() * systemParameterManager.getDoubleParameterValue(analyzeFirstTwinTileRate);
					entry.setValue(entry.getValue() + addition);
					sum -= addition;
				}
			}
		} else {
			usedNumbers.add(playedTile.getLeft());
			usedNumbers.add(playedTile.getRight());
			OpponentTilesFilter opponentTilesFilter = new OpponentTilesFilter()
					.mustUsedNumbers(usedNumbers);
			for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
				if (opponentTilesFilter.filter(entry)) {
					double addition = entry.getValue() * systemParameterManager.getDoubleParameterValue(analyzeFirstNotTwinTileRate);
					entry.setValue(entry.getValue() + addition);
					sum -= addition;
				}
			}

			opponentTilesFilter = new OpponentTilesFilter()
					.twin(true);
			for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
				if (opponentTilesFilter.filter(entry)) {
					double subtraction = entry.getValue() * systemParameterManager.getDoubleParameterValue(analyzeFirstNotTwinTileTwinsSubtractionRate);
					entry.setValue(entry.getValue() - subtraction);
					sum += subtraction;
				}
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
		List<Move> moves = PossibleMovesManager.getPossibleMoves(round, true);
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

	private static int normalizeLeftTilesCount(double count) {
		for (int i = 5; ; i += 5) {
			if (i >= count) {
				return i;
			}
		}
	}
}
