//package ge.ai.domino.imageprocessing;
//
//import org.junit.Assert;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public abstract class TilesDetectorTest {
//
//	protected static final String IMAGE_EXTENSION = ".png";
//
//	protected final Map<Integer, List<TileContour>> expectedTilesMap = new HashMap<>();
//
//	private final TilesDetector tilesDetector = new TilesDetector();
//
//	private final Map<Integer, BufferedImage> imagesMap = new HashMap<>();
//
//	protected void innerTestGetTiles() {
//		for (Map.Entry<Integer, BufferedImage> entry : imagesMap.entrySet()) {
//			List<TileContour> resultTileContours = tilesDetector.getTiles(entry.getValue(), getTilesDetectorParams());
//			List<TileContour> expectedTileContours = expectedTilesMap.get(entry.getKey());
//			Assert.assertEquals(expectedTileContours.size(), resultTileContours.size());
//			for (int i = 0; i < expectedTileContours.size() ; i++) {
//				Assert.assertEquals("Problem occurred for image " + entry.getKey(), expectedTileContours.get(i), resultTileContours.get(i));
//			}
//		}
//	}
//
//	protected void initImagesMap(String pathSuffix) throws IOException {
//		for (int i = 1; i <= expectedTilesMap.size(); i++) {
//			BufferedImage img = ImageIO.read(new File(pathSuffix + i + IMAGE_EXTENSION));
//			imagesMap.put(i, img);
//		}
//	}
//
//	protected abstract TilesDetectorParams getTilesDetectorParams();
//}
