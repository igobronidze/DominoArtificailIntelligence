package ge.ai.domino.domain.game;

import java.io.Serializable;

public class GameInitialData implements Serializable {

    private int gameId;

    private int pointsForWin;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPointsForWin() {
        return pointsForWin;
    }

    public void setPointsForWin(int pointsForWin) {
        this.pointsForWin = pointsForWin;
    }
}
