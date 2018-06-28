package ge.ai.domino.serverutil;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.RoundBlockingInfo;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedTile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CloneUtil {

    public static Round getClone(Round round) {
        Round clone = new Round();
        clone.setTableInfo(getClone(round.getTableInfo()));
        clone.setGameInfo(getClone(round.getGameInfo()));
        clone.setOpponentTiles(getClone((round.getOpponentTiles())));
        clone.setMyTiles(new HashSet<>(round.getMyTiles()));
        return clone;
    }

    public static Map<Tile, Double> getClone(Map<Tile, Double> opponentTiles) {
        Map<Tile, Double> clone = new HashMap<>();
        for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
            clone.put(entry.getKey(), entry.getValue());
        }
        return clone;
    }

    private static TableInfo getClone(TableInfo tableInfo) {
        TableInfo clone = new TableInfo();
        clone.setTop(getClone(tableInfo.getTop()));
        clone.setRight(getClone(tableInfo.getRight()));
        clone.setBottom(getClone(tableInfo.getBottom()));
        clone.setLeft(getClone(tableInfo.getLeft()));
        clone.setOpponentTilesCount(tableInfo.getOpponentTilesCount());
        clone.setBazaarTilesCount(tableInfo.getBazaarTilesCount());
        clone.setMyMove(tableInfo.isMyMove());
        clone.setWithCenter(tableInfo.isWithCenter());
        clone.setTilesFromBazaar(tableInfo.getTilesFromBazaar());
        clone.setRoundBlockingInfo(getClone(tableInfo.getRoundBlockingInfo()));
        clone.setFirstRound(tableInfo.isFirstRound());
        return clone;
    }

    private static PlayedTile getClone(PlayedTile playedTile) {
        if (playedTile == null) {
            return null;
        }
        PlayedTile clone = new PlayedTile();
        clone.setConsiderInSum(playedTile.isConsiderInSum());
        clone.setTwin(playedTile.isTwin());
        clone.setCenter(playedTile.isCenter());
        clone.setOpenSide(playedTile.getOpenSide());
        return clone;
    }

    private static GameInfo getClone(GameInfo gameInfo) {
        GameInfo clone = new GameInfo();
        clone.setGameId(gameInfo.getGameId());
        clone.setMyPoint(gameInfo.getMyPoint());
        clone.setOpponentPoint(gameInfo.getOpponentPoint());
        clone.setFinished(gameInfo.isFinished());
        return clone;
    }

    private static RoundBlockingInfo getClone(RoundBlockingInfo roundBlockingInfo) {
        RoundBlockingInfo clone = new RoundBlockingInfo();
        clone.setOmitMe(roundBlockingInfo.isOmitMe());
        clone.setOmitOpponent(roundBlockingInfo.isOmitOpponent());
        clone.setLastNotTwinPlayedTileMy(roundBlockingInfo.isLastNotTwinPlayedTileMy());
        return clone;
    }
}
