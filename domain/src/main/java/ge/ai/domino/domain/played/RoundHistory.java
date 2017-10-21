package ge.ai.domino.domain.played;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayDeque;
import java.util.Deque;

@XmlType(name = "RoundHistory")
public class RoundHistory {

    private Deque<PlayedMove> playedMoves = new ArrayDeque<>();

    public Deque<PlayedMove> getPlayedMoves() {
        return playedMoves;
    }

    public void setPlayedMoves(Deque<PlayedMove> playedMoves) {
        this.playedMoves = playedMoves;
    }
}
