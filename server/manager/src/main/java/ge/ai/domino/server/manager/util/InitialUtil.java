package ge.ai.domino.server.manager.util;

import ge.ai.domino.domain.ai.HeuristicInfo;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.server.manager.played.PlayedGameManager;

import java.util.HashMap;
import java.util.Map;

public class InitialUtil {

    private static final PlayedGameManager playedGameManager = new PlayedGameManager();

    private static final double INITIAL_PROBABILITY_FOR_OPPONENT = 1.0/ 4;

    private static final int INITIAL_COUNT_TILES_IN_BAZAAR = 21;

    private static final int INITIAL_COUNT_TILES_FOR_OPPONENT = 7;

    public static Game getInitialGame(GameProperties gameProperties) {
        Game game = new Game();
        game.setProperties(gameProperties);
        game.setCurrRound(getInitialRound());
        game.setId(playedGameManager.addPlayedGame(gameProperties));
        game.getCurrRound().getGameInfo().setGameId(game.getId());
        return game;
    }

    public static Round getInitialRound() {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setMyMove(true);
        tableInfo.setBazaarTilesCount(INITIAL_COUNT_TILES_IN_BAZAAR);
        tableInfo.setOpponentTilesCount(INITIAL_COUNT_TILES_FOR_OPPONENT);
        tableInfo.setFirstRound(true);
        Round round = new Round();
        round.setOpponentTiles(getInitialTiles());
        round.setTableInfo(tableInfo);
        round.setGameInfo(new GameInfo());
        round.setHeuristicInfo(new HeuristicInfo());
        return round;
    }

    private static Map<Integer, OpponentTile> getInitialTiles() {
        Map<Integer, OpponentTile> tiles = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            for (int j = i; j >= 0; j--) {
                OpponentTile tile = new OpponentTile(i, j, INITIAL_PROBABILITY_FOR_OPPONENT);
                tiles.put(tile.hashCode(), tile);
            }
        }
        return tiles;
    }
}
