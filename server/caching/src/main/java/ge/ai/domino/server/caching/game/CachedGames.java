package ge.ai.domino.server.caching.game;

import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.RoundHistory;
import ge.ai.domino.domain.played.PlayedMove;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CachedGames {

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    private static final Set<Integer> cachedOpponentStart = new HashSet<>();

    private static final Map<Integer, Integer> pointFromLastRound = new HashMap<>();

    private static final Map<Integer, GameHistory> gameHistories = new HashMap<>();

    public static void addGame(Game game) {
        cachedGames.put(game.getId(), game);
    }

    public static Game getGame(int gameId) {
        return cachedGames.get(gameId);
    }

    public static void addOpponentStart(int id) {
        cachedOpponentStart.add(id);
    }

    public static boolean startOpponent(int id) {
        boolean result = cachedOpponentStart.contains(id);
        cachedOpponentStart.remove(id);
        return result;
    }

    public static void addPointFromLastRound(int gameId, int point) {
        pointFromLastRound.put(gameId, getPointFromLastRound(gameId) + point);
    }

    public static int getPointFromLastRound(int gameId) {
        if (pointFromLastRound.get(gameId) != null) {
            return pointFromLastRound.get(gameId);
        }
        return 0;
    }

    public static void addGameHistory(int gameId) {
        gameHistories.put(gameId, new GameHistory());
    }

    public static void addMove(int gameId, PlayedMove playedMove, boolean firstMove) {
        GameHistory gameHistory = gameHistories.get(gameId);
        if (firstMove) {
            RoundHistory roundHistory = new RoundHistory();
            roundHistory.getPlayedMoves().add(playedMove);
            gameHistory.getRoundHistories().add(roundHistory);
        } else {
            gameHistory.getRoundHistories().getLast().getPlayedMoves().add(playedMove);
        }
    }

    public static void removeLastMove(int gameId) {
        GameHistory gameHistory = gameHistories.get(gameId);
        RoundHistory roundHistory = gameHistory.getRoundHistories().getLast();
        roundHistory.getPlayedMoves().removeLast();
        if (roundHistory.getPlayedMoves().isEmpty()) {
            gameHistory.getRoundHistories().removeLast();
        }
    }

    public static GameHistory getGameHistory(int gameId) {
        return gameHistories.get(gameId);
    }
}
