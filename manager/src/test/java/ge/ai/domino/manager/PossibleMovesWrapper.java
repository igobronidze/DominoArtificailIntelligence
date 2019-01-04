package ge.ai.domino.manager;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "PossibleMovesWrapper")
public class PossibleMovesWrapper {

    private List<PossibleMoves> possibleMoves = new ArrayList<>();

    public List<PossibleMoves> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(List<PossibleMoves> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }
}
