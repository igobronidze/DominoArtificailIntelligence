package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import org.junit.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TilesDetectorTest {

	protected static final String IMAGE_EXTENSION = ".png";

	protected final Map<Integer, List<Tile>> expectedTilesMap = new HashMap<>();

	private final TilesDetector tilesDetector = new TilesDetector();

	private final Map<Integer, BufferedImage> imagesMap = new HashMap<>();

	protected void innerTestGetTiles() {
		for (Map.Entry<Integer, BufferedImage> entry : imagesMap.entrySet()) {
			List<Tile> resultTiles = tilesDetector.getTiles(entry.getValue(), getTilesDetectorParams());
			List<Tile> expectedTiles = expectedTilesMap.get(entry.getKey());
			Assert.assertEquals(expectedTiles.size(), resultTiles.size());
			for (int i = 0; i < expectedTiles.size() ; i++) {
				Assert.assertEquals("Problem occurred for image " + entry.getKey(), expectedTiles.get(i), resultTiles.get(i));
			}
		}
	}

	protected void initImagesMap(String pathSuffix) throws IOException {
		for (int i = 1; i <= expectedTilesMap.size(); i++) {
			BufferedImage img = ImageIO.read(new File(pathSuffix + i + IMAGE_EXTENSION));
			imagesMap.put(i, img);
		}
	}

	protected abstract TilesDetectorParams getTilesDetectorParams();
}
