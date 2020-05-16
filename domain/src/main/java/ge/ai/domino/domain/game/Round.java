package ge.ai.domino.domain.game;

import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Round implements Serializable {

    private Set<Tile> myTiles = new HashSet<>();

    private Map<Tile, Double> opponentTiles = new HashMap<>();

    private AiPredictionsWrapper aiPredictions = new AiPredictionsWrapper();

    private TableInfo tableInfo;

    private GameInfo gameInfo;

    private String warnMsgKey;
}
