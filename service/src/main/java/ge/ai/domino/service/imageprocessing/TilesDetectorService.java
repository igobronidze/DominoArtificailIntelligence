package ge.ai.domino.service.imageprocessing;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Tile;

import java.util.List;

public interface TilesDetectorService {

    List<Tile> detectTiles(int gameId) throws DAIException;
}
