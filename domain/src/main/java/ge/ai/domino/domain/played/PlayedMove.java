package ge.ai.domino.domain.played;

import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@XmlType(name = "PlayedMove")
public class PlayedMove {

    private MoveType type;

    private MoveDirection direction;

    private int left;

    private int right;

    private SkipRoundInfo skipRoundInfo;

    @Override
    public String toString() {
        return  "type=" + (type == null ? "N" : type) +
                ", direction=" + (direction == null ? "N" : direction) +
                ", left=" + left +
                ", right=" + right +
                ((skipRoundInfo == null) ? "" : ", " + skipRoundInfo.toString());
    }
}
