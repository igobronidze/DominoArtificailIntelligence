package ge.ai.domino.console.ui.game;

public class GamePaneInitialData {

    private boolean bestMoveAutoPlay;

    private boolean detectAddedTiles;

    public boolean isBestMoveAutoPlay() {
        return bestMoveAutoPlay;
    }

    public void setBestMoveAutoPlay(boolean bestMoveAutoPlay) {
        this.bestMoveAutoPlay = bestMoveAutoPlay;
    }

    public boolean isDetectAddedTiles() {
        return detectAddedTiles;
    }

    public void setDetectAddedTiles(boolean detectAddedTiles) {
        this.detectAddedTiles = detectAddedTiles;
    }
}
