package ge.ai.domino.server.manager.domino.helper;

import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.Tile;
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

    private final static SysParam logOnVirtualMode = new SysParam("logOnVirtualMode", "false");

    public static void logHandFullInfo(Hand hand, boolean virtualMode) {
        if (systemParameterManager.getBooleanParameterValue(logTilesAfterMethod)) {
            if (!virtualMode || systemParameterManager.getBooleanParameterValue(logOnVirtualMode)) {
                NumberFormat formatter = new DecimalFormat("#0.0000");
                Map<String, Tile> tiles = hand.getTiles();
                int k = 0;
                StringBuilder log = new StringBuilder("Me:" + hand.getGameInfo().getMyPoints() + ", HIM:" + hand.getGameInfo().getHimPoints() + System.lineSeparator());
                log.append("Tiles info(format - HIM  IS_MINE  IS_PLAYED)").append(System.lineSeparator());
                for (int i = 6; i >= 0; i--) {
                    for (int j = i; j >= 0; j--) {
                        String uid = TileUtil.getTileUID(i, j);
                        Tile tile = tiles.get(uid);
                        log.append(uid).append("  ").append(formatter.format(tile.getHim())).append("  ").append("  ").append(tile.isMine() ? "1" : "0").append("  ").
                                append(tile.isPlayed()? "1" : "0");
                        k++;
                        if (k % 2 == 0) {
                            log.append(System.lineSeparator());
                            k = 0;
                        } else {
                            log.append("        ");
                        }
                    }
                }
                logger.info(log);
            }
        }
    }

    public static void logInfoOnTurn(String text, boolean virtualMode) {
        if (!virtualMode || systemParameterManager.getBooleanParameterValue(logOnVirtualMode)) {
            logger.info(text);
        }
    }
}
