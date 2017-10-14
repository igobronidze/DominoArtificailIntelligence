package ge.ai.domino.domain.domino.played;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayDeque;
import java.util.Deque;

@XmlType(name = "HandHistory")
public class HandHistory {

    private Deque<Turn> turns = new ArrayDeque<>();

    public Deque<Turn> getTurns() {
        return turns;
    }

    public void setTurns(Deque<Turn> turns) {
        this.turns = turns;
    }
}
