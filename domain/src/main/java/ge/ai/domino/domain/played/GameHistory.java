package ge.ai.domino.domain.played;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayDeque;
import java.util.Deque;

@XmlRootElement(name = "GameHistory")
public class GameHistory {

    private Deque<PlayedMove> playedMoves = new ArrayDeque<>();

    private Deque<OpponentPlay> opponentPlays = new ArrayDeque<>();

    public Deque<PlayedMove> getPlayedMoves() {
        return playedMoves;
    }

    public void setPlayedMoves(Deque<PlayedMove> playedMoves) {
        this.playedMoves = playedMoves;
    }

    public Deque<OpponentPlay> getOpponentPlays() {
        return opponentPlays;
    }

    public void setOpponentPlays(Deque<OpponentPlay> opponentPlays) {
        this.opponentPlays = opponentPlays;
    }
}
