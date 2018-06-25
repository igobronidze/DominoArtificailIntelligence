package ge.ai.domino.server.manager.game.helper.game;

import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.played.PlayedMove;

public class MoveHelper {

    public static PlayedMove getStartNewRoundMove() {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.START_NEW_ROUND);
        return playedMove;
    }

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

    public static PlayedMove getAddInitialTileForMeMove(Move move) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.ADD_INIT_TILE_FOR_ME);
        playedMove.setLeft(move.getLeft());
        playedMove.setRight(move.getRight());
        return playedMove;
    }

    public static PlayedMove getAddTileForMeMove(Move move) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.ADD_FOR_ME);
        playedMove.setLeft(move.getLeft());
        playedMove.setRight(move.getRight());
        return playedMove;
    }

    public static PlayedMove getAddTileForOpponentMove() {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.ADD_FOR_OPPONENT);
        return playedMove;
    }

    public static PlayedMove getPlayForMeMove(Move move) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.PLAY_FOR_ME);
        playedMove.setLeft(move.getLeft());
        playedMove.setRight(move.getRight());
        playedMove.setDirection(move.getDirection());
        return playedMove;
    }

    public static PlayedMove getPlayForOpponentMove(Move move) {
        PlayedMove playedMove = new PlayedMove();
        playedMove.setType(MoveType.PLAY_FOR_OPPONENT);
        playedMove.setLeft(move.getLeft());
        playedMove.setRight(move.getRight());
        playedMove.setDirection(move.getDirection());
        return playedMove;
    }
}
