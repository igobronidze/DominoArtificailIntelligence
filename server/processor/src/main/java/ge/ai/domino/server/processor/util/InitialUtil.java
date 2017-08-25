package ge.ai.domino.server.processor.util;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.Tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InitialUtil {

    private static final double INITIAL_PROBABILITY_FOR_HIM = 1.0/ 4;

    private static final double INITIAL_PROBABILITY_FOT_BAZAAR = 3.0 / 4;

    private static final int INITIAL_COUNT_TILES_IN_BAZAAR = 21;

    public static Game getInitialGame(GameProperties gameProperties) {
        Hand hand = new Hand();
        hand.setMyTurn(gameProperties.isStart());
        hand.setTiles(getInitialTiles());
        hand.setTilesInBazaar(INITIAL_COUNT_TILES_IN_BAZAAR);
        Game game = new Game();
        game.setGameProperties(gameProperties);
        game.setCurrHand(hand);
        game.setId(new Random().nextInt());  // TODO[IG] it temporary, id may set by database
        return game;
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
                tiles.put(ge.ai.domino.util.tile.TileUtil.getTileUID(i, j), tile);
            }
        }
        return tiles;
    }
}
