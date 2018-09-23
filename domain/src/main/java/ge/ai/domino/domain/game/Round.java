package ge.ai.domino.domain.game;

import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Round implements Serializable {

    private Set<Tile> myTiles = new HashSet<>();

    private Map<Tile, Double> opponentTiles = new HashMap<>();

    private AiPredictionsWrapper aiPredictions = new AiPredictionsWrapper();

    private TableInfo tableInfo;

    private GameInfo gameInfo;

    private String warnMsgKey;

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

    public AiPredictionsWrapper getAiPredictions() {
        return aiPredictions;
    }

    public void setAiPredictions(AiPredictionsWrapper aiPredictions) {
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

    public String getWarnMsgKey() {
        return warnMsgKey;
    }

    public void setWarnMsgKey(String warnMsgKey) {
        this.warnMsgKey = warnMsgKey;
    }
}
