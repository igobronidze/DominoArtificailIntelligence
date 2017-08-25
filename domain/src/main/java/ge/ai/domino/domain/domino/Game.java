package ge.ai.domino.domain.domino;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int id;

    private GameProperties gameProperties;

    private Hand currHand;

    private List<Hand> history = new ArrayList<>();

    private int myPoint;

    private int himPoint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameProperties getGameProperties() {
        return gameProperties;
    }

    public void setGameProperties(GameProperties gameProperties) {
        this.gameProperties = gameProperties;
    }

    public Hand getCurrHand() {
        return currHand;
    }

    public void setCurrHand(Hand currHand) {
        this.currHand = currHand;
    }

    public List<Hand> getHistory() {
        return history;
    }

    public void setHistory(List<Hand> history) {
        this.history = history;
    }

    public int getMyPoint() {
        return myPoint;
    }

    public void setMyPoint(int myPoint) {
        this.myPoint = myPoint;
    }

    public int getHimPoint() {
        return himPoint;
    }

    public void setHimPoint(int himPoint) {
        this.himPoint = himPoint;
    }
}
