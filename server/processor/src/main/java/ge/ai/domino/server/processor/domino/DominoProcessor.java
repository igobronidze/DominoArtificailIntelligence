package ge.ai.domino.server.processor.domino;

import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.processor.sysparam.SystemParameterProcessor;
import ge.ai.domino.server.processor.util.CloneUtil;
import ge.ai.domino.server.processor.util.InitialUtil;
import ge.ai.domino.util.tile.TileUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DominoProcessor {

    private Logger logger = Logger.getLogger(DominoProcessor.class);

    private static final SystemParameterProcessor systemParameterProcessor = new SystemParameterProcessor();

    private static final SysParam logTilesAfterMethod = new SysParam("logTilesAfterMethod", "false");

    private static final String HIM = "him";

    private static final String ME = "me";

    private static final String BAZAAR = "bazaar";

    private static Map<Integer, Integer> tilesFromBazaar = new HashMap<>();

    private static Map<Integer, String> omittedGames = new HashMap<>();

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    public Hand startGame(GameProperties gameProperties) {
        logger.info("Started prepare new game");
        Game game = InitialUtil.getInitialGame(gameProperties);
        cachedGames.put(game.getId(), game);
        logger.info("Started new game");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(game.getCurrHand());
        }
        return game.getCurrHand();
    }

    public Hand addTileForMe(Hand hand, int x, int y) {
        logger.info("Start add tile for me method for tile [" + x + "-" + y + "]");
        if (hand.getTableInfo().getBazaarTilesCount() == 2) {
            if (HIM.equals(omittedGames.get(hand.getGameInfo().getGameId()))) {
                return finishedLastAndGetNewHand(hand, true);
            } else {
                omittedGames.put(hand.getGameInfo().getGameId(), ME);
            }
            return hand;
        }
        Map<String, Tile> tiles = hand.getTiles();
        Tile tile = tiles.get(TileUtil.getTileUID(x, y));
        double him = tile.getHim();
        double bazaar = tile.getBazaar();
        makeTileForMe(tile);
        addProbabilitiesProportional(tiles, getNotPlayedMineOrBazaarTiles(tiles), him, HIM);
        addProbabilitiesProportional(tiles, getNotPlayedMineOrBazaarTiles(tiles), bazaar - 1, BAZAAR);
        updateTileCountBeforeAddMe(hand);
        logger.info("Added tile for me");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(hand);
        }
        return hand;
    }

    public Hand addTileForHim(Hand hand) {
        logger.info("Start add tile for him method");
        int gameId = hand.getGameInfo().getGameId();
        if (tilesFromBazaar.get(gameId) == null || tilesFromBazaar.get(gameId) == 0) {
            makeTilesAsInBazaarAndUpdateProbabilitiesForOther(hand);
            updateTileCountBeforeAddHim(hand);
        }
        if (hand.getTableInfo().getBazaarTilesCount() == 2) {
            if (hand.getTableInfo().getBazaarTilesCount() == 2) {
                if (ME.equals(omittedGames.get(hand.getGameInfo().getGameId()))) {
                    return finishedLastAndGetNewHand(hand, false);
                } else {
                    omittedGames.put(hand.getGameInfo().getGameId(), HIM);
                }
                return hand;
            }
            if (tilesFromBazaar.get(gameId) != null && tilesFromBazaar.get(gameId) > 0) {
                updateProbabilitiesForLastPickedTiles(hand, gameId);
            }
            return hand;
        }
        tilesFromBazaar.put(gameId, tilesFromBazaar.get(gameId) == null ? 1 : tilesFromBazaar.get(gameId) + 1);
        updateTileCountBeforeAddHim(hand);
        logger.info("Added tile for him");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(hand);
        }
        return hand;
    }

    public Hand playForMe(Hand hand, int x, int y, PlayDirection direction) {
        logger.info("Start play for me method for tile [" + x + "-" + y + "] direction [" + direction.name() + "]");
        makeTileAsPlayed(hand.getTiles().get(TileUtil.getTileUID(x, y)));
        playTile(hand.getTableInfo(), x, y, direction);
        updateTileCountBeforePlayMe(hand);
        hand.getGameInfo().setMyPoints(hand.getGameInfo().getMyPoints() + countScore(hand));
        hand.getTableInfo().setMyTurn(false);
        if (hand.getTableInfo().getMyTilesCount() == 0) {
            return finishedLastAndGetNewHand(hand, true);
        }
        logger.info("Play tile for me");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(hand);
        }
        return hand;
    }

    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction) {
        logger.info("Start play for him method for tile [" + x + "-" + y + "] direction [" + direction.name() + "]");
        int gameId = hand.getGameInfo().getGameId();
        if (tilesFromBazaar.get(gameId) != null && tilesFromBazaar.get(gameId) > 0) {
            updateProbabilitiesForLastPickedTiles(hand, gameId);
        } else {
            Map<String, Tile> tiles = hand.getTiles();
            Tile playedTile = tiles.get(TileUtil.getTileUID(x, y));
            double him = playedTile.getHim();
            double bazaar = playedTile.getBazaar();
            makeTileAsPlayed(playedTile);
            addProbabilitiesProportional(tiles, getNotPlayedMineOrBazaarTiles(tiles), him - 1, HIM);
            addProbabilitiesProportional(tiles, getNotPlayedMineOrBazaarTiles(tiles), bazaar, BAZAAR);
        }
        playTile(hand.getTableInfo(), x, y, direction);
        updateTileCountBeforePlayHim(hand);
        hand.getGameInfo().setHimPoints(hand.getGameInfo().getHimPoints() + countScore(hand));
        hand.getTableInfo().setMyTurn(true);
        if (hand.getTableInfo().getHimTilesCount() == 0) {
            return finishedLastAndGetNewHand(hand, false);
        }
        AIPrediction aiPrediction = MinMax.minMax(CloneUtil.getClone(hand));
        hand.setAiPrediction(aiPrediction);
        logger.info("Play tile for him");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(hand);
        }
        return hand;
    }

    private void makeTilesAsInBazaarAndUpdateProbabilitiesForOther(Hand hand) {
        Set<Integer> notUsedNumbers = getNotUsedNumbers(hand);
        double himSum = 0.0;
        double bazaarSum = 0.0;
        Set<String> mayHaveTiles = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (!tile.isPlayed() && tile.getMe() != 1.0) {
                if (notUsedNumbers.contains(tile.getX()) || notUsedNumbers.contains(tile.getY())) {
                    himSum += tile.getHim();
                    bazaarSum += (1.0 - tile.getBazaar());
                    tile.setHim(0);
                    tile.setMe(0);
                    tile.setBazaar(1.0);
                } else {
                    mayHaveTiles.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
                }
            }
        }
        addProbabilitiesProportional(hand.getTiles(), mayHaveTiles, himSum, HIM);
        addProbabilitiesProportional(hand.getTiles(), mayHaveTiles, -1 * bazaarSum, BAZAAR);
    }

    private void updateProbabilitiesForLastPickedTiles(Hand hand, int gameId) {
        Set<Integer> notUsedNumbers = getNotUsedNumbers(hand);
        Set<String> usefulUIDs = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (!notUsedNumbers.contains(tile.getX()) && !notUsedNumbers.contains(tile.getY()) && !tile.isPlayed() && tile.getMe() != 1.0) {
                usefulUIDs.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
            }
        }
        double bazaarTilesCount = tilesFromBazaar.get(gameId);
        addProbabilitiesProportional(hand.getTiles(), usefulUIDs, bazaarTilesCount, HIM);
        addProbabilitiesProportional(hand.getTiles(), usefulUIDs, -1 * bazaarTilesCount, BAZAAR);
        tilesFromBazaar.put(gameId, 0);
    }

    private Set<Integer> getNotUsedNumbers(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        Set<Integer> notUsedNumbers = new HashSet<>();
        if (tableInfo.getTop() != null) {
            notUsedNumbers.add(tableInfo.getTop().getOpenSide());
        }
        if (tableInfo.getRight() != null) {
            notUsedNumbers.add(tableInfo.getRight().getOpenSide());
        }
        if (tableInfo.getBottom() != null) {
            notUsedNumbers.add(tableInfo.getBottom().getOpenSide());
        }
        if (tableInfo.getLeft() != null) {
            notUsedNumbers.add(tableInfo.getLeft().getOpenSide());
        }
        return notUsedNumbers;
    }

    private void makeTileForMe(Tile tile) {
        tile.setMe(1.0);
        tile.setHim(0.0);
        tile.setBazaar(0.0);
    }

    private void addProbabilitiesProportional(Map<String, Tile> tiles, Set<String> possibleTiles, double probability, String type) {
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

    private Set<String> getNotPlayedMineOrBazaarTiles(Map<String, Tile> tiles) {
        Set<String> keys = new HashSet<>();
        for (String key : tiles.keySet()) {
            Tile tile = tiles.get(key);
            if (!tile.isPlayed() && tile.getMe() != 1.0 && tile.getBazaar() != 1.0) {
                keys.add(key);
            }
        }
        return keys;
    }

    private double getProbability(Tile tile, String type) {
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

    private void addProbability(Tile tile, double add, String type) {
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

    private void updateTileCountBeforeAddMe(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }

    private void updateTileCountBeforeAddHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }

    private Hand finishedLastAndGetNewHand(Hand hand, boolean me) {
        omittedGames = new HashMap<>();
        tilesFromBazaar = new HashMap<>();
        GameInfo gameInfo = hand.getGameInfo();
        if (me) {
            gameInfo.setMyPoints(gameInfo.getMyPoints() + countLeftTiles(hand.getTiles(), true));
        } else {
            gameInfo.setHimPoints(gameInfo.getHimPoints() + countLeftTiles(hand.getTiles(), false));
        }
        int scoreForWin = cachedGames.get(hand.getGameInfo().getGameId()).getGameProperties().getPointsForWin();
        if (gameInfo.getMyPoints() >= scoreForWin) {
            logger.info("I win the game");
            return null;
        } else if (gameInfo.getHimPoints() >= scoreForWin) {
            logger.info("He win the game");
            return null;
        } else {
            Game game = cachedGames.get(hand.getGameInfo().getGameId());
            game.getHistory().add(hand);
            game.setCurrHand(InitialUtil.getInitialHand(false));
            game.getCurrHand().setGameInfo(hand.getGameInfo());
            return game.getCurrHand();
        }
    }

    void updateTileCountBeforePlayMe(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() - 1);
    }

    void updateTileCountBeforePlayHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() - 1);
    }

    void makeTileAsPlayed(Tile tile) {
        tile.setMe(0);
        tile.setHim(0);
        tile.setBazaar(0);
        tile.setPlayed(true);
    }

    void playTile(TableInfo tableInfo, int x, int y, PlayDirection direction) {
        if (!tableInfo.isWithCenter() && x == y) {
            tableInfo.setTop(new PlayedTile(x, true, false, true));
            tableInfo.setBottom(new PlayedTile(x, true, false, true));
            if (tableInfo.getLeft() == null) {
                tableInfo.setLeft(new PlayedTile(x, true, true, true));
                tableInfo.setRight(new PlayedTile(x, true, true, true));
            } else {
                if (direction == PlayDirection.LEFT) {
                    tableInfo.setLeft(new PlayedTile(x, true, true, true));
                } else if (direction == PlayDirection.RIGHT) {
                    tableInfo.setRight(new PlayedTile(x, true, true, true));
                }
            }
            tableInfo.setWithCenter(true);
        } else {
            switch (direction) {
                case TOP:
                    tableInfo.setTop(new PlayedTile(tableInfo.getTop().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case RIGHT:
                    tableInfo.setRight(new PlayedTile(tableInfo.getRight().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case BOTTOM:
                    tableInfo.setBottom(new PlayedTile(tableInfo.getBottom().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
                case LEFT:
                    tableInfo.setLeft(new PlayedTile(tableInfo.getLeft().getOpenSide() == x ? y : x, x == y, true, false));
                    break;
            }
        }
        tableInfo.setLastPlayedUID(TileUtil.getTileUID(x, y));
    }

    int countLeftTiles(Map<String, Tile> tiles, boolean me) {
        double cont = 0;
        for (Tile tile : tiles.values()) {
            if (me) {
                cont += tile.getMe() * (tile.getX() + tile.getY());
            } else {
                cont += tile.getHim() * (tile.getX() + tile.getY());
            }
        }
        for (int i = 5; ; i+=5) {
            if (i >= cont) {
                return i;
            }
        }
    }

    int countScore(Hand hand) {
        int count = 0;
        TableInfo tableInfo = hand.getTableInfo();
        if (tableInfo.getLeft().getOpenSide() == tableInfo.getRight().getOpenSide() && tableInfo.getLeft().isDouble() && tableInfo.getRight().isDouble()) {
            count = tableInfo.getLeft().getOpenSide() * 2;
        } else {
            count += tableInfo.getLeft().isDouble() ? (tableInfo.getLeft().getOpenSide() * 2) : tableInfo.getLeft().getOpenSide();
            count += tableInfo.getRight().isDouble() ? (tableInfo.getRight().getOpenSide() * 2) : tableInfo.getRight().getOpenSide();
            if (tableInfo.getTop().isCountInSum()) {
                count += tableInfo.getTop().isDouble() ? (tableInfo.getTop().getOpenSide() * 2) : tableInfo.getTop().getOpenSide();
            }
            if (tableInfo.getBottom().isCountInSum()) {
                count += tableInfo.getBottom().isDouble() ? (tableInfo.getBottom().getOpenSide() * 2) : tableInfo.getBottom().getOpenSide();
            }
        }
        if (count > 0 && count % 5 == 0) {
            return count;
        } else {
            return 0;
        }
    }
}
