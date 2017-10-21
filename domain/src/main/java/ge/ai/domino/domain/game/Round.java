package ge.ai.domino.domain.game;

import ge.ai.domino.domain.ai.AIPrediction;
import ge.ai.domino.domain.ai.HeuristicInfo;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Round {

    private Set<Tile> myTiles = new HashSet<>();

    private Map<Integer, OpponentTile> opponentTiles = new HashMap<>();

    private AIPrediction aiPrediction;

    private TableInfo tableInfo;

    private GameInfo gameInfo;

    private HeuristicInfo heuristicInfo;

    public Set<Tile> getMyTiles() {
        return myTiles;
    }

    public void setMyTiles(Set<Tile> myTiles) {
        this.myTiles = myTiles;
    }

    public Map<Integer, OpponentTile> getOpponentTiles() {
        return opponentTiles;
    }

    public void setOpponentTiles(Map<Integer, OpponentTile> opponentTiles) {
        this.opponentTiles = opponentTiles;
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

    public HeuristicInfo getHeuristicInfo() {
        return heuristicInfo;
    }

    public void setHeuristicInfo(HeuristicInfo heuristicInfo) {
        this.heuristicInfo = heuristicInfo;
    }
}
