package ge.ai.domino.domain.domino;

import ge.ai.domino.domain.ai.AIExtraInfo;

import java.util.HashMap;
import java.util.Map;

public class Hand {

    private Map<String, Tile> tiles = new HashMap<>();

    private AIPrediction aiPrediction;

    private TableInfo tableInfo;

    private AIExtraInfo aiExtraInfo;

    private GameInfo gameInfo;

    public Map<String, Tile> getTiles() {
        return tiles;
    }

    public void setTiles(Map<String, Tile> tiles) {
        this.tiles = tiles;
    }

    public AIPrediction getAiPrediction() {
        return aiPrediction;
    }

    public void setAiPrediction(AIPrediction aiPrediction) {
        this.aiPrediction = aiPrediction;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public AIExtraInfo getAiExtraInfo() {
        return aiExtraInfo;
    }

    public void setAiExtraInfo(AIExtraInfo aiExtraInfo) {
        this.aiExtraInfo = aiExtraInfo;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }
}
