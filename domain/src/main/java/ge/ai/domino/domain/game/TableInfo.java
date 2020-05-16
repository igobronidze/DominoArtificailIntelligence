package ge.ai.domino.domain.game;

import ge.ai.domino.domain.played.PlayedTile;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TableInfo implements Serializable {

    private PlayedTile top;

    private PlayedTile right;

    private PlayedTile bottom;

    private PlayedTile left;

    private boolean withCenter;

    private boolean myMove;

    private boolean firstRound;

    private RoundBlockingInfo roundBlockingInfo = new RoundBlockingInfo();

    private double opponentTilesCount;

    private double bazaarTilesCount;

    private int tilesFromBazaar;
}
