package ge.ai.domino.domain.game.opponentplay;

import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.MoveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpponentPlay {

    private int id;

    private int gameId;

    private String version;

    private MoveType moveType;

    private Tile tile;

    private OpponentTilesWrapper opponentTiles;

    private List<Integer> possiblePlayNumbers = new ArrayList<>();
}
