package ge.ai.domino.server.caching.domino;

import ge.ai.domino.domain.domino.game.Game;
import ge.ai.domino.domain.domino.played.GameHistory;
import ge.ai.domino.domain.domino.played.HandHistory;
import ge.ai.domino.domain.domino.played.Turn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CachedDominoGames {

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    private static final Set<Integer> cachedHimStart = new HashSet<>();

    private static final Map<Integer, Integer> pointFromLastHand = new HashMap<>();

    private static final Map<Integer, GameHistory> gameHistories = new HashMap<>();

    public static void addGame(Game game) {
        cachedGames.put(game.getId(), game);
    }

    public static Game getGame(int gameId) {
        return cachedGames.get(gameId);
    }

    public static void addHimStart(int id) {
        cachedHimStart.add(id);
    }

    public static boolean startHim(int id) {
        boolean result = cachedHimStart.contains(id);
        cachedHimStart.remove(id);
        return result;
    }

    public static void addPointFromLastHand(int gameId, int point) {
        pointFromLastHand.put(gameId, getPointFromLastHand(gameId) + point);
    }

    public static int getPointFromLastHand(int gameId) {
        if (pointFromLastHand.get(gameId) != null) {
            return pointFromLastHand.get(gameId);
        }
        return 0;
    }

    public static void addGameHistory(int gameId) {
        gameHistories.put(gameId, new GameHistory());
    }

    public static void addTurn(int gameId, Turn turn, boolean firstTurn) {
        GameHistory gameHistory = gameHistories.get(gameId);
        if (firstTurn) {
            HandHistory handHistory = new HandHistory();
            handHistory.getTurns().add(turn);
            gameHistory.getHandHistories().add(handHistory);
        } else {
            gameHistory.getHandHistories().getLast().getTurns().add(turn);
        }
    }

    public static void removeLastTurn(int gameId) {
        GameHistory gameHistory = gameHistories.get(gameId);
        HandHistory handHistory = gameHistory.getHandHistories().getLast();
        handHistory.getTurns().removeLast();
        if (handHistory.getTurns().isEmpty()) {
            gameHistory.getHandHistories().removeLast();
        }
    }

    public static GameHistory getGameHistory(int gameId) {
        return gameHistories.get(gameId);
    }
}
