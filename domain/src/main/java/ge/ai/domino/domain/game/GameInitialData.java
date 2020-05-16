package ge.ai.domino.domain.game;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GameInitialData implements Serializable {

    private int gameId;

    private int pointsForWin;
}
