package ge.ai.domino.server.manager.game.helper;

import ge.ai.domino.domain.game.*;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameOperations {

	public static int countLeftTiles(Round round, boolean countMine, boolean virtual) {
		float count = 0;
		if (countMine) {
			for (Tile tile : round.getMyTiles()) {
				count += tile.getLeft() + tile.getRight();
			}
		} else {
			for (Map.Entry<Tile, Float> entry : round.getOpponentTiles().entrySet()) {
				count += entry.getValue() * (entry.getKey().getLeft() + entry.getKey().getRight());
			}
		}
		int gameId = round.getGameInfo().getGameId();
		if (count == 0) {
			GameLoggingProcessor.logInfoAboutMove("Left tiles count is " + 10 + "(0,0), gameId[" + gameId + "]", virtual);
			return 10;
		}
		GameLoggingProcessor.logInfoAboutMove("Left tiles count is " + count + ", gameId[" + gameId + "]", virtual);
		return (int)count;
	}

	public static void addLeftTiles(GameInfo gameInfo, int count, boolean forMe, int gameId, boolean virtual) {
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
			CachedGames.addLeftTilesCountFromLastRound(gameId, 0);
		}
	}

	public static Round finishedLastAndGetNewRound(Round round, boolean finishedMe, boolean countLeft, boolean virtual) {
		GameInfo gameInfo = round.getGameInfo();
		int gameId = gameInfo.getGameId();
		round.getTableInfo().setTilesFromBazaar(0);
		if (countLeft) {
			if (finishedMe) {
				addLeftTiles(gameInfo, countLeftTiles(round, false, virtual), true, gameId, virtual);
			} else {
				addLeftTiles(gameInfo, countLeftTiles(round, true, virtual), false, gameId, virtual);
			}
		}
		int scoreForWin = CachedGames.getGameProperties(gameId).getPointsForWin();
		if (!round.getTableInfo().isOmittedOpponent() || !round.getTableInfo().isOmittedMe()) {
			if (gameInfo.getMyPoint() >= scoreForWin && gameInfo.getMyPoint() >= gameInfo.getOpponentPoint()) {
				round.getGameInfo().setFinished(true);
				GameLoggingProcessor.logInfoAboutMove("I win the game", virtual);
				return round;
			} else if (gameInfo.getOpponentPoint() >= scoreForWin) {
				round.getGameInfo().setFinished(true);
				GameLoggingProcessor.logInfoAboutMove("He win the game", virtual);
				return round;
			}
		}
		CachedGames.addRound(gameId, round);
		CachedGames.addMove(gameId, MoveHelper.getStartNewRoundMove());
		Round newRound = InitialUtil.getInitialRound(0);
		newRound.getTableInfo().setLastPlayedProb(round.getTableInfo().getLastPlayedProb());   // For MinMax
		newRound.getTableInfo().setMyMove(true);
		newRound.getTableInfo().setFirstRound(false);
		newRound.setGameInfo(round.getGameInfo());
		if (!finishedMe && !virtual) {
			CachedGames.makeOpponentNextRoundBeginner(gameId);
		}

		GameLoggingProcessor.logInfoAboutMove("Finished round and start new one, gameId[" + gameId + "]", virtual);
		return newRound;
	}

	public static void distributeProbabilitiesOpponentProportional(Map<Tile, Float> tiles, float probability) {
		int count = 0;
		float sum = 0.0F;
		for (float prob : tiles.values()) {
			if (filterTile(prob, false, true, false, true)) {
				sum += prob;
				count++;
			}
		}

		if (ComparisonHelper.equal(sum + probability, count)) {
			for (Map.Entry<Tile, Float> entry : tiles.entrySet()) {
				if (filterTile(entry.getValue(), false, true, false, true)) {
					entry.setValue(1.0F);
				}
			}
		} else {
			for (Map.Entry<Tile, Float> entry : tiles.entrySet()) {
				if (filterTile(entry.getValue(), false, true, false, true)) {
					float add = probability * entry.getValue() / sum;
					entry.setValue(entry.getValue() + add);
				}
			}
		}
	}

	// TODO
	public static void updateProbabilitiesForLastPickedTiles(Round round, boolean played) {
		float bazaarTilesCount = round.getTableInfo().getTilesFromBazaar();
		float probability = played ? bazaarTilesCount - 1 : bazaarTilesCount;

		Map<Tile, Float> tiles = round.getOpponentTiles();
		Set<Integer> notUsedNumbers = getPossiblePlayNumbers(round.getTableInfo());

		int count = 0;
		float sum = 0.0F;
		for (Map.Entry<Tile, Float> entry : tiles.entrySet()) {
			if (filterTile(entry.getValue(), false, true, false, true)
					&& !notUsedNumbers.contains(entry.getKey().getLeft()) && !notUsedNumbers.contains(entry.getKey().getRight())) {
				sum += (1 - entry.getValue());
				count++;
			}
		}

		if (ComparisonHelper.equal(sum + probability, count)) {
			for (Map.Entry<Tile, Float> entry : tiles.entrySet()) {
				if (filterTile(entry.getValue(), false, true, false, true)
						&& !notUsedNumbers.contains(entry.getKey().getLeft()) && !notUsedNumbers.contains(entry.getKey().getRight())) {
					entry.setValue(1.0F);
				}
			}
		} else {
			for (Map.Entry<Tile, Float> entry : tiles.entrySet()) {
				if (filterTile(entry.getValue(), false, true, false, true)
						&& !notUsedNumbers.contains(entry.getKey().getLeft()) && !notUsedNumbers.contains(entry.getKey().getRight())) {
					float add = probability * (1 - entry.getValue()) / sum;
					entry.setValue(entry.getValue() + add);
				}
			}
		}


		round.getTableInfo().setTilesFromBazaar(0);
	}

	public static float makeTilesAsBazaarAndReturnProbabilitiesSum(Round round) {
		Set<Integer> possiblePlayNumbers = getPossiblePlayNumbers(round.getTableInfo());
		float sum = 0.0F;
		for (Map.Entry<Tile, Float> entry : round.getOpponentTiles().entrySet()) {
			if (filterTile(entry.getValue(), false, false, false, true)
					&& (possiblePlayNumbers.contains(entry.getKey().getLeft()) || possiblePlayNumbers.contains(entry.getKey().getRight()))) {
				sum += entry.getValue();
				entry.setValue(0.0F);
			}
		}
		return sum;
	}

	public static float makeTwinTilesAsBazaarAndReturnProbabilitiesSum(Map<Tile, Float> opponentTiles, int twinNumber) {
		float sum = 0.0F;
		for (Map.Entry<Tile, Float> entry : opponentTiles.entrySet()) {
			Tile tile = entry.getKey();
			if (filterTile(entry.getValue(), false, false, false, true)
					&& (tile.getLeft() == tile.getRight() && tile.getLeft() > twinNumber)) {
				sum += entry.getValue();
				entry.setValue(0.0F);
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
				tableInfo.setLeft(new PlayedTile(left, false, true, false));
				tableInfo.setRight(new PlayedTile(right, false, true, false));
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

	private static Set<Integer> getPossiblePlayNumbers(TableInfo tableInfo) {
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

	private static boolean filterTile(float prob, boolean opponent, boolean notOpponent, boolean bazaar, boolean notBazaar) {
		return !(opponent && prob != 1.0) &&
				!(notOpponent && prob == 1.0) &&
				!(bazaar && prob != 0.0) &&
				!(notBazaar && prob == 0.0);
	}

	private static int normalizeLeftTilesCount(float count) {
		for (int i = 5; ; i += 5) {
			if (i >= count) {
				return i;
			}
		}
	}
}
