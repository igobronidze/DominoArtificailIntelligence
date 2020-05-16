package ge.ai.domino.domain.game;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RoundBlockingInfo implements Serializable {

    private boolean omitMe;

    private boolean omitOpponent;

    private boolean lastNotTwinPlayedTileMy;
}
