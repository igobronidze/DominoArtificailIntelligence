package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import org.junit.Test;

import java.util.List;

public class TilesDetectorRunner {

	private static final String SRC_IMAGE_PATH_1 = "test_images/src/domino_l.png";

	@Test
	public void test() throws Exception {
		TilesDetector tilesDetector = new TilesDetector();
		List<Tile> tiles = tilesDetector.getTiles(SRC_IMAGE_PATH_1);
	}
}
