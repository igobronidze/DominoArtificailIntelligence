package ge.ai.domino.domain.ai;

import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.Tile;

public class PossibleTurn {

    private Tile tile;

    private PlayDirection direction;

    public PossibleTurn() {}

    public PossibleTurn(Tile tile, PlayDirection direction) {
        this.tile = tile;
        this.direction = direction;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public PlayDirection getDirection() {
        return direction;
    }

    public void setDirection(PlayDirection direction) {
        this.direction = direction;
    }
}
