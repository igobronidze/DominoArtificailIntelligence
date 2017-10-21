package ge.ai.domino.server.manager.util;

import ge.ai.domino.domain.ai.AIPrediction;
import ge.ai.domino.domain.ai.HeuristicInfo;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.PlayedTile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CloneUtil {

    public static Round getClone(Round round) {
        Round clone = new Round();
        clone.setAiPrediction(getClone(round.getAiPrediction()));
        clone.setTableInfo(getClone(round.getTableInfo()));
        clone.setGameInfo(getClone(round.getGameInfo()));
        clone.setOpponentTiles(getClone((round.getOpponentTiles())));
        clone.setMyTiles(new HashSet<>(round.getMyTiles()));
        clone.setHeuristicInfo(getClone(round.getHeuristicInfo()));
        return clone;
    }

    public static Round getCloneForMinMax(Round round) {
        Round clone = new Round();
        clone.setTableInfo(getClone(round.getTableInfo()));
        clone.setGameInfo(getClone(round.getGameInfo()));
        clone.setOpponentTiles(getClone(round.getOpponentTiles()));
        clone.setMyTiles(new HashSet<>(round.getMyTiles()));
        clone.setHeuristicInfo(getClone(round.getHeuristicInfo()));
        return clone;
    }

    private static Map<Integer, OpponentTile> getClone(Map<Integer, OpponentTile> tiles) {
        Map<Integer, OpponentTile> clone = new HashMap<>();
        for (OpponentTile tile : tiles.values()) {
            clone.put(tile.hashCode(), new OpponentTile(tile.getLeft(), tile.getRight(), tile.getProb()));
        }
        return clone;
    }

    private static AIPrediction getClone(AIPrediction aiPrediction) {
        if (aiPrediction == null) {
            return null;
        }
        AIPrediction clone = new AIPrediction();
        clone.setLeft(aiPrediction.getLeft());
        clone.setRight(aiPrediction.getRight());
        clone.setDirection(aiPrediction.getDirection());
        return clone;
    }

    private static TableInfo getClone(TableInfo tableInfo) {
        TableInfo clone = new TableInfo();
        clone.setTop(getClone(tableInfo.getTop()));
        clone.setRight(getClone(tableInfo.getRight()));
        clone.setBottom(getClone(tableInfo.getBottom()));
        clone.setLeft(getClone(tableInfo.getLeft()));
        clone.setOpponentTilesCount(tableInfo.getOpponentTilesCount());
        clone.setMyTilesCount(tableInfo.getMyTilesCount());
        clone.setBazaarTilesCount(tableInfo.getBazaarTilesCount());
        clone.setMyMove(tableInfo.isMyMove());
        clone.setWithCenter(tableInfo.isWithCenter());
        clone.setLastPlayedProb(tableInfo.getLastPlayedProb());
        clone.setNeedToAddLeftTiles(tableInfo.isNeedToAddLeftTiles());
        clone.setTilesFromBazaar(tableInfo.getTilesFromBazaar());
        clone.setOmittedMe(tableInfo.isOmittedMe());
        clone.setOmittedOpponent(tableInfo.isOmittedOpponent());
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

    private static HeuristicInfo getClone(HeuristicInfo heuristicInfo) {
        HeuristicInfo clone = new HeuristicInfo();
        clone.setValue(heuristicInfo.getValue());
        return clone;
    }
}
