package ge.ai.domino.server.caching.game;

import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.PlayedMove;
import ge.ai.domino.domain.played.RoundHistory;

import java.util.HashMap;
import java.util.Map;

public class CachedGames {

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    public static void addGame(Game game, int gameId) {
        if (gameId != 0 && game.getProperties() == null) {
            game.setProperties(cachedGames.get(gameId).getProperties());
        }
        cachedGames.put(game.getId(), game);
    }

    public static Game getGame(int gameId) {
        return cachedGames.get(gameId);
    }

    public static Round getCurrentRound(int gameId) {
        return cachedGames.get(gameId).getRounds().peek();
    }

    public static void addRound(int gameId, Round round) {
        cachedGames.get(gameId).getRounds().push(round);
    }

    public static void makeOpponentNextRoundBeginner(int gameId) {
        cachedGames.get(gameId).setOpponentNextRoundBeginner(true);
    }

    public static boolean isOpponentNextRoundBeginner(int gameId) {
        return cachedGames.get(gameId).isOpponentNextRoundBeginner();
    }

    public static void addLeftTilesCountFromLastRound(int gameId, int point) {
        cachedGames.get(gameId).setLeftTilesCountFromLastRound(point);
    }

    public static int getLeftTilesCountFromLastRound(int gameId) {
        return cachedGames.get(gameId).getLeftTilesCountFromLastRound();
    }

    public static void addMove(int gameId, PlayedMove playedMove, boolean firstMove) {
        GameHistory gameHistory = cachedGames.get(gameId).getGameHistory();
        if (firstMove) {
            RoundHistory roundHistory = new RoundHistory();
            roundHistory.getPlayedMoves().add(playedMove);
            gameHistory.getRoundHistories().add(roundHistory);
        } else {
            gameHistory.getRoundHistories().getLast().getPlayedMoves().add(playedMove);
        }
    }

    public static void removeLastMove(int gameId) {
        GameHistory gameHistory = cachedGames.get(gameId).getGameHistory();
        RoundHistory roundHistory = gameHistory.getRoundHistories().getLast();
        roundHistory.getPlayedMoves().removeLast();
        if (roundHistory.getPlayedMoves().isEmpty()) {
            gameHistory.getRoundHistories().removeLast();
        }
    }

    public static GameHistory getGameHistory(int gameId) {
        return cachedGames.get(gameId).getGameHistory();
    }
}
