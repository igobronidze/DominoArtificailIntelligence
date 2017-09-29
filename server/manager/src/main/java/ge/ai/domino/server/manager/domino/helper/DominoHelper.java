package ge.ai.domino.server.manager.domino.helper;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.util.InitialUtil;

public class DominoHelper {

    public static Hand finishedLastAndGetNewHand(Hand hand, boolean finishedMe, boolean countLeft, boolean virtual) {
        GameInfo gameInfo = hand.getGameInfo();
        int gameId = gameInfo.getGameId();
        Game game = CachedDominoGames.getGame(gameId);
        hand.getTableInfo().setTileFromBazaar(0);
        if (countLeft) {
            if (finishedMe) {
                gameInfo.setMyPoints(gameInfo.getMyPoints() + countLeftTiles(hand, false, virtual));
            } else {
                gameInfo.setHimPoints(gameInfo.getHimPoints() + countLeftTiles(hand, true, virtual));
            }
        }
        int scoreForWin = game.getGameProperties().getPointsForWin();
        if (gameInfo.getMyPoints() >= scoreForWin && gameInfo.getMyPoints() >= gameInfo.getHimPoints()) {
            hand.getGameInfo().setFinished(true);
            DominoLoggingProcessor.logInfoOnTurn("I win the game", virtual);
            return hand;
        } else if (gameInfo.getHimPoints() >= scoreForWin) {
            hand.getGameInfo().setFinished(true);
            DominoLoggingProcessor.logInfoOnTurn("He win the game", virtual);
            return hand;
        }
        game.getHistory().add(hand);
        game.setCurrHand(InitialUtil.getInitialHand(true));
        game.getCurrHand().getTableInfo().setLastPlayedUID(hand.getTableInfo().getLastPlayedUID());   // MinMax იყენებს
        game.getGameProperties().setStart(finishedMe);
        game.getGameProperties().setFirstHand(false);
        game.getCurrHand().setGameInfo(hand.getGameInfo());
        DominoLoggingProcessor.logInfoOnTurn("Finished hand and start new one, gameId[" + gameId + "]", virtual);
        return game.getCurrHand();
    }

    public static int countLeftTiles(Hand hand, boolean countMine, boolean virtual) {
        double count = 0;
        for (Tile tile : hand.getTiles().values()) {
            if (countMine) {
                count += tile.isMine() ? (tile.getX() + tile.getY()) : 0;
            } else {
                count += tile.getHim() * (tile.getX() + tile.getY());
            }
        }
        int gameId = hand.getGameInfo().getGameId();
        if (count == 0) {
            DominoLoggingProcessor.logInfoOnTurn("Left tiles count is " + 10 + ", gameId[" + gameId + "]", virtual);
            return 10;
        }
        for (int i = 5; ; i += 5) {
            if (i >= count) {
                DominoLoggingProcessor.logInfoOnTurn("Left tiles count is " + i + ", gameId[" + gameId + "]", virtual);
                return i;
            }
        }
    }

    public static boolean isNewHand(TableInfo tableInfo) {
        return tableInfo.getMyTilesCount() == 0 && tableInfo.getHimTilesCount() == 7 && tableInfo.getBazaarTilesCount() == 21;
    }
}
