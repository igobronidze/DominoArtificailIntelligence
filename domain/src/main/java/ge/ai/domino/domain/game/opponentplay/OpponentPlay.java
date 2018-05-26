package ge.ai.domino.domain.game.opponentplay;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.MoveType;

import java.util.ArrayList;
import java.util.List;

public class OpponentPlay {

    private int id;

    private int gameId;

    private String version;

    private MoveType moveType;

    private Tile tile;

    private OpponentTilesWrapper opponentTiles;

    private List<Integer> possiblePlayNumbers = new ArrayList<>();

    public OpponentPlay() {
    }

    public OpponentPlay(int id, int gameId, String version, MoveType moveType, Tile tile, OpponentTilesWrapper opponentTiles, List<Integer> possiblePlayNumbers) {
        this.id = id;
        this.gameId = gameId;
        this.version = version;
        this.moveType = moveType;
        this.tile = tile;
        this.opponentTiles = opponentTiles;
        this.possiblePlayNumbers = possiblePlayNumbers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public OpponentTilesWrapper getOpponentTiles() {
        return opponentTiles;
    }

    public void setOpponentTiles(OpponentTilesWrapper opponentTiles) {
        this.opponentTiles = opponentTiles;
    }

    public List<Integer> getPossiblePlayNumbers() {
        return possiblePlayNumbers;
    }

    public void setPossiblePlayNumbers(List<Integer> possiblePlayNumbers) {
        this.possiblePlayNumbers = possiblePlayNumbers;
    }
}