package ge.ai.domino.domain.domino.played;

import java.util.ArrayList;
import java.util.List;

public class GameHistory {

    private List<HandHistory> handHistories = new ArrayList<>();

    public List<HandHistory> getHandHistories() {
        return handHistories;
    }

    public void setHandHistories(List<HandHistory> handHistories) {
        this.handHistories = handHistories;
    }
}
