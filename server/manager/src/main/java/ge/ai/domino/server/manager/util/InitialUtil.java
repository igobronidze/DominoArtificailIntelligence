package ge.ai.domino.server.manager.util;

import ge.ai.domino.domain.ai.AIExtraInfo;
import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.util.tile.TileUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InitialUtil {

    private static final double INITIAL_PROBABILITY_FOR_HIM = 1.0/ 4;

    private static final double INITIAL_PROBABILITY_FOT_BAZAAR = 3.0 / 4;

    private static final int INITIAL_COUNT_TILES_IN_BAZAAR = 21;

    private static final int INITIAL_COUNT_TILES_FOR_HIM = 7;

    public static Game getInitialGame(GameProperties gameProperties) {
        Game game = new Game();
        game.setGameProperties(gameProperties);
        game.setCurrHand(getInitialHand(true));
        game.setId(new Random().nextInt());  // TODO[IG] it temporary, id may set by database
        game.getCurrHand().getGameInfo().setGameId(game.getId());
        return game;
    }

    public static Hand getInitialHand(boolean startMe) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setMyTurn(startMe);
        tableInfo.setBazaarTilesCount(INITIAL_COUNT_TILES_IN_BAZAAR);
        tableInfo.setHimTilesCount(INITIAL_COUNT_TILES_FOR_HIM);
        Hand hand = new Hand();
        hand.setTiles(getInitialTiles());
        hand.setTableInfo(tableInfo);
        hand.setAiExtraInfo(new AIExtraInfo());
        hand.setGameInfo(new GameInfo());
        return hand;
    }

    private static Map<String, Tile> getInitialTiles() {
        Map<String, Tile> tiles = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            for (int j = i; j >= 0; j--) {
                Tile tile = new Tile();
                tile.setX(i);
                tile.setY(j);
                tile.setHim(INITIAL_PROBABILITY_FOR_HIM);
                tile.setBazaar(INITIAL_PROBABILITY_FOT_BAZAAR);
                tiles.put(TileUtil.getTileUID(i, j), tile);
            }
        }
        return tiles;
    }
}
