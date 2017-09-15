package ge.ai.domino.domain.domino;

public class GameInfo {

    private int gameId;

    private int myPoints;

    private int himPoints;

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
}
