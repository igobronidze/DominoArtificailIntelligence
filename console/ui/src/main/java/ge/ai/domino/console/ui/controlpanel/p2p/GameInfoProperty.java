package ge.ai.domino.console.ui.controlpanel.p2p;

import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.game.GameInfo;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GameInfoProperty {

    private IntegerProperty gameId;

    private IntegerProperty myPoint;

    private IntegerProperty opponentPoint;

    private StringProperty winner;

    public GameInfoProperty(GameInfo gameInfo) {
        gameId = new SimpleIntegerProperty(gameInfo.getGameId());
        myPoint = new SimpleIntegerProperty(gameInfo.getMyPoint());
        opponentPoint = new SimpleIntegerProperty(gameInfo.getOpponentPoint());
        winner = new SimpleStringProperty(gameInfo.getMyPoint() > gameInfo.getOpponentPoint() ? Messages.get("me") : Messages.get("opponent"));
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

    public String getWinner() {
        return winner.get();
    }

    public void setWinner(String winner) {
        this.winner.set(winner);
    }
}
