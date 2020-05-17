package ge.ai.domino.manager;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "PossibleMovesWrapper")
public class PossibleMovesWrapper {

    private List<PossibleMoves> possibleMoves = new ArrayList<>();
}
