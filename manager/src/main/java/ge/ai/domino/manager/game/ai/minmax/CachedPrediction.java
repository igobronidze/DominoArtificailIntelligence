package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.helper.play.MoveHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CachedPrediction {

    private Map<Move, CachedPrediction> children = new HashMap<>();

    private Move move;

    private double heuristicValue;

    public static CachedPrediction getCachedPrediction(NodeRound nodeRound, int height) {
        CachedPrediction cachedPrediction = new CachedPrediction();
        cachedPrediction.setHeuristicValue(nodeRound.getHeuristic());
        cachedPrediction.setMove(MoveHelper.getMove(nodeRound.getLastPlayedMove()));

        if (height != 0) {
            for (NodeRound child : nodeRound.getChildren()) {
                cachedPrediction.getChildren().putIfAbsent(MoveHelper.getMove(child.getLastPlayedMove()), getCachedPrediction(child, height - 1));
            }
        }
        return cachedPrediction;
    }
}
