package ge.ai.domino.server.manager.played;

import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.played.PlayedMove;

public class MoveHelper {

    public static PlayedMove getOmittedMeMove() {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.I_OMIT);
        return playedMove;
    }

    public static PlayedMove getOmittedOpponentMove() {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.OPPONENT_OMIT);
        return playedMove;
    }

    public static PlayedMove getAddInitialTileForMeMove(int left, int right) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.ADD_INIT_TILE_FOR_ME);
        playedMove.setLeft(left);
        playedMove.setRight(right);
        return playedMove;
    }

    public static PlayedMove getAddTileForMeMove(int left, int right) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.ADD_FOR_ME);
        playedMove.setLeft(left);
        playedMove.setRight(right);
        return playedMove;
    }

    public static PlayedMove getAddTileForOpponentMove() {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.ADD_FOR_OPPONENT);
        return playedMove;
    }

    public static PlayedMove getPlayForMeMove(int left, int right, MoveDirection direction) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.PLAY_FOR_ME);
        playedMove.setLeft(left);
        playedMove.setRight(right);
        playedMove.setDirection(direction);
        return playedMove;
    }

    public static PlayedMove getPlayForOpponentMove(int left, int right, MoveDirection direction) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.PLAY_FOR_OPPONENT);
        playedMove.setLeft(left);
        playedMove.setRight(right);
        playedMove.setDirection(direction);
        return playedMove;
    }
}
