package ge.ai.domino.manager.game.logging;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoundLogger {

    public static final String DELIMITER = "|";

    private static final String DELIMITER_WITH_SPACES = "     |     ";

    public static final String EQUAL_CHARACTER = ":";

    private static final String EQUAL_CHARACTER_WITH_SPACE = ": ";

    public static final String END_LINE = "\n";

    public static final String NOT = "N";

    public static final String NOT_WITH_SPACES = "N                 ";

    public static final NumberFormat formatter = new DecimalFormat("#0.0000000000000000");

    private static final Logger logger = Logger.getLogger(RoundLogger.class);

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private static final SysParam logTilesAfterMethod = new SysParam("logTilesAfterMethod", "true");

    public static void logRoundFullInfo(Round round) {
        if (systemParameterManager.getBooleanParameterValue(logTilesAfterMethod)) {
            StringBuilder log = new StringBuilder(END_LINE);
            log.append("____________________________________________Round Info____________________________________________").append(END_LINE);
            log.append(gameInfoToString(round.getGameInfo()));
            log.append(tableInfoToString(round.getTableInfo()));
            log.append(myTileToString(new ArrayList<>(round.getMyTiles())));
            log.append(opponentTileToString(round.getOpponentTiles()));
            log.append("_________________________________________________________________________________________________");
            logger.info(log);
        }
    }

    private static StringBuilder gameInfoToString(GameInfo gameInfo) {
        return new StringBuilder()
                .append("__________Game Info__________").append(END_LINE)
                .append("Game ID" + EQUAL_CHARACTER_WITH_SPACE).append(gameInfo.getGameId())
                .append(DELIMITER_WITH_SPACES + "Finished" + EQUAL_CHARACTER_WITH_SPACE).append(gameInfo.isFinished())
                .append(DELIMITER_WITH_SPACES + "My Point" + EQUAL_CHARACTER_WITH_SPACE).append(gameInfo.getMyPoint())
                .append(DELIMITER_WITH_SPACES + "Opponent Point" + EQUAL_CHARACTER_WITH_SPACE).append(gameInfo.getOpponentPoint()).append(END_LINE);
    }

    private static StringBuilder tableInfoToString(TableInfo tableInfo) {
        return new StringBuilder()
                .append("__________Table Info__________").append(END_LINE)
                .append("Left" + EQUAL_CHARACTER_WITH_SPACE).append(playedTileToString(tableInfo.getLeft()))
                .append(DELIMITER_WITH_SPACES + "Right" + EQUAL_CHARACTER_WITH_SPACE).append(playedTileToString(tableInfo.getRight()))
                .append(DELIMITER_WITH_SPACES + "Top" + EQUAL_CHARACTER_WITH_SPACE).append(playedTileToString(tableInfo.getTop()))
                .append(DELIMITER_WITH_SPACES + "Bottom" + EQUAL_CHARACTER_WITH_SPACE).append(playedTileToString(tableInfo.getBottom())).append(END_LINE)
                .append("My Move" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.isMyMove())
                .append(DELIMITER_WITH_SPACES + "With Center" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.isWithCenter())
                .append(DELIMITER_WITH_SPACES + "First Round" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.isFirstRound()).append(END_LINE)
                .append("Omitted Me" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.getRoundBlockingInfo().isOmitMe())
                .append(DELIMITER_WITH_SPACES + "Omitted Opponent" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.getRoundBlockingInfo().isOmitOpponent())
                .append(DELIMITER_WITH_SPACES + "Last Played Not Twin Tile Is Mine" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.getRoundBlockingInfo().isLastNotTwinPlayedTileMy()).append(END_LINE)
                .append("Opponent Tiles" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.getOpponentTilesCount())
                .append(DELIMITER_WITH_SPACES + "Bazaar Tiles" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.getBazaarTilesCount())
                .append(DELIMITER_WITH_SPACES + "Tiles From Bazaar" + EQUAL_CHARACTER_WITH_SPACE).append(tableInfo.getTilesFromBazaar()).append(END_LINE);
    }

    private static String playedTileToString(PlayedTile playedTile) {
        if (playedTile == null) {
            return NOT;
        }
        return String.valueOf(playedTile.getOpenSide());
    }

    private static StringBuilder myTileToString(List<Tile> tiles) {
        StringBuilder info = new StringBuilder();
        info.append("__________My Tiles__________").append(END_LINE);
        if (tiles.isEmpty()) {
            info.append("No tiles").append(END_LINE);
        }
        for (int i = 0; i < tiles.size(); i++) {
            info.append(tiles.get(i));
            if (i != tiles.size() - 1) {
                info.append(DELIMITER_WITH_SPACES);
            }
        }
        info.append(END_LINE);
        return info;
    }

    private static StringBuilder opponentTileToString(Map<Tile, Double> tiles) {
        StringBuilder info = new StringBuilder();
        info.append("__________Opponent Tiles__________").append(END_LINE);
        for (int i = 0; i <= 6; i++) {
            for (int j = 6; j >= i ; j--) {
                Tile tile = new Tile(j, i);
                Double prob = tiles.get(tile);
                info.append(tile).append(EQUAL_CHARACTER_WITH_SPACE).append(prob == null ? NOT_WITH_SPACES : formatter.format(prob));
                if (j != i) {
                    info.append(DELIMITER_WITH_SPACES);
                }
            }
            info.append(END_LINE);
        }
        return info;
    }
}
