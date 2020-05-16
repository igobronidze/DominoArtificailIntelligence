package ge.ai.domino.domain.game.opponentplay;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GroupedOpponentPlay {

    private int gameId;

    private String version;

    private Map<String, Double> averageGuess = new HashMap<>();
}
