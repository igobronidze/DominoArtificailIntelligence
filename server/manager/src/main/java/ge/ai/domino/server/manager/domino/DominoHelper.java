package ge.ai.domino.server.manager.domino;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.domino.TileOwner;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.util.InitialUtil;
import ge.ai.domino.util.tile.TileUtil;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DominoHelper {

    private static final Logger logger = Logger.getLogger(DominoHelper.class);

    public static Hand finishedLastAndGetNewHand(Hand hand, boolean me, boolean countLeft) {
        GameInfo gameInfo = hand.getGameInfo();
        int gameId = gameInfo.getGameId();
        Game game = CachedDominoGames.getGame(gameId);
        CachedDominoGames.clearOmittedAndTiles(gameId);
        if (countLeft) {
            if (me) {
                gameInfo.setMyPoints(gameInfo.getMyPoints() + countLeftTiles(hand, true));
            } else {
                gameInfo.setHimPoints(gameInfo.getHimPoints() + countLeftTiles(hand, false));
            }
        }
        int scoreForWin = game.getGameProperties().getPointsForWin();
        if (gameInfo.getMyPoints() >= scoreForWin && gameInfo.getMyPoints() >= gameInfo.getHimPoints()) {
            logger.info("I win the game");
            return hand;
        } else if (gameInfo.getHimPoints() >= scoreForWin) {
            logger.info("He win the game");
            return hand;
        } else {
            game.getHistory().add(hand);
            game.setCurrHand(InitialUtil.getInitialHand(true));
            game.getGameProperties().setStart(me);
            game.getGameProperties().setFirstHand(false);
            game.getCurrHand().setGameInfo(hand.getGameInfo());
            logger.info("Finished hand and start new one, gameId[" + gameId + "]");
            return game.getCurrHand();
        }
    }

    public static void addProbabilitiesProportional(Map<String, Tile> tiles, Set<String> possibleTiles, double probability, TileOwner type) {
        double sum = 0.0;
        for (String key : possibleTiles) {
            sum += getProbability(tiles.get(key), type);
        }
        for (String key : possibleTiles) {
            Tile tile = tiles.get(key);
            double add = probability * getProbability(tile, type) / sum;
            addProbability(tile, add, type);
        }
    }

    public static Set<String> getNotPlayedMineOrBazaarTiles(Map<String, Tile> tiles) {
        Set<String> keys = new HashSet<>();
        for (String key : tiles.keySet()) {
            Tile tile = tiles.get(key);
            if (!tile.isPlayed() && tile.getMe() != 1.0 && tile.getBazaar() != 1.0) {
                keys.add(key);
            }
        }
        return keys;
    }

    public static void makeTileAsPlayed(Tile tile) {
        tile.setMe(0);
        tile.setHim(0);
        tile.setBazaar(0);
        tile.setPlayed(true);
    }

    public static void playTile(TableInfo tableInfo, int x, int y, PlayDirection direction) {
        // თუ პირველი სვლაა
        if (tableInfo.getLeft() == null) {
            if (x == y) {  // თუ წყვილია
                tableInfo.setTop(new PlayedTile(x, true, false, true));
                tableInfo.setBottom(new PlayedTile(x, true, false, true));
                tableInfo.setLeft(new PlayedTile(x, true, true, true));
                tableInfo.setRight(new PlayedTile(x, true, true, true));
            } else {
                tableInfo.setLeft(new PlayedTile(x, false, true, false));
                tableInfo.setRight(new PlayedTile(y, false, true, false));
            }
        } else {
            switch (direction) {
                case TOP:
                    tableInfo.setTop(new PlayedTile(tableInfo.getTop().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case RIGHT:
                    if (!tableInfo.isWithCenter()) {   // შემოწმება ხომ არ შეიქმნა ახალი ცენტრი
                        PlayedTile right = tableInfo.getRight();
                        if (right.isDouble()) {
                            tableInfo.setTop(new PlayedTile(right.getOpenSide(), true, false, true));
                            tableInfo.setBottom(new PlayedTile(right.getOpenSide(), true, false, true));
                            tableInfo.setWithCenter(true);
                        }
                    }
                    tableInfo.setRight(new PlayedTile(tableInfo.getRight().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case BOTTOM:
                    tableInfo.setBottom(new PlayedTile(tableInfo.getBottom().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case LEFT:
                    if (!tableInfo.isWithCenter()) {   // შემოწმება ხომ არ შეიქმნა ახალი ცენტრი
                        PlayedTile left = tableInfo.getLeft();
                        if (left.isDouble()) {
                            tableInfo.setTop(new PlayedTile(left.getOpenSide(), true, false, true));
                            tableInfo.setBottom(new PlayedTile(left.getOpenSide(), true, false, true));
                            tableInfo.setWithCenter(true);
                        }
                    }
                    tableInfo.setLeft(new PlayedTile(tableInfo.getLeft().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
            }
        }
        tableInfo.setLastPlayedUID(TileUtil.getTileUID(x, y));
    }

    public static int countScore(Hand hand) {
        int count = 0;
        TableInfo tableInfo = hand.getTableInfo();
        if (tableInfo.getLeft().getOpenSide() == tableInfo.getRight().getOpenSide() && tableInfo.getLeft().isDouble() && tableInfo.getRight().isDouble()) {
            count = tableInfo.getLeft().getOpenSide() * 2;  // პირველი ჩამოსვლა, 5X5 შემთხვევა
        } else if (tableInfo.getMyTilesCount() + tableInfo.getHimTilesCount() == 13 && tableInfo.getBazaarTilesCount() == 14) {
            return 0;   // პირველი ჩამოსვლა
        } else {
            count += tableInfo.getLeft().isDouble() ? (tableInfo.getLeft().getOpenSide() * 2) : tableInfo.getLeft().getOpenSide();
            count += tableInfo.getRight().isDouble() ? (tableInfo.getRight().getOpenSide() * 2) : tableInfo.getRight().getOpenSide();
            if (tableInfo.getTop() != null && tableInfo.getTop().isCountInSum()) {
                count += tableInfo.getTop().isDouble() ? (tableInfo.getTop().getOpenSide() * 2) : tableInfo.getTop().getOpenSide();
            }
            if (tableInfo.getBottom() != null && tableInfo.getBottom().isCountInSum()) {
                count += tableInfo.getBottom().isDouble() ? (tableInfo.getBottom().getOpenSide() * 2) : tableInfo.getBottom().getOpenSide();
            }
        }
        if (count > 0 && count % 5 == 0) {
            return count;
        } else {
            return 0;
        }
    }

    private static double getProbability(Tile tile, TileOwner type) {
        switch (type) {
            case HIM:
                return tile.getHim();
            case ME:
                return tile.getMe();
            case BAZAAR:
                return tile.getBazaar();
        }
        return 0;
    }

    private static void addProbability(Tile tile, double add, TileOwner type) {
        switch (type) {
            case HIM:
                tile.setHim(tile.getHim() + add);
                break;
            case ME:
                tile.setMe(tile.getMe() + add);
                break;
            case BAZAAR:
                tile.setBazaar(tile.getBazaar() + add);
                break;
        }
    }

    public static void updateTileCountBeforePlayMe(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() - 1);
    }

    public static void updateTileCountBeforePlayHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() - 1);
    }

    public static int countLeftTiles(Hand hand, boolean me) {
        double count = 0;
        for (Tile tile : hand.getTiles().values()) {
            if (me) {
                count += tile.getHim() * (tile.getX() + tile.getY());
            } else {
                count += tile.getMe() * (tile.getX() + tile.getY());
            }
        }
        int gameId = hand.getGameInfo().getGameId();
        if (count == 0) {
            logger.info("Left tiles count is " + 10 + ", gameId[" + gameId + "]");
            return 10;
        }
        for (int i = 5; ; i += 5) {
            if (i >= count) {
                logger.info("Left tiles count is " + i + ", gameId[" + gameId + "]");
                return i;
            }
        }
    }
}
