package ge.ai.domino.domain.game;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GameInfo implements Serializable {

    private int gameId;

    private int myPoint;

    private int opponentPoint;

    private boolean finished;
}
