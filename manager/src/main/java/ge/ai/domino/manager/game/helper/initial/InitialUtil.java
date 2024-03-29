package ge.ai.domino.manager.game.helper.initial;

import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.GameHistory;
import ge.ai.domino.manager.played.PlayedGameManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InitialUtil {

    private static final PlayedGameManager playedGameManager = new PlayedGameManager();

    private static final double INITIAL_PROBABILITY_FOR_OPPONENT = 1.0 / 4;

    private static final int INITIAL_COUNT_TILES_IN_BAZAAR = 21;

    private static final int INITIAL_COUNT_TILES_FOR_OPPONENT = 7;

    public static Game getInitialGame(GameProperties gameProperties, boolean addPlayedGame) {
        Game game = new Game();
        game.setProperties(gameProperties);
        if (addPlayedGame) {
            game.setId(playedGameManager.addPlayedGame(game.getProperties()));
        } else {
            game.setId(new Random().nextInt());
        }
        game.getRounds().push(getInitialRound(game.getId(), true));
        game.setGameHistory(new GameHistory());
        return game;
    }

    public static Round getInitialRound(int gameId, boolean firstRound) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setMyMove(true);
        tableInfo.setBazaarTilesCount(INITIAL_COUNT_TILES_IN_BAZAAR);
        tableInfo.setOpponentTilesCount(INITIAL_COUNT_TILES_FOR_OPPONENT);
        tableInfo.setFirstRound(firstRound);
        Round round = new Round();
        round.setOpponentTiles(getInitialTiles());
        round.setTableInfo(tableInfo);
        round.setGameInfo(new GameInfo());
        round.getGameInfo().setGameId(gameId);
        return round;
    }

    private static Map<Tile, Double> getInitialTiles() {
        Map<Tile, Double> tiles = new HashMap<>();
        for (int i = 0; i < INITIAL_COUNT_TILES_FOR_OPPONENT; i++) {
            for (int j = 0; j <= i; j++) {
                tiles.put(new Tile(i, j), INITIAL_PROBABILITY_FOR_OPPONENT);
            }
        }
        return tiles;
    }
}
