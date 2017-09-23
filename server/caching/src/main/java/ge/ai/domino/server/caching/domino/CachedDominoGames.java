package ge.ai.domino.server.caching.domino;

import ge.ai.domino.domain.domino.Game;

import java.util.HashMap;
import java.util.Map;

public class CachedDominoGames {

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    public static void addGame(Game game) {
        cachedGames.put(game.getId(), game);
    }

    public static Game getGame(int gameId) {
        return cachedGames.get(gameId);
    }
}
