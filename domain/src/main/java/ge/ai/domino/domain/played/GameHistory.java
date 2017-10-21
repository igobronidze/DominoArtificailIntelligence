package ge.ai.domino.domain.played;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayDeque;
import java.util.Deque;

@XmlRootElement(name = "GameHistory")
public class GameHistory {

    private Deque<RoundHistory> roundHistories = new ArrayDeque<>();

    public Deque<RoundHistory> getRoundHistories() {
        return roundHistories;
    }

    public void setRoundHistories(Deque<RoundHistory> roundHistories) {
        this.roundHistories = roundHistories;
    }
}
