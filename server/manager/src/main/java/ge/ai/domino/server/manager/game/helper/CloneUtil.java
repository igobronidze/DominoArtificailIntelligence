package ge.ai.domino.server.manager.game.helper;

import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.played.PlayedTile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CloneUtil {

    public static Round getClone(Round round) {
        Round clone = new Round();
        clone.setAiPrediction(getClone(round.getAiPrediction()));
        clone.setTableInfo(getClone(round.getTableInfo()));
        clone.setGameInfo(getClone(round.getGameInfo()));
        clone.setOpponentTiles(getClone((round.getOpponentTiles())));
        clone.setMyTiles(getClone(round.getMyTiles()));
        clone.setHeuristicValue(round.getHeuristicValue());
        return clone;
    }

    private static Set<Tile> getClone(Set<Tile> myTiles) {
        Set<Tile> clone = new HashSet<>();
        clone.addAll(myTiles);
        return clone;
    }

    private static Map<Tile, Float> getClone(Map<Tile, Float> opponentTiles) {
        Map<Tile, Float> clone = new HashMap<>();
        for (Map.Entry<Tile, Float> entry : opponentTiles.entrySet()) {
            clone.put(entry.getKey(), entry.getValue());
        }
        return clone;
    }

    private static Move getClone(Move aiPrediction) {
        if (aiPrediction == null) {
            return null;
        }
        return new Move(aiPrediction.getLeft(), aiPrediction.getRight(), aiPrediction.getDirection());
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
}
