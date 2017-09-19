package ge.ai.domino.server.manager.domino.processor;

import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.domino.TileOwner;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.util.tile.TileUtil;

import java.util.HashSet;
import java.util.Set;

public abstract class TurnProcessor {

    public abstract Hand addTile(Hand hand, int x, int y);

    public abstract Hand play(Hand hand, int x, int y, PlayDirection direction);

    @SuppressWarnings("Duplicates")
    protected void makeDoubleTilesAsInBazaar(Hand hand, int a) {
        double himSum = 0.0;
        double bazaarSum = 0.0;
        Set<String> mayHaveTiles = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (tile.getX() == tile.getY() && tile.getX() > a) {
                himSum += tile.getHim();
                bazaarSum += (1.0 - tile.getBazaar());
                tile.setHim(0);
                tile.setMe(0);
                tile.setBazaar(1.0);
            } else {
                mayHaveTiles.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
            }
        }
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), mayHaveTiles, himSum, TileOwner.HIM);
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), mayHaveTiles, -1 * bazaarSum, TileOwner.BAZAAR);
    }
}
