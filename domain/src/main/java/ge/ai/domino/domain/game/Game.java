package ge.ai.domino.domain.game;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.played.GameHistory;

import java.util.ArrayDeque;
import java.util.Deque;

public class Game {

    private int id;

    private GameProperties properties;

    private Deque<Round> rounds = new ArrayDeque<>();

    private GameHistory gameHistory;

    private Deque<OpponentPlay> opponentPlays = new ArrayDeque<>();

    private int leftTilesCountFromLastRound;

    private boolean opponentNextRoundBeginner;

    private int opponentLeftTilesCount;

    public Game() {}

    public Game(int id, GameProperties properties, GameHistory gameHistory) {
        this.id = id;
        this.properties = properties;
        this.gameHistory = gameHistory;
    }

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

    public Deque<Round> getRounds() {
        return rounds;
    }

    public GameHistory getGameHistory() {
        return gameHistory;
    }

    public void setGameHistory(GameHistory gameHistory) {
        this.gameHistory = gameHistory;
    }

    public int getLeftTilesCountFromLastRound() {
        return leftTilesCountFromLastRound;
    }

    public void setLeftTilesCountFromLastRound(int leftTilesCountFromLastRound) {
        this.leftTilesCountFromLastRound = leftTilesCountFromLastRound;
    }

    public boolean isOpponentNextRoundBeginner() {
        return opponentNextRoundBeginner;
    }

    public void setOpponentNextRoundBeginner(boolean opponentNextRoundBeginner) {
        this.opponentNextRoundBeginner = opponentNextRoundBeginner;
    }

    public int getOpponentLeftTilesCount() {
        return opponentLeftTilesCount;
    }

    public void setOpponentLeftTilesCount(int opponentLeftTilesCount) {
        this.opponentLeftTilesCount = opponentLeftTilesCount;
    }

    public Deque<OpponentPlay> getOpponentPlays() {
        return opponentPlays;
    }
}
