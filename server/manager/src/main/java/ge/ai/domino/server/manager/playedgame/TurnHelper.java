package ge.ai.domino.server.manager.playedgame;

import ge.ai.domino.domain.domino.game.PlayDirection;
import ge.ai.domino.domain.domino.played.Turn;
import ge.ai.domino.domain.domino.played.TurnType;

public class TurnHelper {

    public static Turn getOmittedMeTurn() {
        Turn turn = new Turn();
        turn.setType(TurnType.OMIT_ME);
        return turn;
    }

    public static Turn getOmittedHimTurn() {
        Turn turn = new Turn();
        turn.setType(TurnType.OMIT_HIM);
        return turn;
    }

    public static Turn getAddInitialTileForMeTurn(int x, int y) {
        Turn turn = new Turn();
        turn.setType(TurnType.ADD_INIT_TILE_FOR_ME);
        turn.setX(x);
        turn.setY(y);
        return turn;
    }

    public static Turn getAddTileForMeTurn(int x, int y) {
        Turn turn = new Turn();
        turn.setType(TurnType.ADD_FOR_ME);
        turn.setX(x);
        turn.setY(y);
        return turn;
    }

    public static Turn getAddTileForHimTurn() {
        Turn turn = new Turn();
        turn.setType(TurnType.ADD_FOR_HIM);
        return turn;
    }

    public static Turn getPlayForMeTurn(int x, int y, PlayDirection direction) {
        Turn turn = new Turn();
        turn.setType(TurnType.PLAY_FOR_ME);
        turn.setX(x);
        turn.setY(y);
        turn.setDirection(direction);
        return turn;
    }

    public static Turn getPlayForHimTurn(int x, int y, PlayDirection direction) {
        Turn turn = new Turn();
        turn.setType(TurnType.PLAY_FOR_HIM);
        turn.setX(x);
        turn.setY(y);
        turn.setDirection(direction);
        return turn;
    }
}
