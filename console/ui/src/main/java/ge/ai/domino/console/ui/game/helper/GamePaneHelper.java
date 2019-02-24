package ge.ai.domino.console.ui.game.helper;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;

import java.util.ArrayList;
import java.util.List;

public class GamePaneHelper {

    public static List<AiPrediction> getAiPredictionByTile(List<AiPrediction> aiPredictions, Tile tile) {
        List<AiPrediction> result = new ArrayList<>();
        if (aiPredictions == null) {
            return result;
        }
        for (AiPrediction aiPrediction : aiPredictions) {
            if (aiPrediction.getMove().getLeft() == tile.getLeft() && aiPrediction.getMove().getRight() == tile.getRight()) {
                result.add(aiPrediction);
            }
        }
        return result;
    }

    public static Tile getHighestTile() {
        for (int i = 6; i >= 0; i--) {
            Tile tile = new Tile(i, i);
            if (AppController.round.getMyTiles().contains(tile)) {
                return tile;
            }
        }
        for (int i = 6; i >= 0; i--) {
            for (int j = i -1; j >= 0; j--) {
                Tile tile = new Tile(i, j);
                if (AppController.round.getMyTiles().contains(tile)) {
                    return tile;
                }
            }
        }
        return new Tile(0, 0);
    }
}
