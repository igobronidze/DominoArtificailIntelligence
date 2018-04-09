package ge.ai.domino.server.caching.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.PlayedMove;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CachedGames {

    private static final Logger logger = Logger.getLogger(CachedGames.class);

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    public static void addGame(Game game) {
        cachedGames.put(game.getId(), game);
    }

    public static GameProperties getGameProperties(int gameId) {
        return cachedGames.get(gameId).getProperties();
    }

    public static void changeNextRoundBeginner(int gameId, boolean startMe) {
        cachedGames.get(gameId).setOpponentNextRoundBeginner(!startMe);
    }

    public static boolean isOpponentNextRoundBeginner(int gameId) {
        return cachedGames.get(gameId).isOpponentNextRoundBeginner();
    }

    public static void setLeftTilesCountFromLastRound(int gameId, int point) {
        cachedGames.get(gameId).setLeftTilesCountFromLastRound(point);
    }

    public static int getLeftTilesCountFromLastRound(int gameId) {
        return cachedGames.get(gameId).getLeftTilesCountFromLastRound();
    }

    public static void addRound(int gameId, Round round) {
        cachedGames.get(gameId).getRounds().addFirst(round);
    }

    public static Round getCurrentRound(int gameId, boolean clone) {
        if (clone) {
            return CloneUtil.getClone(cachedGames.get(gameId).getRounds().getFirst());
        } else {
            return cachedGames.get(gameId).getRounds().getFirst();
        }
    }

    public static Round getAndRemoveLastRound(int gameId) throws DAIException {
        Game game = cachedGames.get(gameId);
        if (game.getRounds().size() <= 1) {
            logger.warn("Rounds is empty gameId[" + gameId + "]");
            throw new DAIException("roundsIsEmpty");
        } else {
            game.getRounds().removeFirst();
            return game.getRounds().getFirst();
        }
    }

    public static void addMove(int gameId, PlayedMove playedMove) {
        cachedGames.get(gameId).getGameHistory().getPlayedMoves().addLast(playedMove);
    }

    public static void removeLastMove(int gameId) throws DAIException {
        GameHistory gameHistory = cachedGames.get(gameId).getGameHistory();
        if (gameHistory.getPlayedMoves().size() <= 1) {
            logger.warn("Move history is empty gameId[" + gameId + "]");
            throw new DAIException("movesHistoryIsEmpty");
        } else {
            gameHistory.getPlayedMoves().removeLast();
        }
    }

    public static GameHistory getGameHistory(int gameId) {
        return cachedGames.get(gameId).getGameHistory();
    }

    public static void specifyOpponentLeftTilesCount(int gameId, int leftTilesCount) {
        cachedGames.get(gameId).setOpponentLeftTilesCount(leftTilesCount);
    }

    public static int getOpponentLeftTilesCount(int gameId) {
        return cachedGames.get(gameId).getOpponentLeftTilesCount();
    }
}
