package ge.ai.domino.server.caching.domino;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.TileOwner;

import java.util.HashMap;
import java.util.Map;

public class CachedDominoGames {

    private static Map<Integer, Integer> tilesFromBazaar = new HashMap<>();

    private static Map<Integer, TileOwner> omittedGames = new HashMap<>();

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    public static void addGame(Game game) {
        cachedGames.put(game.getId(), game);
    }

    public static Game getGame(int gameId) {
        return cachedGames.get(gameId);
    }

    public static void addOmitted(int gameId, TileOwner value) {
        omittedGames.put(gameId, value);
    }

    public static TileOwner getOmittedValue(int gameId) {
        return omittedGames.get(gameId);
    }

    public static void addTileFromBazaar(int gameId, int count) {
        tilesFromBazaar.put(gameId, count);
    }

    public static Integer getTileFromBazaar(int gameId) {
        return tilesFromBazaar.get(gameId);
    }

    public static void clearOmittedAndTiles(int gameId) {
        omittedGames.remove(gameId);
        tilesFromBazaar.remove(gameId);
    }
}
