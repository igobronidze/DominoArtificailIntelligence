package ge.ai.domino.server.processor.util;

import ge.ai.domino.domain.ai.AIExtraInfo;
import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayedTile;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;

import java.util.HashMap;
import java.util.Map;

public class CloneUtil {

    public static Hand getClone(Hand hand) {
        Hand clone = new Hand();
        clone.setAiExtraInfo(getClone(hand.getAiExtraInfo()));
        clone.setAiPrediction(getClone(hand.getAiPrediction()));
        clone.setTableInfo(getClone(hand.getTableInfo()));
        clone.setTiles(getClone(hand.getTiles()));
        return clone;
    }

    private static AIPrediction getClone(AIPrediction aiPrediction) {
        if (aiPrediction == null) {
            return null;
        }
        AIPrediction clone = new AIPrediction();
        clone.setX(aiPrediction.getX());
        clone.setY(aiPrediction.getY());
        clone.setDirection(aiPrediction.getDirection());
        return clone;
    }

    private static TableInfo getClone(TableInfo tableInfo) {
        TableInfo clone = new TableInfo();
        clone.setTop(getClone(tableInfo.getTop()));
        clone.setRight(getClone(tableInfo.getRight()));
        clone.setBottom(getClone(tableInfo.getBottom()));
        clone.setLeft(getClone(tableInfo.getLeft()));
        clone.setHimTilesCount(tableInfo.getHimTilesCount());
        clone.setMyTilesCount(tableInfo.getMyTilesCount());
        clone.setBazaarTilesCount(tableInfo.getBazaarTilesCount());
        clone.setMyTurn(tableInfo.isMyTurn());
        clone.setWithCenter(tableInfo.isWithCenter());
        clone.setLastPlayedUID(tableInfo.getLastPlayedUID());
        return clone;
    }

    private static PlayedTile getClone(PlayedTile playedTile) {
        if (playedTile == null) {
            return null;
        }
        PlayedTile clone = new PlayedTile();
        clone.setCountInSum(playedTile.isCountInSum());
        clone.setDouble(playedTile.isDouble());
        clone.setCenter(playedTile.isCenter());
        clone.setOpenSide(playedTile.getOpenSide());
        return clone;
    }

    private static Tile getClone(Tile tile) {
        Tile clone = new Tile();
        clone.setX(tile.getX());
        clone.setY(tile.getY());
        clone.setPlayed(tile.isPlayed());
        clone.setHim(tile.getHim());
        clone.setMe(tile.getMe());
        clone.setBazaar(tile.getBazaar());
        return clone;
    }

    private static HashMap<String, Tile> getClone(Map<String, Tile> tiles) {
        HashMap<String, Tile> clone = new HashMap<>();
        for (String key : tiles.keySet()) {
            clone.put(key, getClone(tiles.get(key)));
        }
        return clone;
    }

    private static AIExtraInfo getClone(AIExtraInfo aiExtraInfo) {
        if (aiExtraInfo == null) {
            return null;
        }
        AIExtraInfo clone = new AIExtraInfo();
        clone.setHeuristicValue(aiExtraInfo.getHeuristicValue());
        return clone;
    }
}
