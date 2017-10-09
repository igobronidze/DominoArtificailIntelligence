package ge.ai.domino.domain.domino.game;

import ge.ai.domino.domain.ai.AIExtraInfo;
import ge.ai.domino.domain.ai.AIPrediction;

import java.util.HashMap;
import java.util.Map;

public class Hand {

    private Map<String, Tile> tiles = new HashMap<>();

    private AIPrediction aiPrediction;

    private TableInfo tableInfo;

    private GameInfo gameInfo;

    private AIExtraInfo aiExtraInfo;

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

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public AIExtraInfo getAiExtraInfo() {
        return aiExtraInfo;
    }

    public void setAiExtraInfo(AIExtraInfo aiExtraInfo) {
        this.aiExtraInfo = aiExtraInfo;
    }
}
