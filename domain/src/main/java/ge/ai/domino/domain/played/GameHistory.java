package ge.ai.domino.domain.played;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayDeque;
import java.util.Deque;

@XmlRootElement(name = "GameHistory")
public class GameHistory {

    private Deque<PlayedMove> playedMoves = new ArrayDeque<>();

    public Deque<PlayedMove> getPlayedMoves() {
        return playedMoves;
    }

    public void setPlayedMoves(Deque<PlayedMove> playedMoves) {
        this.playedMoves = playedMoves;
    }
}
