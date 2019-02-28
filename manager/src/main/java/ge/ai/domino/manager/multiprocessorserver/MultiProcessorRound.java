package ge.ai.domino.manager.multiprocessorserver;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.MoveType;

import java.io.Serializable;

public class MultiProcessorRound implements Serializable {

    private int id;

    private Round round;

    private MoveType lastPlayedMoveType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public MoveType getLastPlayedMoveType() {
        return lastPlayedMoveType;
    }

    public void setLastPlayedMoveType(MoveType lastPlayedMoveType) {
        this.lastPlayedMoveType = lastPlayedMoveType;
    }
}
