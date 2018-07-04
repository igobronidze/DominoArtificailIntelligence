package ge.ai.domino.service.imageprocessing;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.manager.imageprocessing.TilesDetectorManager;

import java.util.List;

public class TilesDetectorServiceImpl implements TilesDetectorService {

    private final TilesDetectorManager tilesDetectorManager = new TilesDetectorManager();

    @Override
    public List<Tile> detectTiles(int gameId) throws DAIException {
        return tilesDetectorManager.detectTiles(gameId);
    }
}
