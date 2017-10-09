package ge.ai.domino.server.manager.domino.logging;

import ge.ai.domino.domain.domino.game.GameInfo;
import ge.ai.domino.domain.domino.game.Hand;
import ge.ai.domino.domain.domino.game.PlayedTile;
import ge.ai.domino.domain.domino.game.TableInfo;
import ge.ai.domino.domain.domino.game.Tile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.util.tile.TileUtil;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

public class DominoLoggingProcessor {

    private static final Logger logger = Logger.getLogger(DominoLoggingProcessor.class);

    private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private static final SysParam logTilesAfterMethod = new SysParam("logTilesAfterMethod", "true");

    private static final SysParam logOnVirtualMode = new SysParam("logOnVirtualMode", "false");

    public static void logHandFullInfo(Hand hand, boolean virtualMode) {
        if (systemParameterManager.getBooleanParameterValue(logTilesAfterMethod)) {
            if (!virtualMode || systemParameterManager.getBooleanParameterValue(logOnVirtualMode)) {
                StringBuilder log = new StringBuilder();
                log.append(gameInfoToString(hand.getGameInfo()));
                log.append(tableInfoToString(hand.getTableInfo()));
                log.append(tileToString(hand.getTiles()));
                logger.info(log);
            }
        }
    }

    private static StringBuilder gameInfoToString(GameInfo gameInfo) {
        StringBuilder info = new StringBuilder();
        info.append("Game Info:").append(System.lineSeparator()).append("Me:").append(gameInfo.getMyPoints()).append(", HIM:").append(gameInfo.getHimPoints()).append(System.lineSeparator());
        return info;
    }

    private static StringBuilder tableInfoToString(TableInfo tableInfo) {
        StringBuilder info = new StringBuilder();
        info.append("Table Info:").append(System.lineSeparator()).append("left:").append(playedTileToString(tableInfo.getLeft())).append(", right:").append(playedTileToString(tableInfo.getRight())).
                append(", top:").append(playedTileToString(tableInfo.getTop())).append(", bottom:").append(playedTileToString(tableInfo.getBottom())).append(System.lineSeparator());
        info.append("My turn:").append(tableInfo.isMyTurn()).append(", With center:").append(tableInfo.isWithCenter()).append(", Need to add left tiles:").append(tableInfo.isNeedToAddLeftTiles())
                .append(", Omitted me:").append(tableInfo.isOmittedMe()).append(", Omitted him:").append(tableInfo.isOmittedHim()).append(System.lineSeparator());
        info.append("My tiles:").append(tableInfo.getMyTilesCount()).append(", Him tiles:").append(tableInfo.getHimTilesCount()).append(", Bazaar tiles:").append(tableInfo.getBazaarTilesCount()).
                append(", Tiles from bazaar:").append(tableInfo.getTileFromBazaar()).append(", Last played:").append(tableInfo.getLastPlayedUID()).append(System.lineSeparator());
        return info;
    }

    private static String playedTileToString(PlayedTile playedTile) {
        if (playedTile == null) {
            return "X";
        }
        return String.valueOf(playedTile.getOpenSide());
    }

    private static StringBuilder tileToString(Map<String, Tile> tiles) {
        StringBuilder info = new StringBuilder();
        info.append("Tiles info:").append(System.lineSeparator()).append("format - HIM  IS_MINE").append(System.lineSeparator());
        int counter = 0;
        NumberFormat formatter = new DecimalFormat("#0.0000");
        for (int i = 6; i >= 0; i--) {
            for (int j = i; j >= 0; j--) {
                String uid = TileUtil.getTileUID(i, j);
                Tile tile = tiles.get(uid);
                if (tile == null ){
                    info.append(uid).append("  played   ");
                } else {
                    info.append(uid).append("  ").append(formatter.format(tile.getHim())).append("  ").append(tile.isMine() ? "1" : "0");
                }
                counter++;
                if (counter % 4 == 0) {
                    info.append(System.lineSeparator());
                    counter = 0;
                } else {
                    info.append("      ");
                }
            }
        }
        return info;
    }

    public static void logInfoOnTurn(String text, boolean virtualMode) {
        if (!virtualMode || systemParameterManager.getBooleanParameterValue(logOnVirtualMode)) {
            logger.info(text);
        }
    }
}
