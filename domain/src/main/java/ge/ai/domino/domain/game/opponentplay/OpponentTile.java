package ge.ai.domino.domain.game.opponentplay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlType(name = "OpponentTile")
public class OpponentTile {

    private int left;

    private int right;

    private double probability;
}
