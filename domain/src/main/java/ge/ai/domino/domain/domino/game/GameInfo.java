package ge.ai.domino.domain.domino.game;

public class GameInfo {

    private int gameId;

    private int myPoints;

    private int himPoints;

    private boolean finished;

    public int getMyPoints() {
        return myPoints;
    }

    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    public int getHimPoints() {
        return himPoints;
    }

    public void setHimPoints(int himPoints) {
        this.himPoints = himPoints;
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
