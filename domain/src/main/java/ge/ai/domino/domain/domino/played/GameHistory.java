package ge.ai.domino.domain.domino.played;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayDeque;
import java.util.Deque;

@XmlRootElement(name = "GameHistory")
public class GameHistory {

    private Deque<HandHistory> handHistories = new ArrayDeque<>();

    public Deque<HandHistory> getHandHistories() {
        return handHistories;
    }

    public void setHandHistories(Deque<HandHistory> handHistories) {
        this.handHistories = handHistories;
    }
}
