package ge.ai.domino.server.manager.util;

import ge.ai.domino.domain.ai.AIExtraInfo;
import ge.ai.domino.domain.domino.game.Game;
import ge.ai.domino.domain.domino.game.GameInfo;
import ge.ai.domino.domain.domino.game.GameProperties;
import ge.ai.domino.domain.domino.game.Hand;
import ge.ai.domino.domain.domino.game.TableInfo;
import ge.ai.domino.domain.domino.game.Tile;
import ge.ai.domino.util.tile.TileUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InitialUtil {

    private static final double INITIAL_PROBABILITY_FOR_HIM = 1.0/ 4;

    private static final int INITIAL_COUNT_TILES_IN_BAZAAR = 21;

    private static final int INITIAL_COUNT_TILES_FOR_HIM = 7;

    public static Game getInitialGame(GameProperties gameProperties) {
        Game game = new Game();
        game.setGameProperties(gameProperties);
        game.setCurrHand(getInitialHand());
        game.setId(new Random().nextInt());  // TODO[IG] it temporary, id may set by database
        game.getCurrHand().getGameInfo().setGameId(game.getId());
        return game;
    }

    public static Hand getInitialHand() {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setMyTurn(true);
        tableInfo.setBazaarTilesCount(INITIAL_COUNT_TILES_IN_BAZAAR);
        tableInfo.setHimTilesCount(INITIAL_COUNT_TILES_FOR_HIM);
        tableInfo.setFirstHand(true);
        Hand hand = new Hand();
        hand.setTiles(getInitialTiles());
        hand.setTableInfo(tableInfo);
        hand.setGameInfo(new GameInfo());
        hand.setAiExtraInfo(new AIExtraInfo());
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
                tiles.put(TileUtil.getTileUID(i, j), tile);
            }
        }
        return tiles;
    }
}
