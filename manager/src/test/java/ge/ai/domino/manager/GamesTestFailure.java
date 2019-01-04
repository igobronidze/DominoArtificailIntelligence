package ge.ai.domino.manager;

import ge.ai.domino.domain.played.ReplayMoveInfo;

public class GamesTestFailure {

    private ReplayMoveInfo replayMoveInfo;

    private PossibleMoves possibleMoves;

    int index;

    public ReplayMoveInfo getReplayMoveInfo() {
        return replayMoveInfo;
    }

    public void setReplayMoveInfo(ReplayMoveInfo replayMoveInfo) {
        this.replayMoveInfo = replayMoveInfo;
    }

    public PossibleMoves getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(PossibleMoves possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
