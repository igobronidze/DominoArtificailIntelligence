package ge.ai.domino.console.ui.controlpanel.p2p;

import ge.ai.domino.domain.game.GameInfo;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameInfoProperty {

    private IntegerProperty gameId;

    private IntegerProperty myPoint;

    private IntegerProperty opponentPoint;

    public GameInfoProperty(GameInfo gameInfo) {
        gameId = new SimpleIntegerProperty(gameInfo.getGameId());
        myPoint = new SimpleIntegerProperty(gameInfo.getMyPoint());
        opponentPoint = new SimpleIntegerProperty(gameInfo.getOpponentPoint());
    }

    public int getGameId() {
        return gameId.get();
    }

    public void setGameId(int gameId) {
        this.gameId.set(gameId);
    }

    public int getMyPoint() {
        return myPoint.get();
    }

    public void setMyPoint(int myPoint) {
        this.myPoint.set(myPoint);
    }

    public int getOpponentPoint() {
        return opponentPoint.get();
    }

    public void setOpponentPoint(int opponentPoint) {
        this.opponentPoint.set(opponentPoint);
    }
}
