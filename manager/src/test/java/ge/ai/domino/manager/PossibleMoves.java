package ge.ai.domino.manager;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlType(name = "PossibleMoves")
public class PossibleMoves {

    private int index;

    private List<Move> moves = new ArrayList<>();
}
