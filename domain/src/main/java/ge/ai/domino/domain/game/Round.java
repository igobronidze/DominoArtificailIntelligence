package ge.ai.domino.domain.game;

import ge.ai.domino.domain.move.Move;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Round {

    private Set<Tile> myTiles = new HashSet<>();

    private Map<Tile, Float> opponentTiles = new HashMap<>();

    private Move aiPrediction;

    private TableInfo tableInfo;

    private GameInfo gameInfo;

    private float heuristicValue;

    public Set<Tile> getMyTiles() {
        return myTiles;
    }

    public void setMyTiles(Set<Tile> myTiles) {
        this.myTiles = myTiles;
    }

    public Map<Tile, Float> getOpponentTiles() {
        return opponentTiles;
    }

    public void setOpponentTiles(Map<Tile, Float> opponentTiles) {
        this.opponentTiles = opponentTiles;
    }

    public Move getAiPrediction() {
        return aiPrediction;
    }

    public void setAiPrediction(Move aiPrediction) {
        this.aiPrediction = aiPrediction;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public float getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(float heuristicValue) {
        this.heuristicValue = heuristicValue;
    }
}
