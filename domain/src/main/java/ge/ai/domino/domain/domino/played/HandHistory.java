package ge.ai.domino.domain.domino.played;

import java.util.ArrayList;
import java.util.List;

public class HandHistory {

    private List<Turn> turns = new ArrayList<>();

    public List<Turn> getTurns() {
        return turns;
    }

    public void setTurns(List<Turn> turns) {
        this.turns = turns;
    }
}
