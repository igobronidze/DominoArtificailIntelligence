package ge.ai.domino.imageprocessing.recognizer;

import ge.ai.domino.imageprocessing.service.table.IPPossMovesAndCenter;
import ge.ai.domino.imageprocessing.service.table.IPRectangle;
import ge.ai.domino.imageprocessing.service.table.IPTile;
import org.junit.Assert;

import java.util.List;

public class TilesDetectorHelper {

    public static void assertPossMoveTilesAndCenter(IPPossMovesAndCenter expected, IPPossMovesAndCenter real) {
        Assert.assertEquals("Poss move tiles quantity", expected.getPossMoves().size(), real.getPossMoves().size());
        for (int i = 0; i < expected.getPossMoves().size(); i++) {
            assertIPRectangle(expected.getPossMoves().get(i), real.getPossMoves().get(i));
        }
        assertIPRectangle(expected.getCenter(), real.getCenter());
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

    private static void assertIPRectangle(IPRectangle expected, IPRectangle real) {
        if (expected == null && real == null) {
            return;
        }
        Assert.assertNotNull(expected);
        Assert.assertNotNull(real);
        Assert.assertEquals("IP rectangle tile top-left point", expected.getTopLeft(), real.getTopLeft());
        Assert.assertEquals("IP rectangle tile bottom-right point", expected.getBottomRight(), real.getBottomRight());
    }
}
