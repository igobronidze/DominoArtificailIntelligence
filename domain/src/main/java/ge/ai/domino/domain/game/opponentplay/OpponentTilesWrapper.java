package ge.ai.domino.domain.game.opponentplay;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "OpponentTilesWrapper")
public class OpponentTilesWrapper {

    private List<OpponentTile> opponentTiles = new ArrayList<>();
}
