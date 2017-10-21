package ge.ai.domino.domain.game;

public class GameInfo {

    private int gameId;

    private int myPoint;

    private int opponentPoint;

    private boolean finished;

    public int getMyPoint() {
        return myPoint;
    }

    public void setMyPoint(int myPoint) {
        this.myPoint = myPoint;
    }

    public int getOpponentPoint() {
        return opponentPoint;
    }

    public void setOpponentPoint(int opponentPoint) {
        this.opponentPoint = opponentPoint;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
