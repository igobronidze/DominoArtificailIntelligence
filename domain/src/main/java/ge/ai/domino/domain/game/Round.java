package ge.ai.domino.domain.game;

import java.util.*;

public class Round {

    private Set<Tile> myTiles = new HashSet<>();

    private Map<Tile, Float> opponentTiles = new HashMap<>();

    private List<AiPrediction> aiPredictions;

    private TableInfo tableInfo;

    private GameInfo gameInfo;

    private float heuristicValue;

    private ParentRound parentRound;

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

    public List<AiPrediction> getAiPredictions() {
        return aiPredictions;
    }

    public void setAiPredictions(List<AiPrediction> aiPredictions) {
        this.aiPredictions = aiPredictions;
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

    public ParentRound getParentRound() {
        return parentRound;
    }

    public void setParentRound(ParentRound parentRound) {
        this.parentRound = parentRound;
    }
}
