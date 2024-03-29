package ge.ai.domino.caching.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.domain.played.PlayedMove;
import ge.ai.domino.serverutil.CloneUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedGames {

    private static final Logger logger = Logger.getLogger(CachedGames.class);

    private static final Map<Integer, Game> cachedGames = new HashMap<>();

    private static final Map<Integer, GameHistory> createdGameHistory = new HashMap<>();

    private static final Map<Integer, Map<Tile, Integer>> tilesOrder = new HashMap<>();

    private static final Map<Integer, Map<MoveDirection, MoveDirection>> directionsMap = new HashMap<>();

    public static void addCreatedGameHistory(int gameId, GameHistory gameHistory) {
        createdGameHistory.put(gameId, gameHistory);
    }

    public static GameHistory getCreatedGameHistory(int gameId) {
        return createdGameHistory.get(gameId);
    }

    public static void removeCreatedGameHistory(int gameId) {
        createdGameHistory.remove(gameId);
    }

    public static void addGame(Game game) {
        cachedGames.put(game.getId(), game);
        tilesOrder.put(game.getId(), new HashMap<>());
        initDirectionsMap(game.getId());
    }

    public static void removeGame(int gameId) {
        cachedGames.remove(gameId);
        tilesOrder.remove(gameId);
        directionsMap.remove(gameId);
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
        logger.info("Added move in game history, size[" + cachedGames.get(gameId).getGameHistory().getPlayedMoves().size() + "]");
    }

    public static void removeLastMove(int gameId) throws DAIException {
        GameHistory gameHistory = cachedGames.get(gameId).getGameHistory();
        if (gameHistory.getPlayedMoves().size() <= 1) {
            logger.warn("Move history is empty gameId[" + gameId + "]");
            throw new DAIException("movesHistoryIsEmpty");
        } else {
            PlayedMove playedMove = gameHistory.getPlayedMoves().removeLast();
            if (playedMove.getType() == MoveType.ADD_FOR_OPPONENT || playedMove.getType() == MoveType.PLAY_FOR_OPPONENT) {
                cachedGames.get(gameId).getOpponentPlays().removeLast();
            }
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

    public static void addOpponentPlay(int gameId, OpponentPlay opponentPlay) {
        cachedGames.get(gameId).getOpponentPlays().addLast(opponentPlay);
    }

    public static List<OpponentPlay> getOpponentPlays(int gameId) {
        return new ArrayList<>(cachedGames.get(gameId).getOpponentPlays());
    }

    public static Map<Tile, Integer> getTilesOrder(int gameId) {
        return tilesOrder.get(gameId);
    }

    public static void addTileForOrder(int gameId, Tile tile) {
        tilesOrder.get(gameId).put(tile, tilesOrder.get(gameId).size() + 1);
    }

    public static void clearTilesOrder(int gameId) {
        tilesOrder.get(gameId).clear();
    }

    public static Map<MoveDirection, MoveDirection> getDirectionsMap(int gameId) {
        return directionsMap.get(gameId);
    }

    public static void initDirectionsMap(int gameId) {
        Map<MoveDirection, MoveDirection> map = new HashMap<>();
        map.put(MoveDirection.LEFT, MoveDirection.LEFT);
        map.put(MoveDirection.RIGHT, MoveDirection.RIGHT);
        map.put(MoveDirection.BOTTOM, MoveDirection.BOTTOM);
        map.put(MoveDirection.TOP, MoveDirection.TOP);
        directionsMap.put(gameId, map);
    }
}
