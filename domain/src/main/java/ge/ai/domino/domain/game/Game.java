package ge.ai.domino.domain.game;

import java.util.ArrayDeque;
import java.util.Deque;

public class Game {

    private int id;

    private GameProperties properties;

    private Round currRound;

    private Deque<Round> history = new ArrayDeque<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameProperties getProperties() {
        return properties;
    }

    public void setProperties(GameProperties properties) {
        this.properties = properties;
    }

    public Round getCurrRound() {
        return currRound;
    }

    public void setCurrRound(Round currRound) {
        this.currRound = currRound;
    }

    public Deque<Round> getHistory() {
        return history;
    }

    public void setHistory(Deque<Round> history) {
        this.history = history;
    }
}
