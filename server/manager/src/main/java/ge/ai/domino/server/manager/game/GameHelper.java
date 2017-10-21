package ge.ai.domino.server.manager.game;

import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.Tile;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.util.InitialUtil;

public class GameHelper {

    public static Round finishedLastAndGetNewRound(Round round, boolean finishedMe, boolean countLeft, boolean virtual) {
        GameInfo gameInfo = round.getGameInfo();
        int gameId = gameInfo.getGameId();
        Game game = CachedGames.getGame(gameId);
        round.getTableInfo().setTilesFromBazaar(0);
        if (countLeft) {
            if (finishedMe) {
                addLeftTiles(gameInfo, countLeftTiles(round, false, virtual), true, gameId, virtual);
            } else {
                addLeftTiles(gameInfo, countLeftTiles(round, true, virtual), false, gameId, virtual);
            }
        }
        int scoreForWin = game.getProperties().getPointsForWin();
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
        game.getHistory().add(round);
        game.setCurrRound(InitialUtil.getInitialRound());
        game.getCurrRound().getTableInfo().setLastPlayedProb(round.getTableInfo().getLastPlayedProb());   // MinMax იყენებს
        game.getCurrRound().getTableInfo().setMyMove(true);
        if (!finishedMe && !virtual) {
            CachedGames.addOpponentStart(gameId);
        }
        game.getCurrRound().getTableInfo().setFirstRound(false);
        game.getCurrRound().setGameInfo(round.getGameInfo());
        GameLoggingProcessor.logInfoAboutMove("Finished round and start new one, gameId[" + gameId + "]", virtual);
        return game.getCurrRound();
    }

    public static int countLeftTiles(Round round, boolean countMine, boolean virtual) {
        double count = 0;
        if (countMine) {
            for (Tile tile : round.getMyTiles()) {
                count += tile.getLeft() + tile.getRight();
            }
        } else {
            for (OpponentTile tile : round.getOpponentTiles().values()) {
                count += tile.getProb() * (tile.getLeft() + tile.getRight());
            }
        }
        int gameId = round.getGameInfo().getGameId();
        if (count == 0) {
            GameLoggingProcessor.logInfoAboutMove("Left tiles count is " + 10 + ", gameId[" + gameId + "]", virtual);
            return 10;
        }
        GameLoggingProcessor.logInfoAboutMove("Left tiles count is " + count + ", gameId[" + gameId + "]", virtual);
        return (int)count;
    }

    public static boolean isNewRound(TableInfo tableInfo) {
        return tableInfo.getMyTilesCount() == 0 && tableInfo.getOpponentTilesCount() == 7 && tableInfo.getBazaarTilesCount() == 21;
    }

    public static void addLeftTiles(GameInfo gameInfo, int count, boolean forMe, int gameId, boolean virtual) {
        int countFromLastRound = CachedGames.getPointFromLastRound(gameId);
        if (count % 5 != 0) {
            count = normalizeLeftTilesCount(count);
        }
        if (forMe) {
            gameInfo.setMyPoint(gameInfo.getMyPoint() + count + countFromLastRound);
        } else {
            gameInfo.setOpponentPoint(gameInfo.getOpponentPoint() + count + countFromLastRound);
        }
        if (!virtual) {
            CachedGames.addPointFromLastRound(gameId, 0);
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
