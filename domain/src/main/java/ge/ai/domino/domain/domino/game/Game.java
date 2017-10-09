package ge.ai.domino.domain.domino.game;

import java.util.ArrayDeque;
import java.util.Deque;

public class Game {

    private int id;

    private GameProperties gameProperties;

    private Hand currHand;

    private Deque<Hand> history = new ArrayDeque<>();

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

    public Deque<Hand> getHistory() {
        return history;
    }

    public void setHistory(Deque<Hand> history) {
        this.history = history;
    }
}
