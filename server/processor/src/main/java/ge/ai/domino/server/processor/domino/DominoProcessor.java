package ge.ai.domino.server.processor.domino;

import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Game;
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

    private static final Map<Integer, Integer> tilesFromBazaar = new HashMap<>();

    public Game startGame(GameProperties gameProperties) {
        logger.info("Started prepare new game");
        Game game = InitialUtil.getInitialGame(gameProperties);
        logger.info("Started new game");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(game.getCurrHand());
        }
        return game;
    }

    public Hand addTileForMe(Hand hand, int x, int y) {
        logger.info("Start add tile for me method for tile [" + x + "-" + y + "]");
        if (hand.getTableInfo().getBazaarTilesCount() == 2) {
            return hand;
        }
        addTileForMe(hand.getTiles(), x, y);
        updateTileCountBeforeAddMe(hand);
        logger.info("Added tile for me");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(hand);
        }
        return hand;
    }

    public Hand addTileForHim(Hand hand, int gameId) {
        logger.info("Start add tile for him method");
        if (tilesFromBazaar.get(gameId) == null || tilesFromBazaar.get(gameId) == 0) {
            makeTilesAsInBazaarAndUpdateProbabilitiesForOther(hand);
        }
        if (hand.getTableInfo().getBazaarTilesCount() == 2) {
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
        logger.info("Play tile for me");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(hand);
        }
        return hand;
    }

    public Hand playForHim(Hand hand, int x, int y, PlayDirection direction, int gameId) {
        logger.info("Start play for him method for tile [" + x + "-" + y + "] direction [" + direction.name() + "]");
        if (tilesFromBazaar.get(gameId) != null && tilesFromBazaar.get(gameId) > 0) {
            updateProbabilitiesForLastPickedTiles(hand, gameId);
        }
        makeTileAsPlayed(hand.getTiles().get(TileUtil.getTileUID(x, y)));
        playTile(hand.getTableInfo(), x, y, direction);
        updateTileCountBeforePlayHim(hand);
        AIPrediction aiPrediction = MinMax.minMax(CloneUtil.getClone(hand));
        hand.setAiPrediction(aiPrediction);
        logger.info("Play tile for him");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(hand);
        }
        if (aiPrediction == null) {
            logger.info("No AIPrediction");
        } else {
            logger.info("AIPrediction is [" + aiPrediction.getX() + "-" + aiPrediction.getY() + " " + aiPrediction.getDirection().name() + "]");
        }
        return hand;
    }


    /**
     * თუ პირველად წავიდა ბაზარში, ვიღებთ ყველა შესაძლო სვლას და ვაცხადებთ, რომ მას ეს ქვები არ ყავს
     * შესაბამის ალბათობებს ვუნაწილებთ სხვას
     *
     * @param hand
     */
    private void makeTilesAsInBazaarAndUpdateProbabilitiesForOther(Hand hand) {
        Set<Integer> notUsedNumbers = getNotUsedNumbers(hand);
        double himSum = 0.0;
        double bazaarSum = 0.0;
        Set<String> mayHaveTiles = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (notUsedNumbers.contains(tile.getX()) || notUsedNumbers.contains(tile.getY())) {
                himSum += tile.getHim();
                bazaarSum += (1.0 - tile.getBazaar());
                tile.setHim(0);
                tile.setMe(0);
                tile.setBazaar(1.0);
            } else if (!tile.isPlayed() && tile.getMe() != 1.0) {
                mayHaveTiles.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
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

    public void playTile(TableInfo tableInfo, int x, int y, PlayDirection direction) {
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

    private void addTileForMe(Map<String, Tile> tiles, int x, int y) {
        Tile tile = tiles.get(TileUtil.getTileUID(x, y));
        double him = tile.getHim();
        double bazaar = tile.getBazaar();
        makeTileForMe(tile);
        addProbabilitiesProportional(tiles, getNotPlayedAndNotMineTiles(tiles), him, HIM);
        addProbabilitiesProportional(tiles, getNotPlayedAndNotMineTiles(tiles), bazaar - 1, BAZAAR);
    }

    private void makeTileForMe(Tile tile) {
        tile.setMe(1.0);
        tile.setHim(0.0);
        tile.setBazaar(0.0);
    }

    public void makeTileAsPlayed(Tile tile) {
        tile.setMe(0);
        tile.setHim(0);
        tile.setBazaar(0);
        tile.setPlayed(true);
    }

    /**
     * probability ნაწილდება possibleTiles ქვებზე
     *
     * @param tiles არსებული ყველა ქვა
     * @param possibleTiles ის ქვები, რომელსაც უნდა შეეხოს ალბათობის მომატება
     * @param probability მთლიანი ალბათობა რომელიც უნდა გადანაწილდეს ქვებზე
     * @param type ტიპი (HIM, ME, BAZAAR)
     */
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

    private Set<String> getNotPlayedAndNotMineTiles(Map<String, Tile> tiles) {
        Set<String> keys = new HashSet<>();
        for (String key : tiles.keySet()) {
            Tile tile = tiles.get(key);
            if (!tile.isPlayed() && tile.getMe() != 1.0) {
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

    public void updateTileCountBeforePlayMe(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() - 1);
    }

    public void updateTileCountBeforePlayHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() - 1);
    }
}
