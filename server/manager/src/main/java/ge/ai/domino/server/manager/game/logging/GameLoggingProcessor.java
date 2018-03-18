package ge.ai.domino.server.manager.game.logging;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;

public class GameLoggingProcessor {

    private static final Logger logger = Logger.getLogger(GameLoggingProcessor.class);

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private static final SysParam logTilesAfterMethod = new SysParam("logTilesAfterMethod", "true");

    private static final SysParam logOnVirtualMode = new SysParam("logOnVirtualMode", "false");

    public static void logRoundFullInfo(Round round, boolean virtualMode) {
        if (systemParameterManager.getBooleanParameterValue(logTilesAfterMethod)) {
            if (!virtualMode || systemParameterManager.getBooleanParameterValue(logOnVirtualMode)) {
                StringBuilder log = new StringBuilder(System.lineSeparator());
                log.append("_____________________________________Round Info_____________________________________").append(System.lineSeparator());
                log.append(gameInfoToString(round.getGameInfo()));
                log.append(tableInfoToString(round.getTableInfo()));
                log.append(myTileToString(round.getMyTiles()));
                log.append(opponentTileToString(round.getOpponentTiles()));
                log.append("___________________________________________________________________________________");
                logger.info(log);
            }
        }
    }

    private static StringBuilder gameInfoToString(GameInfo gameInfo) {
        StringBuilder info = new StringBuilder();
        info.append("_____Game Info").append(System.lineSeparator())
                .append("Game Id:").append(gameInfo.getGameId())
                .append(",   My Point:").append(gameInfo.getMyPoint())
                .append(",   Opponent Point:").append(gameInfo.getOpponentPoint()).append(System.lineSeparator());
        return info;
    }

    private static StringBuilder tableInfoToString(TableInfo tableInfo) {
        StringBuilder info = new StringBuilder();
        info.append("_____Table Info").append(System.lineSeparator())
                .append("Left:").append(playedTileToString(tableInfo.getLeft()))
                .append(",   Right:").append(playedTileToString(tableInfo.getRight()))
                .append(",   Top:").append(playedTileToString(tableInfo.getTop()))
                .append(",   Bottom:").append(playedTileToString(tableInfo.getBottom())).append(System.lineSeparator())
                .append("My move:").append(tableInfo.isMyMove())
                .append(",   With Center:").append(tableInfo.isWithCenter())
                .append(",   Omitted Me:").append(tableInfo.isOmittedMe())
                .append(",   Omitted Opponent:").append(tableInfo.isOmittedOpponent())
                .append(",   First round:").append(tableInfo.isFirstRound())
                .append(",   Need to add left tiles:").append(tableInfo.isNeedToAddLeftTiles()).append(System.lineSeparator())
                .append("Opponent Tiles:").append(tableInfo.getOpponentTilesCount())
                .append(",   Bazaar Tiles:").append(tableInfo.getBazaarTilesCount())
                .append(",   Last played prob:").append(tableInfo.getLastPlayedProb())
                .append(",   Tiles from bazaar:").append(tableInfo.getTilesFromBazaar()).append(System.lineSeparator());
        return info;
    }

    private static String playedTileToString(PlayedTile playedTile) {
        if (playedTile == null) {
            return "N";
        }
        return String.valueOf(playedTile.getOpenSide());
    }

    private static StringBuilder myTileToString(Collection<Tile> tiles) {
        StringBuilder info = new StringBuilder();
        info.append("_____My Tiles").append(System.lineSeparator());
        int counter = 0;
        if (tiles.isEmpty()) {
            info.append("No tiles").append(System.lineSeparator());
        }
        for (Tile tile : tiles) {
            if (counter != 0) {
                info.append("     ");
            }
            info.append(tile);
            counter++;
            if (counter == 10) {
                info.append(System.lineSeparator());
                counter = 0;
            }
        }
        if (counter != 0) {
            info.append(System.lineSeparator());
        }
        return info;
    }

    private static StringBuilder opponentTileToString(Map<Tile, Float> tiles) {
        StringBuilder info = new StringBuilder();
        info.append("_____Opponent Tiles").append(System.lineSeparator());
        NumberFormat formatter = new DecimalFormat("#0.0000");
        for (int i = 0; i <= 6; i++) {
            for (int j = 6; j >= i ; j--) {
                Tile tile = new Tile(j, i);
                Float prob = tiles.get(tile);
                info.append(tile).append(" ").append(prob == null ? "N     " : formatter.format(prob)).append("     ");
            }
            info.append(System.lineSeparator());
        }
        return info;
    }

    public static void logInfoAboutMove(String text, boolean virtualMode) {
        if (!virtualMode || systemParameterManager.getBooleanParameterValue(logOnVirtualMode)) {
            logger.info(text);
        }
    }
}
