package ge.ai.domino.domain.game;

import java.util.*;

public class Round {

    private Set<Tile> myTiles = new HashSet<>();

    private Map<Tile, Double> opponentTiles = new HashMap<>();

    private List<AiPrediction> aiPredictions;

    private TableInfo tableInfo;

    private GameInfo gameInfo;

    public Set<Tile> getMyTiles() {
        return myTiles;
    }

    public void setMyTiles(Set<Tile> myTiles) {
        this.myTiles = myTiles;
    }

    public Map<Tile, Double> getOpponentTiles() {
        return opponentTiles;
    }

    public void setOpponentTiles(Map<Tile, Double> opponentTiles) {
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
}
