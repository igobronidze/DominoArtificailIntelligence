package ge.ai.domino.domain.game.opponentplay;

import java.util.HashMap;
import java.util.Map;

public class GroupedOpponentPlay {

    private int gameId;

    private String version;

    private Map<String, Double> averageGuess = new HashMap<>();

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Double> getAverageGuess() {
        return averageGuess;
    }

    public void setAverageGuess(Map<String, Double> averageGuess) {
        this.averageGuess = averageGuess;
    }
}
