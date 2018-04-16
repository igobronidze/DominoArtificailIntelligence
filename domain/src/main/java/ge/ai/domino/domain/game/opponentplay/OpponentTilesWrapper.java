package ge.ai.domino.domain.game.opponentplay;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "OpponentTilesWrapper")
public class OpponentTilesWrapper {

    private List<OpponentTile> opponentTiles = new ArrayList<>();

    public List<OpponentTile> getOpponentTiles() {
        return opponentTiles;
    }

    public void setOpponentTiles(List<OpponentTile> opponentTiles) {
        this.opponentTiles = opponentTiles;
    }
}
