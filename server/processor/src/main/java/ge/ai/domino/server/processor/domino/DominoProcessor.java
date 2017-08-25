package ge.ai.domino.server.processor.domino;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.processor.sysparam.SystemParameterProcessor;
import ge.ai.domino.server.processor.util.InitialUtil;
import ge.ai.domino.util.tile.TileUtil;
import org.apache.log4j.Logger;

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

    public Game startGame(GameProperties gameProperties) {
        logger.info("Started prepare new game");
        Game game = InitialUtil.getInitialGame(gameProperties);
        logger.info("Started new game");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(game.getCurrHand());
        }
        return game;
    }

    public Game addTileForMe(Game game, int x, int y) {
        logger.info("Start add tile for me method for tile [" + x + "-" + y + "]");
        if (game.getCurrHand().getTilesInBazaar() == 2) {
            return game;
        }
        addTileForMe(game.getCurrHand().getTiles(), x, y);
        decreaseBazaarTileCount(game);
        logger.info("Added tile for me");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(game.getCurrHand());
        }
        return game;
    }

    public Game addTileForHim(Game game) {
        logger.info("Start add tile for him method");
        double sum = makeTilesAsInBazaarAndReturnProbabilitiesSum(game.getCurrHand());
        Set<String> keys = getTilesWithHimBetweenZeroAndOne(game.getCurrHand().getTiles());
        addProbabilitiesProportional(game.getCurrHand().getTiles(), keys, sum, HIM);
        addProbabilitiesProportional(game.getCurrHand().getTiles(), keys, -1 * sum, BAZAAR);
        if (game.getCurrHand().getTilesInBazaar() == 2) {
            return game;
        }
        addTileForHim(game.getCurrHand().getTiles());
        decreaseBazaarTileCount(game);
        logger.info("Added tile for him");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(game.getCurrHand());
        }
        return game;
    }

    public Game playForMe(Game game, int x, int y, PlayDirection direction) {
        logger.info("Start play for me method for tile [" + x + "-" + y + "] direction [" + direction.name() + "]");
        makeTileAsPlayed(game.getCurrHand().getTiles().get(TileUtil.getTileUID(x, y)));
        playTile(game.getCurrHand(), x, y, direction);
        logger.info("Play tile for me");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(game.getCurrHand());
        }
        return game;
    }

    public Game playForHim(Game game, int x, int y, PlayDirection direction) {
        logger.info("Start play for him method for tile [" + x + "-" + y + "] direction [" + direction.name() + "]");
        makeTileAsPlayed(game.getCurrHand().getTiles().get(TileUtil.getTileUID(x, y)));
        playTile(game.getCurrHand(), x, y, direction);
        logger.info("Play tile for him");
        if (systemParameterProcessor.getBooleanParameterValue(logTilesAfterMethod)) {
            LoggingProcessor.logTilesFullInfo(game.getCurrHand());
        }
        return game;
    }

    private double makeTilesAsInBazaarAndReturnProbabilitiesSum(Hand hand) {
        double sum = 0.0;
        Set<Tile> tiles = new HashSet<>(hand.getTiles().values());
        if (hand.getTop() != null) {
            sum += makeTilesAsInBazaarAndReturnProbabilitiesSum(tiles, hand.getTop());
        }
        if (hand.getRight() != null) {
            sum += makeTilesAsInBazaarAndReturnProbabilitiesSum(tiles, hand.getRight());
        }
        if (hand.getBottom() != null) {
            sum += makeTilesAsInBazaarAndReturnProbabilitiesSum(tiles, hand.getBottom());
        }
        if (hand.getLeft() != null) {
            sum += makeTilesAsInBazaarAndReturnProbabilitiesSum(tiles, hand.getLeft());
        }
        return sum;
    }

    private double makeTilesAsInBazaarAndReturnProbabilitiesSum(Set<Tile> tiles, int o) {
        double sum = 0.0;
        for (Tile tile : tiles) {
            if (tile.getX() == 0 || tile.getY() == o) {
                sum += tile.getHim();
                tile.setHim(0);
                tile.setMe(0);
                tile.setBazaar(1.0);
            }
        }
        return sum;
    }

    private void playTile(Hand hand, int x, int y, PlayDirection direction) {
        switch (direction) {
            case TOP:
                hand.setTop(x == hand.getTop() ? y : x);
                break;
            case BOTTOM:
                hand.setBottom(x == hand.getBottom() ? y : x);
                break;
            case RIGHT:
                if (!hand.isHasCenter() && x == y) {
                    hand.setTop(x);
                    hand.setRight(x);
                    hand.setBottom(x);
                } else {
                    hand.setRight(x == hand.getRight() ? y : x);
                }
                break;
            case LEFT:
                if (hand.getLeft() == null) {
                    hand.setLeft(x);
                    hand.setRight(y);
                    if (x == y) {
                        hand.setTop(x);
                        hand.setBottom(y);
                    }
                } else if (!hand.isHasCenter() && x == y) {
                    hand.setTop(x);
                    hand.setLeft(x);
                    hand.setBottom(x);
                } else {
                    hand.setLeft(x == hand.getLeft() ? y : x);
                }
                break;
        }
    }

    private void addTileForHim(Map<String, Tile> tiles) {
        Set<String> keys = getTilesWithBazaarBetweenZeroAndOne(tiles, true);
        addProbabilitiesProportional(tiles, keys, 1, HIM);
        addProbabilitiesProportional(tiles, keys, -1, HIM);
    }

    private void addTileForMe(Map<String, Tile> tiles, int x, int y) {
        Tile tile = tiles.get(TileUtil.getTileUID(x, y));
        double him = tile.getHim();
        double bazaar = tile.getBazaar();
        makeTileForMe(tile);
        addProbabilitiesProportional(tiles, getTilesWithHimBetweenZeroAndOne(tiles), him, HIM);
        addProbabilitiesProportional(tiles, getTilesWithBazaarBetweenZeroAndOne(tiles, false), bazaar, BAZAAR);
    }

    private void makeTileForMe(Tile tile) {
        tile.setMe(1.0);
        tile.setHim(0.0);
        tile.setBazaar(0.0);
    }

    private void makeTileAsPlayed(Tile tile) {
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

    private Set<String> getTilesWithHimBetweenZeroAndOne(Map<String, Tile> tiles) {
        Set<String> keys = new HashSet<>();
        for (String key : tiles.keySet()) {
            Tile tile = tiles.get(key);
            double him = tile.getHim();
            if (!tile.isPlayed() && him > 0.0 && him < 1.0) {
                keys.add(key);
            }
        }
        return keys;
    }

    private Set<String> getTilesWithBazaarBetweenZeroAndOne(Map<String, Tile> tiles, boolean includingOne) {
        Set<String> keys = new HashSet<>();
        for (String key : tiles.keySet()) {
            Tile tile = tiles.get(key);
            double bazaar = tile.getBazaar();
            if (includingOne) {
                if (!tile.isPlayed() && bazaar > 0.0 && bazaar <= 1.0) {
                    keys.add(key);
                }
            } else {
                if (!tile.isPlayed() && bazaar > 0.0 && bazaar < 1.0) {
                    keys.add(key);
                }
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

    private void decreaseBazaarTileCount(Game game) {
        game.getCurrHand().setTilesInBazaar(game.getCurrHand().getTilesInBazaar() - 1);
    }
}
