package ge.ai.domino.domain.game;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.played.GameHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
@Setter
@NoArgsConstructor
public class Game {

    private int id;

    private GameProperties properties;

    private Deque<Round> rounds = new ArrayDeque<>();

    private GameHistory gameHistory;

    private Deque<OpponentPlay> opponentPlays = new ArrayDeque<>();

    private int leftTilesCountFromLastRound;

    private boolean opponentNextRoundBeginner;

    private int opponentLeftTilesCount;

    public Game(int id, GameProperties properties, GameHistory gameHistory) {
        this.id = id;
        this.properties = properties;
        this.gameHistory = gameHistory;
    }
}
