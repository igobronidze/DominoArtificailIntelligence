package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.table.IPPossMoveTile;
import ge.ai.domino.imageprocessing.service.table.IPTile;
import org.junit.Assert;

import java.util.List;

public class TilesDetectorHelper {

    public static void assertPossMoveTiles(List<IPPossMoveTile> expectedTiles, List<IPPossMoveTile> realTiles) {
        Assert.assertEquals("Poss move tiles quantity", expectedTiles.size(), realTiles.size());
        for (int i = 0; i < expectedTiles.size(); i++) {
            assertPossMoveTile(expectedTiles.get(i), realTiles.get(i));
        }
    }

    public static void assertIPTiles(List<IPTile> expectedMyTiles, List<IPTile> realMyTiles) {
        Assert.assertEquals("My tiles quantity", expectedMyTiles.size(), realMyTiles.size());
        for (int i = 0; i < expectedMyTiles.size(); i++) {
            assertTiles(expectedMyTiles.get(i), realMyTiles.get(i));
        }
    }

    private static void assertTiles(IPTile expectedTile, IPTile realTile) {
        Assert.assertEquals("Tile left side", expectedTile.getLeft(), realTile.getLeft());
        Assert.assertEquals("Tile right side", expectedTile.getRight(), realTile.getRight());
        Assert.assertEquals("Tile top-left point", expectedTile.getTopLeft(), realTile.getTopLeft());
        Assert.assertEquals("Tile bottom-right point", expectedTile.getBottomRight(), realTile.getBottomRight());
    }

    private static void assertPossMoveTile(IPPossMoveTile expected, IPPossMoveTile real) {
        Assert.assertEquals("Poss move tile top-left point", expected.getTopLeft(), real.getTopLeft());
        Assert.assertEquals("Poss move tile bottom-right point", expected.getBottomRight(), real.getBottomRight());
    }
}
