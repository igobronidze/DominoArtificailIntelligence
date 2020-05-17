package ge.ai.domino.manager;

import ge.ai.domino.domain.move.MoveDirection;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@XmlType(name = "Move")
public class Move {

    private int left;

    private int right;

    private MoveDirection direction;

    @Override
    public String toString() {
        return left + "-" + right + " " + direction;
    }
}
